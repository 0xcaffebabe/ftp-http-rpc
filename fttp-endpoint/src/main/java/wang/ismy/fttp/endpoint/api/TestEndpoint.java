package wang.ismy.fttp.endpoint.api;

import cn.hutool.core.io.IoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @Title: TestEndpoint
 * @description:
 * @author: cjiping@linewell.com
 * @since: 2022年03月16日 11:02
 */
@RestController
@RequestMapping("test")
@Slf4j
public class TestEndpoint {

    @PostMapping("upload")
    public String upload(HttpServletRequest request) throws IOException {
        byte[] bytes = IoUtil.readBytes(request.getInputStream());
        return new String(bytes);
    }

    @GetMapping("drop")
    public String drop() {
        log.info("接收到一个 drop");
        return System.currentTimeMillis() + "";
    }
}
