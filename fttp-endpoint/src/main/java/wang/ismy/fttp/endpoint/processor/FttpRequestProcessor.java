package wang.ismy.fttp.endpoint.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import wang.ismy.fttp.endpoint.WithoutSSLFactory;
import wang.ismy.fttp.sdk.dto.FttpRequest;
import wang.ismy.fttp.sdk.dto.FttpResponse;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * @Title: FttpRequestProcessor
 * @description:
 * @author: cjiping@linewell.com
 * @since: 2022年03月15日 19:20
 */
@Component
@Slf4j
public class FttpRequestProcessor {

    public FttpResponse simpleGetProcess(FttpRequest request) {
        String serviceProviderResponse = getRestTemplate().getForObject(request.getInvokeUrl(), String.class);
        FttpResponse response = new FttpResponse();
        response.setRequestId(request.getRequestId());
        response.setInvokeBody(Base64.getEncoder().encodeToString(serviceProviderResponse.getBytes(StandardCharsets.UTF_8)));
        return response;
    }

    private RestTemplate getRestTemplate(){
        return new RestTemplateBuilder()
                .requestFactory(WithoutSSLFactory.class)
                .build();
    }
}
