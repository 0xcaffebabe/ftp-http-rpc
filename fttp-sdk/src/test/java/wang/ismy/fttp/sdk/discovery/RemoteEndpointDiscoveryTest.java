package wang.ismy.fttp.sdk.discovery;

import org.junit.jupiter.api.Test;
import wang.ismy.fttp.sdk.dto.FttpEndpoint;

import static org.junit.jupiter.api.Assertions.*;

class RemoteEndpointDiscoveryTest {

    @Test
    public void test() {
        RemoteEndpointDiscovery discovery = new RemoteEndpointDiscovery("http://127.0.0.1:11945");
        System.out.println(discovery.lookup("gaw"));
        System.out.println(discovery.lookup());
    }
}