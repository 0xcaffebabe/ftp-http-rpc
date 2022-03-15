package wang.ismy.fttp.endpoint.util;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author : wubin
 * @version : 0.0.1
 * PROJECT : learning
 * PACKAGE.CLASS : com.alibaba.aic.wb.redisstructuralcoverage.constants.SpringConst
 * DATE : 2020-04-01 11:28
 * DESCRIPTION :
 */
@Component
public class SpringConst {

    public static Environment env;

    @Resource
    public void setEnv(Environment env) {
        SpringConst.env = env;
    }

}
