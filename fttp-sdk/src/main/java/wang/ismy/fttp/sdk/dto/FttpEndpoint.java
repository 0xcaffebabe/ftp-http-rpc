package wang.ismy.fttp.sdk.dto;

import lombok.Data;

/**
 * @Title: FttpEndpoint
 * @description:
 * @author: cjiping@linewell.com
 * @since: 2022年03月15日 14:26
 */
@Data
public class FttpEndpoint {
    private String id;
    private String url;
    private FttpEndpointConfig config;
}
