package wang.ismy.fttp.endpoint.data;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import wang.ismy.fttp.sdk.dto.FttpResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @Title: ResponseDispatcher
 * @description:
 * @author: cjiping@linewell.com
 * @since: 2022年03月15日 17:57
 */
@Component
@Slf4j
public class ResponseDispatcher {
    private Map<String, Exchanger<FttpResponse>> requestExchangeMap = new ConcurrentHashMap<>(16);

    public FttpResponse await(String requestId) throws InterruptedException {
        Exchanger<FttpResponse> exchanger = requestExchangeMap.computeIfAbsent(requestId, key -> new Exchanger<>());
        return exchanger.exchange(null);
    }

    public void dispatch(FttpResponse response) throws InterruptedException, TimeoutException {
        if (!StringUtils.hasLength(response.getRequestId())) {
            log.warn("接收到没有请求 ID 的响应 丢弃 {}", response);
            return;
        }
        try {
            Exchanger<FttpResponse> exchanger = requestExchangeMap.get(response.getRequestId());
            // 最多等待5秒 超过5秒还没人交换 丢弃
            exchanger.exchange(response, 5, TimeUnit.SECONDS);
        }finally {
            requestExchangeMap.remove(response.getRequestId());
        }
    }


}
