package wang.ismy.fttp.endpoint.worker;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import wang.ismy.fttp.endpoint.discovery.DiscoveryEndpointsHolder;
import wang.ismy.fttp.endpoint.discovery.DistributedDiscovery;
import wang.ismy.fttp.endpoint.dto.EndpointInstance;
import wang.ismy.fttp.endpoint.ftp.FtpDatasourceHolder;
import wang.ismy.fttp.endpoint.ftp.FtpTransferService;
import wang.ismy.fttp.endpoint.util.ThreadPoolUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Title: DistributedDiscoveryFetcher
 * @description:
 * @author: cjiping@linewell.com
 * @since: 2022年03月18日 14:22
 */
@Component
@Slf4j
@ConditionalOnProperty(value = "discovery.push.enable", havingValue = "true")
public class DistributedDiscoveryFetcher {

    @Autowired
    private DistributedDiscovery distributedDiscovery;

    @Autowired
    private FtpDatasourceHolder ftpDatasourceHolder;

    @Autowired
    private FtpTransferService ftpTransferService;

    @Scheduled(fixedRateString = "${discovery.poll.interval}")
    public void fetch() {
        ArrayList<String> datasourceList = new ArrayList<>(ftpDatasourceHolder.getDatasource().keySet());
        for (String datasource : datasourceList) {
            ThreadPoolUtils.SHORT_LIFE_POOL.execute(() -> {
                List<String> endpoints = ftpTransferService.listAll(datasource, "endpoints");
                for (String endpoint : endpoints) {
                    byte[] bytes = ftpTransferService.download(datasource, endpoint);
                    EndpointInstance instance = JSONObject.parseObject(new String(bytes), EndpointInstance.class);
                    distributedDiscovery.offer(instance);
                }
            });
        }
    }

    @Scheduled(fixedRateString = "${discovery.poll.evictionInterval}")
    public void eviction() {
        distributedDiscovery.eviction();
    }
}
