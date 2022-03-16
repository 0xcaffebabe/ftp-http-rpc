package wang.ismy.fttp.endpoint.api;

import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import wang.ismy.fttp.endpoint.discovery.LocalDiscovery;
import wang.ismy.fttp.endpoint.WithoutSSLFactory;
import wang.ismy.fttp.endpoint.data.ResponseDispatcher;
import wang.ismy.fttp.endpoint.ftp.FtpTransferService;
import wang.ismy.fttp.sdk.dto.FttpEndpoint;
import wang.ismy.fttp.sdk.dto.FttpRequest;
import wang.ismy.fttp.sdk.dto.FttpResponse;
import wang.ismy.fttp.sdk.discovery.FttpEndpointDiscovery;

import java.util.UUID;

/**
 * @Title: ServiceInvokerEndpoint
 * @description:
 * @author: cjiping@linewell.com
 * @since: 2022年03月15日 17:14
 */
@RestController
@RequestMapping("invoke")
public class ServiceInvokerEndpoint {

    @Autowired
    private FtpTransferService ftpTransferService;

    @Autowired
    private ResponseDispatcher responseDispatcher;

    @Autowired
    private LocalDiscovery localDiscovery;

    /**
     * 发起一个简单的 get 请求
     * @param url 服务提供方url
     * @param targetEndpoint 目的端点id
     * @return
     * @throws InterruptedException
     */
    @GetMapping("get")
    public FttpResponse get(@RequestParam("url") String url, @RequestParam("targetEndpoint") String targetEndpoint) throws InterruptedException {
        FttpEndpoint endpoint = localDiscovery.lookup(targetEndpoint);
        FttpRequest request = packetSimpleGetRequest(url, endpoint);
        ftpTransferService.upload(endpoint.getConfig().getRequestFtpDatasource(),
                JSONUtil.toJsonStr(request));
        return responseDispatcher.await(request.getRequestId());
    }

    /**
     * 发起一个通用请求
     * @param fttpRequest 请求实体
     * @return
     * @throws InterruptedException
     */
    @PostMapping("request")
    public FttpResponse request(@RequestBody FttpRequest fttpRequest) throws InterruptedException {
        if (!StringUtils.hasLength(fttpRequest.getRequestId())) {
            fttpRequest.setRequestId(UUID.randomUUID().toString());
        }

        FttpEndpoint endpoint = localDiscovery.lookup(fttpRequest.getTargetEndpoint().getId());
        ftpTransferService.upload(endpoint.getConfig().getRequestFtpDatasource(),
                JSONUtil.toJsonStr(fttpRequest));
        return responseDispatcher.await(fttpRequest.getRequestId());
    }

    /**
     * 发起一个不等待响应的请求
     * @param fttpRequest
     * @return
     * @throws InterruptedException
     */
    @PostMapping("drop")
    public FttpResponse drop(@RequestBody FttpRequest fttpRequest) throws InterruptedException {
        // 置空掉请求ID
        fttpRequest.setRequestId("");

        FttpEndpoint endpoint = localDiscovery.lookup(fttpRequest.getTargetEndpoint().getId());
        ftpTransferService.upload(endpoint.getConfig().getRequestFtpDatasource(),
                JSONUtil.toJsonStr(fttpRequest));
        return FttpResponse.emptyResponse();
    }

    private FttpRequest packetSimpleGetRequest(String url, FttpEndpoint target) {
        FttpRequest request = new FttpRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setInvokeUrl(url);
        request.setInvokeMethod("GET");
        request.setSourceEndpoint(localDiscovery.self());
        request.setTargetEndpoint(target);
        return request;
    }

    private RestTemplate getRestTemplate(){
        return new RestTemplateBuilder()
                .requestFactory(WithoutSSLFactory.class)
                .build();
    }
}
