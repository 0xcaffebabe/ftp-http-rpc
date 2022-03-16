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
public interface FttpEndpointDiscovery {

    /**
     * 根据ID找到指定的端点
     * @param id
     * @return
     */
    FttpEndpoint lookup(String id);

    /**
     * 列出所有端点
     * @return
     */
    List<FttpEndpoint> lookup();
}
