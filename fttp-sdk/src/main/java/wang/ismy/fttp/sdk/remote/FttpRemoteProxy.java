package wang.ismy.fttp.sdk.remote;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import wang.ismy.fttp.sdk.dto.FttpEndpoint;
import wang.ismy.fttp.sdk.dto.FttpRequest;
import wang.ismy.fttp.sdk.dto.FttpResponse;

import java.util.Base64;

/**
 * @Title: FttpRemoteProxy
 * @description:
 * @author: cjiping@linewell.com
 * @since: 2022年03月16日 9:29
 */
@Data
@AllArgsConstructor
public class FttpRemoteProxy {
    private FttpEndpoint sourceEndpoint;
    private FttpEndpoint targetEndpoint;

    public String get(String url) {
        FttpRequest request = FttpRequest.builder()
                .invokeUrl(url)
                .invokeMethod("GET")
                .sourceEndpoint(sourceEndpoint)
                .targetEndpoint(targetEndpoint)
                .build();
        FttpResponse response = new RestTemplate().postForObject(sourceEndpoint.getUrl() + "/invoke/request", request, FttpResponse.class);
        if (!StringUtils.hasLength(response.getInvokeBody())) {
            throw new RuntimeException(response.getMessage());
        }
        if (response.getCode() != null && response.getCode() < 0) {
            throw new RuntimeException(response.getMessage());
        }
        return new String(Base64.getDecoder().decode(response.getInvokeBody()));
    }


}
