package wang.ismy.fttp.endpoint.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wang.ismy.fttp.endpoint.discovery.DistributedDiscovery;
import wang.ismy.fttp.sdk.dto.FttpEndpoint;

import java.util.List;

/**
 * @Title: LocalDiscoveryEndpoint
 * @description:
 * @author: cjiping@linewell.com
 * @since: 2022年03月16日 11:44
 */
@RestController
@RequestMapping("discovery/distributed")
public class DistributedDiscoveryEndpoint {

    @Autowired
    private DistributedDiscovery discovery;

    @GetMapping("list")
    public List<FttpEndpoint> list() {
        return discovery.lookup();
    }

    @GetMapping("id/{id}")
    public FttpEndpoint getById(@PathVariable("id") String id) {
        return discovery.lookup(id);
    }
}
