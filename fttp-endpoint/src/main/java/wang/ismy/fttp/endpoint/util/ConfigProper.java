
package wang.ismy.fttp.endpoint.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 *
 * @description: 方便获取配置文件项
 * @author: cjiping@linewell.com
 * @since: 2021年4月2日 下午5:53:41
 */
@Component
@Slf4j
public class ConfigProper {

    private volatile static ConfigProper instance;

    private ConfigProper() {

    }

    public static ConfigProper getInstance() {
        if (instance == null) {
            synchronized (ConfigProper.class) {
                if (instance == null) {
                    instance = new ConfigProper();
                }
            }
        }
        return instance;
    }

    public String getString(String name) {
        return SpringConst.env.getProperty(name);
    }

    public String getString(String name, String defaultvalue) {
        return SpringConst.env.getProperty(name, defaultvalue);
    }

    public int getInt(String name) {
        String res = SpringConst.env.getProperty(name);
        res = res.trim();
        return Integer.parseInt(res);
    }

    public int getInt(String name, int defaultvalue) {
        String res = SpringConst.env.getProperty(name);
        return res != null ? Integer.parseInt(res.trim()) : defaultvalue;
    }

    public long getLong(String name) {
        String res = SpringConst.env.getProperty(name);
        res = res.trim();
        return Long.parseLong(res);
    }

    public long getLong(String name, long defaultvalue) {
        String res = SpringConst.env.getProperty(name);
        return res != null ? Long.parseLong(res.trim()) : defaultvalue;
    }

    public boolean getBoolean(String name, boolean defValue) {
        String res = SpringConst.env.getProperty(name);
        return (null == res || res.isEmpty()) ? defValue : Boolean.valueOf(res);
    }

}
