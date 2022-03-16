package wang.ismy.fttp.endpoint.processor;

import cn.hutool.core.io.IoUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import wang.ismy.fttp.endpoint.WithoutSSLFactory;
import wang.ismy.fttp.endpoint.util.HttpClientFactory;
import wang.ismy.fttp.sdk.dto.FttpRequest;
import wang.ismy.fttp.sdk.dto.FttpResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashMap;

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

    public FttpResponse process(FttpRequest request) {
        CloseableHttpClient httpClient = HttpClientFactory.createHttpClient();
        HttpRequestBase httpRequest = null;
        String method = request.getInvokeMethod().toUpperCase();
        switch (method) {
            case "GET":
                httpRequest = new HttpGet(request.getInvokeUrl());
                break;
            case "POST":
                HttpPost post = new HttpPost(request.getInvokeUrl());
                if (StringUtils.hasLength(request.getInvokeBody())) {
                    byte[] requestBodyArray = Base64.getDecoder().decode(request.getInvokeBody());
                    post.setEntity(new ByteArrayEntity(requestBodyArray));
                }
                httpRequest = post;
                break;
            default:
                throw new IllegalArgumentException("未知的请求类型 " + method);
        }

        if (!CollectionUtils.isEmpty(request.getInvokeHeaders())) {
            for (String key : request.getInvokeHeaders().keySet()) {
                httpRequest.setHeader(key, request.getInvokeHeaders().get(key));
            }
        }
        try {
            CloseableHttpResponse httpResponse = httpClient.execute(httpRequest);
            byte[] responseBodyArray = getBinaryContent(httpResponse);
            return FttpResponse.builder()
                    .requestId(request.getRequestId())
                    .sourceEndpoint(request.getSourceEndpoint())
                    .targetEndpoint(request.getTargetEndpoint())
                    .code(httpResponse.getStatusLine().getStatusCode())
                    .invokeBody(Base64.getEncoder().encodeToString(responseBodyArray))
                    .message("success")
                    .invokeHeaders(getHeaders(httpResponse))
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
            return FttpResponse.builder()
                    .requestId(request.getRequestId())
                    .sourceEndpoint(request.getSourceEndpoint())
                    .targetEndpoint(request.getTargetEndpoint())
                    .code(-1)
                    .message(e.getMessage())
                    .build();
        }
    }

    private LinkedHashMap<String, String> getHeaders(HttpResponse response) {
        Header[] allHeaders = response.getAllHeaders();
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        for (Header header : allHeaders) {
            headers.put(header.getName(), header.getValue());
        }
        return headers;
    }

    private byte[] getBinaryContent(HttpResponse response) throws IOException {
        try {
            return IoUtil.readBytes(response.getEntity().getContent());
        }finally {
            response.getEntity().getContent().close();
        }
    }

    private RestTemplate getRestTemplate(){
        return new RestTemplateBuilder()
                .requestFactory(WithoutSSLFactory.class)
                .build();
    }
}
