package wang.ismy.fttp.endpoint.ftp;

import lombok.Data;

/**
 * @Title: FtpDatasource
 * @description:
 * @author: cjiping@linewell.com
 * @since: 2021年11月03日 14:37
 */
@Data
public class FtpDatasource {
    private String name;
    private String host;
    private Integer port;
    private String username;
    private String password;
    /**
     * 编码
     */
    private String encoding;
    private String ftpDir;


    /**
     * 后备目录
     * 当文件消费完成之后：
     * 策略1：直接删除
     * 策略2：移动到后备目录
     */
    private String backFtpDir;
}
