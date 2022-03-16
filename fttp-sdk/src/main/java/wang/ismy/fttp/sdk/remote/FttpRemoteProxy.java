package wang.ismy.fttp.sdk.remote;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.http.HttpEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import wang.ismy.fttp.sdk.dto.FttpEndpoint;
import wang.ismy.fttp.sdk.dto.FttpRequest;
import wang.ismy.fttp.sdk.dto.FttpResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.LinkedHashMap;

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

    /**
     * 执行一个 GET 请求
     * @param url
     * @return 文本数据
     */
    public String get(String url) {
        FttpRequest request = packetGetRequest(url);
        FttpResponse response = request(request);
        assertResponse(response);
        return new String(Base64.getDecoder().decode(response.getInvokeBody()));
    }

    /**
     * 执行一个 GET 请求
     * @param url
     * @return 二进制数据
     */
    public byte[] getRaw(String url) {
        FttpRequest request = packetGetRequest(url);
        FttpResponse response = request(request);
        assertResponse(response);
        return Base64.getDecoder().decode(response.getInvokeBody());
    }

    /**
     * 执行一个不等待返回的 GET 请求
     * @param url
     */
    public void dropGet(String url) {
        FttpRequest request = packetGetRequest(url);
        FttpResponse response = drop(request);
        assertResponse(response);
    }

    /**
     * post 盲转发 原封不动将body数组传递给服务提供者
     * @param url
     * @param headers
     * @param body
     * @return
     */
    public String postDumb(String url, LinkedHashMap<String, String> headers, byte[] body) {
        FttpRequest request = packetPostRequest(url, headers, body);
        FttpResponse response = request(request);
        assertResponse(response);
        return new String(Base64.getDecoder().decode(response.getInvokeBody()));
    }

    private FttpResponse request(FttpRequest request) {
        return new RestTemplate().postForObject(sourceEndpoint.getUrl() + "/invoke/request", request, FttpResponse.class);
    }

    private FttpResponse drop(FttpRequest request) {
        return new RestTemplate().postForObject(sourceEndpoint.getUrl() + "/invoke/drop", request, FttpResponse.class);
    }

    private void assertResponse(FttpResponse response) {
        if (!StringUtils.hasLength(response.getInvokeBody())) {
            throw new RuntimeException(response.getMessage());
        }
        if (response.getCode() != null && response.getCode() < 0) {
            throw new RuntimeException(response.getMessage());
        }
    }

    private FttpRequest packetGetRequest(String url) {
        FttpRequest request = FttpRequest.builder()
                .invokeUrl(url)
                .invokeMethod("GET")
                .sourceEndpoint(sourceEndpoint)
                .targetEndpoint(targetEndpoint)
                .build();
        return request;
    }

    private FttpRequest packetPostRequest(String url, LinkedHashMap<String, String> headers, byte[] body){
        return FttpRequest.builder()
                .invokeUrl(url)
                .invokeMethod("POST")
                .sourceEndpoint(sourceEndpoint)
                .targetEndpoint(targetEndpoint)
                .invokeHeaders(headers)
                .invokeBody(Base64.getEncoder().encodeToString(body))
                .build();
    }


}
