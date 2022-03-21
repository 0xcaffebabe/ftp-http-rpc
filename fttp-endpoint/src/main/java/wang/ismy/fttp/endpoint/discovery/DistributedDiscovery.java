package wang.ismy.fttp.endpoint.discovery;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import wang.ismy.fttp.endpoint.dto.EndpointInstance;
import wang.ismy.fttp.sdk.discovery.FttpEndpointDiscovery;
import wang.ismy.fttp.sdk.dto.FttpEndpoint;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @Title: DistributedDiscovery
 * @description:
 * @author: cjiping@linewell.com
 * @since: 2022年03月18日 14:45
 */
@Component
@Slf4j
public class DistributedDiscovery implements FttpEndpointDiscovery {
    private Map<String, EndpointInstance> instanceMap = new ConcurrentHashMap<>(16);

    @Value("${discovery.poll.evictionInterval}")
    private Integer evictionInterval;

    @Override
    public FttpEndpoint lookup(String id) {
        EndpointInstance instance = instanceMap.get(id);
        if (instance == null) {
            return null;
        }
        return instance.getEndpoint();
    }

    @Override
    public List<FttpEndpoint> lookup() {
        return instanceMap.values().stream().map(EndpointInstance::getEndpoint).collect(Collectors.toList());
    }

    public void offer(EndpointInstance instance) {
        if (instance == null) {
            return;
        }
        if (instance.getEndpoint() == null) {
            return;
        }
        log.info("服务实例更新 {} {}", instance.getLastUpdate(), instance.getEndpoint().getId());
        instanceMap.put(instance.getEndpoint().getId(), instance);
    }

    public void eviction() {
        log.info("准备清除过期服务实例");
        int cnt = 0;
        for (String key : instanceMap.keySet()) {
            EndpointInstance instance = instanceMap.get(key);
            if (instance.getLastUpdate().plusSeconds(evictionInterval / 1000).isBefore(LocalDateTime.now())) {
                instanceMap.remove(key);
                cnt++;
            }
        }
        log.info("过期服务实例清除数 {}", cnt);
    }
}
