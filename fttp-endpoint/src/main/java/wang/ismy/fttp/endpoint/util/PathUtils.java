package wang.ismy.fttp.endpoint.util;


import cn.hutool.core.util.ArrayUtil;
import org.springframework.util.StringUtils;

/**
 * @Title: PathUtils
 * @description:
 * @author: cjiping@linewell.com
 * @since: 2021年11月04日 9:56
 */
public class PathUtils {
    public static String concat(String separator, String...paths) {
        if (ArrayUtil.isEmpty(paths)) {
            return separator;
        }
        StringBuilder sb = new StringBuilder();
        for(int i = 0;i<paths.length;i++){
            String path = paths[i];
            if (!StringUtils.hasLength(path)) {
                continue;
            }
            if (path.endsWith(separator)) {
                sb.append(path, 0, path.length() - 1);
            }else if(path.startsWith(separator)) {
                sb.append(path, 1, path.length());
            }
            else {
                sb.append(path);
            }
            if (i != paths.length - 1) {
                sb.append(separator);
            }
        }
        return sb.toString();
    }
}
