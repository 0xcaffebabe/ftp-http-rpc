package wang.ismy.fttp.sdk.remote;

import org.junit.jupiter.api.Test;
import wang.ismy.fttp.sdk.discovery.FttpEndpointDiscovery;
import wang.ismy.fttp.sdk.dto.FttpEndpoint;

import static org.junit.jupiter.api.Assertions.*;

class FttpRemoteProxyTest {

    private FttpEndpoint source() {
        FttpEndpoint endpoint = new FttpEndpoint();
        endpoint.setId("gaw");
        endpoint.setUrl("http://127.0.0.1:11945");
        return endpoint;
    }

    private FttpEndpoint target() {
        FttpEndpoint endpoint = new FttpEndpoint();
        endpoint.setId("zw");
        endpoint.setUrl("http://127.0.0.1:12945");
        return endpoint;
    }

    @Test
    public void testGetBaidu() {
        FttpRemoteProxy proxy = new FttpRemoteProxy(source(), target());
        String response = proxy.get("http://baidu.com");
        System.out.println(response);
    }

    @Test
    public void testGetGoogle() {
        FttpRemoteProxy proxy = new FttpRemoteProxy(source(), target());
        String response = proxy.get("http://google.com");
        System.out.println(response);
    }

}