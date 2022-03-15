package wang.ismy.fttp.sdk.dto;

import lombok.Data;

/**
 * @Title: FttpEndpointConfig
 * @description:
 * @author: cjiping@linewell.com
 * @since: 2022年03月15日 14:29
 */
@Data
public class FttpEndpointConfig {
    private String requestFtpDatasource;
    private String responseFtpDatasource;
}
