package wang.ismy.fttp.endpoint.discovery;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import wang.ismy.fttp.sdk.dto.FttpEndpoint;

import java.util.Map;

/**
 * @Title: DiscoveryEndpointHolder
 * @description:
 * @author: cjiping@linewell.com
 * @since: 2022年03月15日 20:00
 */
@Configuration
@ConfigurationProperties("discovery")
@Slf4j
@Data
public class DiscoveryEndpointsHolder {
    private Map<String, FttpEndpoint> endpoints;

    public FttpEndpoint get(String id) {
        FttpEndpoint endpoint = endpoints.get(id);
        if (endpoint != null) {
            endpoint.setId(id);
        }
        return endpoint;
    }
}
