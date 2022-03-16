package wang.ismy.fttp.endpoint.worker;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import wang.ismy.fttp.endpoint.discovery.LocalDiscovery;
import wang.ismy.fttp.endpoint.ftp.FtpTransferService;
import wang.ismy.fttp.endpoint.processor.FttpRequestProcessor;
import wang.ismy.fttp.endpoint.util.ThreadPoolUtils;
import wang.ismy.fttp.sdk.dto.FttpRequest;
import wang.ismy.fttp.sdk.dto.FttpResponse;

import java.util.List;

/**
 * @Title: RequestListener
 * @description: 下载FTP里面的请求 进行代理 并返回响应
 * @author: cjiping@linewell.com
 * @since: 2022年03月15日 19:28
 */
@Component
@Slf4j
public class RequestListener {

    @Autowired
    private FtpTransferService ftpTransferService;

    @Autowired
    private FttpRequestProcessor requestProcessor;

    @Scheduled(fixedDelay = 5000)
    public void run() {
        String requestFtpDatasource = LocalDiscovery.self().getConfig().getRequestFtpDatasource();
        List<String> fileList = ftpTransferService.listAll(requestFtpDatasource);
        for (String file : fileList) {
            // 下载并请求 然后将结果回送到对应的FTP
            ThreadPoolUtils.SHORT_LIFE_POOL.execute(() -> {
                byte[] download = ftpTransferService.download(requestFtpDatasource, file);
                FttpRequest request = JSONObject.parseObject(download, FttpRequest.class);
                FttpResponse response;
                try {
                    response = requestProcessor.process(request);
                }catch (Exception e) {
                    e.printStackTrace();
                    response = FttpResponse.builder()
                            .requestId(request.getRequestId())
                            .sourceEndpoint(request.getSourceEndpoint())
                            .targetEndpoint(request.getTargetEndpoint())
                            .code(-1)
                            .message(e.getMessage())
                            .build();
                }
                ftpTransferService.deleteFile(requestFtpDatasource, file);
                String responseFtpDatasource = LocalDiscovery.lookup(request.getSourceEndpoint().getId()).getConfig().getResponseFtpDatasource();
                ftpTransferService.upload(responseFtpDatasource, JSONObject.toJSONString(response));
                log.info("请求信息交换 {}", file);
            });
        }
    }
}
