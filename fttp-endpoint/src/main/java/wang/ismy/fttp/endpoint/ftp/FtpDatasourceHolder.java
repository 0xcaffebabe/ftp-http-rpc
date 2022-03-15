package wang.ismy.fttp.endpoint.ftp;

import cn.hutool.extra.ftp.FtpConfig;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.Charset;
import java.util.Map;

/**
 * @Title: FtpDatasourceHolder
 * @description:
 * @author: cjiping@linewell.com
 * @since: 2021年11月03日 14:39
 */
@Configuration
@ConfigurationProperties("ftp")
@Slf4j
@Data
public class FtpDatasourceHolder {

    private Map<String, FtpDatasource> datasource;

    /**
     * 获取指定FTP数据源
     * @param name
     * @return 可能返回null
     */
    public FtpDatasource get(String name) {
        return datasource.get(name);
    }

    /**
     * 获取并转换成FTP配置
     * @param name
     * @return
     */
    public FtpConfig getAndWrap(String name) {
        FtpDatasource datasource = this.datasource.get(name);
        if (datasource == null) {
            return null;
        }
        return convert2Config(datasource);
    }

    public FtpConfig convert2Config(FtpDatasource datasource){
        return FtpConfig.create()
                .setHost(datasource.getHost())
                .setUser(datasource.getUsername())
                .setPort(datasource.getPort())
                .setConnectionTimeout(10 * 60 * 1000)
                .setSoTimeout(10 * 60 * 1000)
                .setPassword(datasource.getPassword())
                .setCharset(Charset.forName(datasource.getEncoding()));
    }
}

