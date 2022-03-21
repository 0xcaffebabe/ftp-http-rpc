package wang.ismy.fttp.endpoint.service;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wang.ismy.fttp.endpoint.data.ResponseDispatcher;
import wang.ismy.fttp.endpoint.ftp.FtpTransferService;
import wang.ismy.fttp.sdk.dto.FttpEndpoint;
import wang.ismy.fttp.sdk.dto.FttpRequest;
import wang.ismy.fttp.sdk.dto.FttpResponse;

/**
 * @Title: FttpRequestService
 * @description:
 * @author: cjiping@linewell.com
 * @since: 2022年03月21日 10:24
 */
@Service
@Slf4j
public class FttpRequestService {

    @Autowired
    private FtpTransferService ftpTransferService;

    @Autowired
    private ResponseDispatcher responseDispatcher;


    public FttpResponse execute(FttpRequest request, FttpEndpoint targetEndpoint) throws InterruptedException {
        long st = System.currentTimeMillis();
        ftpTransferService.upload(targetEndpoint.getConfig().getRequestFtpDatasource(),
                JSONUtil.toJsonStr(request));
        FttpResponse response = responseDispatcher.await(request.getRequestId());
        response.setCost(System.currentTimeMillis() - st);
        return response;
    }
}
