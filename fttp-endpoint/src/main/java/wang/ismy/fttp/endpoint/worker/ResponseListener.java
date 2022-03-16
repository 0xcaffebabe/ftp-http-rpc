package wang.ismy.fttp.endpoint.worker;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import wang.ismy.fttp.endpoint.discovery.LocalDiscovery;
import wang.ismy.fttp.endpoint.data.ResponseDispatcher;
import wang.ismy.fttp.endpoint.ftp.FtpTransferService;
import wang.ismy.fttp.endpoint.util.ThreadPoolUtils;
import wang.ismy.fttp.sdk.dto.FttpResponse;

import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * @Title: ResponseListener
 * @description:
 * @author: cjiping@linewell.com
 * @since: 2022年03月15日 17:52
 */
@Component
@Slf4j
public class ResponseListener {

    @Autowired
    private FtpTransferService ftpTransferService;

    @Autowired
    private ResponseDispatcher responseDispatcher;

    @Scheduled(cron = "${listener.response.cron}")
    public void run() {
        String responseFtpDatasource = LocalDiscovery.self().getConfig().getResponseFtpDatasource();
        List<String> fileList = ftpTransferService.listAll(responseFtpDatasource);
        for (String file : fileList) {
            // 下载并分发
            ThreadPoolUtils.SHORT_LIFE_POOL.execute(() -> {
                byte[] download = ftpTransferService.download(responseFtpDatasource, file);
                FttpResponse response = JSONObject.parseObject(download, FttpResponse.class);
                try {
                    responseDispatcher.dispatch(response);
                } catch (InterruptedException | TimeoutException e) {
                    e.printStackTrace();
                }
                ftpTransferService.deleteFile(responseFtpDatasource, file);
                log.info("响应信息交换 {}", file);
            });
        }
    }
}
