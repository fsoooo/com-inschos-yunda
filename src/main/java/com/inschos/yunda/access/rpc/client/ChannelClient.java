package com.inschos.yunda.access.rpc.client;

import com.inschos.yunda.access.rpc.bean.ChannelBean;
import com.inschos.yunda.access.rpc.service.ChannelService;
import com.inschos.yunda.assist.kit.L;
import hprose.client.HproseHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by IceAnt on 2018/5/21.
 */
@Component
public class ChannelClient {

    @Value("${rpc.remote.customer.host}")
    private String host;

    private final String uri = "/rpc/channel";


    private ChannelService getService(){
        return new HproseHttpClient(host + uri).useService(ChannelService.class);
    }

    public ChannelBean getChannel(String channelId){
        try {
            ChannelService service = getService();
            return service!=null?service.getChannelDetailById(channelId):null;

        }catch (Exception e){
            L.log.error("remote fail {}",e.getMessage(),e);
            return null;
        }
    }

    public List<String> getChildrenId(String channelId, boolean needSelf){
        try {
            ChannelService service = getService();
            return service!=null?service.getChildrenChannelIdById(channelId,needSelf):null;

        }catch (Exception e){
            L.log.error("remote fail {}",e.getMessage(),e);
            return null;
        }
    }


}
