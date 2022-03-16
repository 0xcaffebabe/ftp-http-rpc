package wang.ismy.fttp.sdk.discovery;

import lombok.AllArgsConstructor;
import org.springframework.web.client.RestTemplate;
import wang.ismy.fttp.sdk.dto.FttpEndpoint;

import javax.xml.stream.events.EndDocument;
import java.util.List;

/**
 * @Title: RemoteEndpointDiscovery
 * @description:
 * @author: cjiping@linewell.com
 * @since: 2022年03月16日 11:42
 */
@AllArgsConstructor
public class RemoteEndpointDiscovery implements FttpEndpointDiscovery{

    private String remoteEndpoint;

    @Override
    public FttpEndpoint lookup(String id) {
        FttpEndpoint endpoint = new RestTemplate().getForObject(remoteEndpoint + "/discovery/id/" + id, FttpEndpoint.class);
        if (endpoint == null) {
            throw new RuntimeException("找不到端点 " + id);
        }
        return endpoint;
    }

    @Override
    public List<FttpEndpoint> lookup() {
        return new RestTemplate().getForObject(remoteEndpoint + "/discovery/list", List.class);
    }
}
