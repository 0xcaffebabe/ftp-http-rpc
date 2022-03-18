package wang.ismy.fttp.endpoint.dto;

import lombok.Data;
import wang.ismy.fttp.sdk.dto.FttpEndpoint;

import java.time.LocalDateTime;

/**
 * @Title: EndpointInstance
 * @description:
 * @author: cjiping@linewell.com
 * @since: 2022年03月18日 13:55
 */
@Data
public class EndpointInstance {
    private FttpEndpoint endpoint;
    private LocalDateTime lastUpdate;

    public static EndpointInstance of(FttpEndpoint endpoint) {
        EndpointInstance endpointInstance = new EndpointInstance();
        endpointInstance.endpoint = endpoint;
        endpointInstance.lastUpdate = LocalDateTime.now();
        return endpointInstance;
    }
}
