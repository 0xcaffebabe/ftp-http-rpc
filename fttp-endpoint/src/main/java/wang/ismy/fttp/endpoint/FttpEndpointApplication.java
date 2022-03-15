package wang.ismy.fttp.endpoint;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author MY
 */
@SpringBootApplication
@EnableScheduling
@EnableAsync
public class FttpEndpointApplication {

    public static void main(String[] args) {
        SpringApplication.run(FttpEndpointApplication.class, args);
    }

}
