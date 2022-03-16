package wang.ismy.fttp.endpoint.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wang.ismy.fttp.endpoint.discovery.DiscoveryEndpointsHolder;
import wang.ismy.fttp.endpoint.discovery.LocalDiscovery;
import wang.ismy.fttp.sdk.dto.FttpEndpoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Title: DiscoveryEndpoint
 * @description:
 * @author: cjiping@linewell.com
 * @since: 2022年03月16日 11:44
 */
@RestController
@RequestMapping("discovery")
public class DiscoveryEndpoint {

    @Autowired
    private LocalDiscovery localDiscovery;

    @GetMapping("list")
    public List<FttpEndpoint> list() {
        return localDiscovery.lookup();
    }

    @GetMapping("id/{id}")
    public FttpEndpoint getById(@PathVariable("id") String id) {
        return localDiscovery.lookup(id);
    }
}
