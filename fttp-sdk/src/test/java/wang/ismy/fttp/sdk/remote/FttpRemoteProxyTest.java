package wang.ismy.fttp.sdk.remote;

import cn.hutool.core.io.IoUtil;
import org.junit.jupiter.api.Test;
import wang.ismy.fttp.sdk.dto.FttpEndpoint;

import java.io.*;
import java.util.LinkedHashMap;

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
    public void testGetImage() throws IOException {
        FttpRemoteProxy proxy = new FttpRemoteProxy(source(), target());
        byte[] response = proxy.getRaw("https://www.baidu.com/img/PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png");
        FileOutputStream fos = new FileOutputStream("test.png");
        fos.write(response);
        fos.flush();
        fos.close();
    }


    @Test
    public void testGetGoogle() {
        FttpRemoteProxy proxy = new FttpRemoteProxy(source(), target());
        String response = proxy.get("http://google.com");
        System.out.println(response);
    }

    @Test
    public void testPost() throws IOException {
        FttpRemoteProxy proxy = new FttpRemoteProxy(source(), target());
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        String response = proxy.postDumb("http://127.0.0.1:11945/test/upload", headers, IoUtil.readBytes(new FileInputStream("pom.xml")));
        System.out.println(response);
    }

    @Test
    public void testDrop(){
        FttpRemoteProxy proxy = new FttpRemoteProxy(source(), target());
        proxy.dropGet("http://127.0.0.1:11945/test/drop");
    }

}