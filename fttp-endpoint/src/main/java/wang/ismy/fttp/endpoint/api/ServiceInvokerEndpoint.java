package wang.ismy.fttp.endpoint.api;

import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
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

    @GetMapping("get")
    public FttpResponse invoke(@RequestParam("url") String url, @RequestParam("targetEndpoint") String targetEndpoint) throws InterruptedException {
        FttpEndpoint endpoint = LocalDiscovery.lookup(targetEndpoint);
        FttpRequest request = packetSimpleGetRequest(url, endpoint);
        ftpTransferService.upload(endpoint.getConfig().getRequestFtpDatasource(),
                JSONUtil.toJsonStr(request));
        return responseDispatcher.await(request.getRequestId());
    }

    private FttpRequest packetSimpleGetRequest(String url, FttpEndpoint target) {
        FttpRequest request = new FttpRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setInvokeUrl(url);
        request.setInvokeMethod("GET");
        // todo
        request.setSourceEndpoint(LocalDiscovery.self());
        request.setTargetEndpoint(target);
        return request;
    }

    private RestTemplate getRestTemplate(){
        return new RestTemplateBuilder()
                .requestFactory(WithoutSSLFactory.class)
                .build();
    }
}
