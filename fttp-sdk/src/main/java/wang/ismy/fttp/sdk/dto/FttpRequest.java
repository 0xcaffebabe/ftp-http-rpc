package wang.ismy.fttp.sdk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;

/**
 * @Title: FttpRequest
 * @description:
 * @author: cjiping@linewell.com
 * @since: 2022年03月15日 14:24
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FttpRequest {
    /**
     * 唯一请求ID 用来匹配跨网请求响应封包
     */
    private String requestId;
    /**
     * 源端点 用来接收服务调用方请求
     */
    private FttpEndpoint sourceEndpoint;
    /**
     * 目标端点 用来对服务提供方发起请求端点
     */
    private FttpEndpoint targetEndpoint;

    private String invokeUrl;

    private String invokeMethod;

    private LinkedHashMap<String, String> invokeHeaders;

    /**
     * BASE64编码
     */
    private String invokeBody;


}
