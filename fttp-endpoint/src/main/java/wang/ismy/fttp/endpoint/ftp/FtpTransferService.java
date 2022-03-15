package wang.ismy.fttp.endpoint.ftp;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.extra.ftp.Ftp;
import cn.hutool.extra.ftp.FtpConfig;
import cn.hutool.extra.ftp.FtpMode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import wang.ismy.fttp.endpoint.util.PathUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Title: FtpTransferService
 * @description: FTP数据传输服务
 * @author: cjiping@linewell.com
 * @since: 2021年11月03日 14:30
 */
@Component
@Slf4j
public class FtpTransferService {

    @Autowired
    private FtpDatasourceHolder ftpDatasourceHolder;

    public static final DateTimeFormatter FILENAME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSS");

    /**
     * 上传文件到指定的FTP数据源
     * @param datasource
     * @param filename
     * @param content
     */
    public boolean upload(String datasource, String filename, byte[] content){
        FtpDatasource ftpDatasource = getFtpDatasource(datasource);
        String fullFilename = getFullFilename(filename, ftpDatasource.getFtpDir());
        return uploadFile(ftpDatasource, filename, content, fullFilename);
    }

    /**
     * 上传文件到FTP数据源后备目录
     * @param datasource
     * @param filename
     * @param content
     * @return
     */
    public boolean uploadToBack(String datasource, String filename, byte[] content) {
        FtpDatasource ftpDatasource = getFtpDatasource(datasource);
        String fullFilename = getFullFilename(filename, ftpDatasource.getBackFtpDir());
        return uploadFile(ftpDatasource, filename, content, fullFilename);
    }

    private boolean uploadFile(FtpDatasource ftpDatasource, String filename, byte[] content, String fullFilename) {
        try (Ftp ftp = buildFtpClient(ftpDatasource)){
            ensureFtpDirExists(ftp, fullFilename);
            log.info("准备上传文件 {} 到 FTP {}", fullFilename, ftpDatasource.getName());
            boolean success =  ftp.upload(getPath(fullFilename), getFilename(fullFilename), new ByteArrayInputStream(content));
            log.info("文件 {} 上传结果 {}",fullFilename, success);
            return success;
        } catch (Exception e) {
            log.error("数据源 {} 文件 {} 上传异常", ftpDatasource.getName(), filename, e);
            return false;
        }
    }


    /**
     * 上传指定文本文件到指定数据源 文件名使用时间序列
     * @param datasource
     * @param content
     * @return
     */
    public boolean upload(String datasource, String content) {
        String filename = LocalDateTime.now().format(FILENAME_FORMATTER) + ".data";
        return upload(datasource, filename, content.getBytes(StandardCharsets.UTF_8));
    }

    private String getFullFilename(String filename, String prefix) {
        String fullFilename;
        if (StringUtils.hasLength(prefix)) {
            fullFilename = prefix + "/" + filename;
        }else {
            fullFilename = filename;
        }
        return fullFilename;
    }

    /**
     * 下载指定文件
     * @param datasource
     * @param filename
     * @return
     */
    public byte[] download(String datasource, String filename) {
        FtpDatasource ftpDatasource = getFtpDatasource(datasource);
        String fullFilename = getFullFilename(filename, ftpDatasource.getFtpDir());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try(Ftp ftp = buildFtpClient(ftpDatasource)) {
            ftp.download(getPath(fullFilename), getFilename(fullFilename), bos);
            return bos.toByteArray();
        }catch (IORuntimeException | IOException e){
            log.error("下载 {} FTP文件出现异常", datasource, e);
            return null;
        }
    }

    /**
     * 非递归地列出数据源下某个文件夹的所有文件
     * @param datasource
     * @param path
     * @return 返回全路径名
     */
    public List<String> listAll(String datasource, String path) {
        FtpDatasource ftpDatasource = getFtpDatasource(datasource);
        String prefix = StringUtils.hasLength(ftpDatasource.getFtpDir()) ? ftpDatasource.getFtpDir() : "";
        try(Ftp ftp = buildFtpClient(ftpDatasource)){
            ftp.init();
            return Arrays.stream(ftp.lsFiles(prefix + "/" + path))
                    .filter(FTPFile::isFile)
                    .map(file -> PathUtils.concat("/", path, file.getName()))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("FTP {} 文件列出异常", datasource, e);
            return Collections.emptyList();
        }
    }

    /**
     * 非递归地列出数据源下的所有文件
     * @param datasource
     * @return 返回全路径名
     */
    public List<String> listAll(String datasource) {
        return listAll(datasource, "");
    }

    /**
     * 物理删除数据源下的指定文件
     * @param datasource
     * @param path
     * @return
     */
    public boolean deleteFile(String datasource, String path) {
        FtpDatasource ftpDatasource = getFtpDatasource(datasource);
        try(Ftp ftp = buildFtpClient(datasource)){
            ftp.init();
            return ftp.delFile(PathUtils.concat("/", ftpDatasource.getFtpDir(), path));
        }catch (Exception e){
            log.error("{} 删除文件 {} 异常", datasource, path, e);
            return false;
        }
    }

    /**
     * 逻辑删除 会将删除的文件移动到后备目录
     * @param datasource
     * @param path
     * @return
     */
    public boolean deleteFileLogically(String datasource, String path) {
        byte[] bytes = download(datasource, path);
        if (!uploadToBack(datasource, path, bytes)) {
            return false;
        }
        return deleteFile(datasource, path);
    }

    /**
     * 删除远程文件的同时会备份到本地的后备目录
     * @param datasource
     * @param path
     * @return
     */
    public boolean deleteFileBak(String datasource, String path) {
        byte[] bytes = download(datasource, path);
        FtpDatasource ftpDatasource = getFtpDatasource(datasource);
        try {
            String physicPath = PathUtils.concat("/", ftpDatasource.getBackFtpDir(), path);
            IoUtil.write(new FileOutputStream(physicPath), true, bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return deleteFile(datasource, path);
    }

    /**
     * 获取某数据源FTP客户端
     * @param datasource
     * @return
     */
    public Ftp buildFtpClient(String datasource) {
        FtpDatasource ftpDatasource = ftpDatasourceHolder.get(datasource);
        if (ftpDatasource == null) {
            throw new IllegalArgumentException("指定的FTP数据源 " + datasource + " 不存在");
        }
        return buildFtpClient(ftpDatasource);
    }

    private Ftp buildFtpClient(FtpDatasource ftpDatasource) {
        FtpConfig ftpConfig = ftpDatasourceHolder.convert2Config(ftpDatasource);
        Ftp ftp = new Ftp(ftpConfig, FtpMode.Passive);
        ftp.init();
        return ftp;
    }

    private FtpDatasource getFtpDatasource(String datasource) {
        FtpDatasource ftpDatasource = ftpDatasourceHolder.get(datasource);
        if (ftpDatasource == null) {
            throw new IllegalArgumentException("没有 " + datasource + " FTP数据源");
        }
        return ftpDatasource;
    }

    private void ensureFtpDirExists(Ftp ftp, String filename){
        if (!StringUtils.hasLength(filename)) {
            return;
        }
        String path = getPath(filename);
        ftp.mkDirs(path);
    }

    private String getPath(String path) {
        String[] arr = path.split("/");
        return String.join("/", Arrays.copyOfRange(arr, 0, arr.length - 1));
    }

    private String getFilename(String path) {
        return path.split("/")[path.split("/").length - 1];
    }
}
