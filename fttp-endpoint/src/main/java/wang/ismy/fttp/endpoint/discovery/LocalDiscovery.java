package wang.ismy.fttp.endpoint.discovery;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wang.ismy.fttp.endpoint.util.ConfigProper;
import wang.ismy.fttp.endpoint.util.SpringContextUtil;
import wang.ismy.fttp.sdk.dto.FttpEndpoint;
import wang.ismy.fttp.sdk.discovery.FttpEndpointDiscovery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Title: LocalDiscovery
 * @description:
 * @author: cjiping@linewell.com
 * @since: 2022年03月15日 17:54
 */
@Component
@Slf4j
public class LocalDiscovery implements FttpEndpointDiscovery{

    @Autowired
    private DiscoveryEndpointsHolder discoveryEndpointsHolder;

    public FttpEndpoint self() {
        FttpEndpoint self = discoveryEndpointsHolder.get(ConfigProper.getInstance().getString("discovery.self"));
        if (self == null) {
            throw new IllegalArgumentException("找不到 self");
        }
        return self;
    }

    public FttpEndpoint lookup(String id) {
        DiscoveryEndpointsHolder holder = SpringContextUtil.getBean(DiscoveryEndpointsHolder.class);
        FttpEndpoint endpoint = holder.get(id);
        if (endpoint == null) {
            throw new IllegalArgumentException("找不到 " + id);
        }
        return endpoint;
    }

    @Override
    public List<FttpEndpoint> lookup() {
        List<FttpEndpoint> endpointList = new ArrayList<>();
        for (Map.Entry<String, FttpEndpoint> entry : discoveryEndpointsHolder.getEndpoints().entrySet()) {
            entry.getValue().setId(entry.getKey());
            endpointList.add(entry.getValue());
        }
        return endpointList;
    }
}
