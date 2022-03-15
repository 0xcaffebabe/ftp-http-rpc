package wang.ismy.fttp.sdk.discovery;

import wang.ismy.fttp.sdk.dto.FttpEndpoint;
import wang.ismy.fttp.sdk.dto.FttpEndpointConfig;
import java.util.Collections;
import java.util.List;

/**
 * @Title: FttpEndpointDiscovery
 * @description:
 * @author: cjiping@linewell.com
 * @since: 2022年03月15日 14:32
 */
public class FttpEndpointDiscovery {

    public static List<FttpEndpoint> lookupLocal() {
        FttpEndpoint endpoint = new FttpEndpoint();
        endpoint.setId("local");
        endpoint.setUrl("http://127.0.0.1:11945");
        FttpEndpointConfig config = new FttpEndpointConfig();
        config.setRequestFtpDatasource("request");
        config.setResponseFtpDatasource("response");
        endpoint.setConfig(config);
        return Collections.singletonList(endpoint);
    }

    public static FttpEndpoint lookup(String id) {
        // todo
        return lookupLocal().stream().findFirst().orElseThrow(() -> new RuntimeException("没有找到指定的FTTP端点"));
    }

    public static List<FttpEndpoint> lookupRemote(String discoveryEndpointUrl) {
        // todo
        return Collections.emptyList();
    }
}
