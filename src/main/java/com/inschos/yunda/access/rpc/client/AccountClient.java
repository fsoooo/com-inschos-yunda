package com.inschos.yunda.access.rpc.client;

import com.inschos.yunda.access.rpc.bean.AccountBean;
import com.inschos.yunda.access.rpc.service.AccountService;
import com.inschos.yunda.assist.kit.L;
import hprose.client.HproseHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by IceAnt on 2018/3/20.
 */
@Component
public class AccountClient{

    @Value("${rpc.remote.account.host}")
    private String host;
    
    private final String uri = "/rpc/account";


    private AccountService getService(){
        return new HproseHttpClient(host + uri).useService(AccountService.class);
    }

    public AccountBean getAccount(String token){
        try {
            AccountService service = getService();
            return service!=null?service.getAccount(token):null;

        }catch (Exception e){
            L.log.error("remote fail {}",e.getMessage(),e);
            return null;
        }
    }

    public AccountBean findByUuid(String uuid){
        try {
            AccountService service = getService();
            return service!=null?service.findByUuid(uuid):null;

        }catch (Exception e){
            L.log.error("remote fail {}",e.getMessage(),e);
            return null;
        }
    }
    public AccountBean findByUser(long sysId, int userType, String userId){
        try {
            AccountService service = getService();
            return service!=null?service.findByUser(sysId,userType,userId):null;

        }catch (Exception e){
            L.log.error("remote fail {}",e.getMessage(),e);
            return null;
        }
    }




}
