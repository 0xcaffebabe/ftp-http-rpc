package wang.ismy.fttp.endpoint.worker;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import wang.ismy.fttp.endpoint.discovery.LocalDiscovery;
import wang.ismy.fttp.endpoint.dto.EndpointInstance;
import wang.ismy.fttp.endpoint.ftp.FtpDatasourceHolder;
import wang.ismy.fttp.endpoint.ftp.FtpTransferService;
import wang.ismy.fttp.endpoint.util.PathUtils;
import wang.ismy.fttp.sdk.dto.FttpEndpoint;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * @Title: DiscoveryBroadcast
 * @description: 定时广播自身信标
 * @author: cjiping@linewell.com
 * @since: 2022年03月18日 11:02
 */
@Component
@Slf4j
@ConditionalOnProperty(value = "discovery.push.enable", havingValue = "true")
public class SelfDiscoveryBroadcast {

    @Autowired
    private FtpDatasourceHolder ftpDatasourceHolder;

    @Autowired
    private FtpTransferService ftpTransferService;

    @Autowired
    private LocalDiscovery localDiscovery;

    @Scheduled(fixedRateString = "${discovery.push.interval}")
    public void run() {
        ArrayList<String> ftpDatasourceList = new ArrayList<>(ftpDatasourceHolder.getDatasource().keySet());
        FttpEndpoint self = localDiscovery.self();
        for (String datasource : ftpDatasourceList) {
            boolean success = ftpTransferService.upload(datasource,
                    PathUtils.concat("/", "endpoints", self.getId()),
                    JSONObject.toJSONString(EndpointInstance.of(self)).getBytes(StandardCharsets.UTF_8));
            log.info("服务注册至 {} 结果 {}", datasource, success);
        }
    }

}
