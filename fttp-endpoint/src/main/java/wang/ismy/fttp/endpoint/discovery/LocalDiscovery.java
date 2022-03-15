package wang.ismy.fttp.endpoint.discovery;

import wang.ismy.fttp.endpoint.util.ConfigProper;
import wang.ismy.fttp.endpoint.util.SpringContextUtil;
import wang.ismy.fttp.sdk.dto.FttpEndpoint;
import wang.ismy.fttp.sdk.discovery.FttpEndpointDiscovery;

/**
 * @Title: LocalDiscovery
 * @description:
 * @author: cjiping@linewell.com
 * @since: 2022年03月15日 17:54
 */
public class LocalDiscovery {

    public static FttpEndpoint self() {
        DiscoveryEndpointsHolder holder = SpringContextUtil.getBean(DiscoveryEndpointsHolder.class);
        FttpEndpoint self = holder.get(ConfigProper.getInstance().getString("discovery.self"));
        if (self == null) {
            throw new IllegalArgumentException("找不到 self");
        }
        return self;
    }

    public static FttpEndpoint lookup(String id) {
        DiscoveryEndpointsHolder holder = SpringContextUtil.getBean(DiscoveryEndpointsHolder.class);
        FttpEndpoint endpoint = holder.get(id);
        if (endpoint == null) {
            throw new IllegalArgumentException("找不到 " + id);
        }
        return endpoint;
    }
}
