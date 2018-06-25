package com.inschos.yunda.access.rpc.service;

import com.inschos.yunda.access.rpc.bean.ChannelBean;

import java.util.List;

/**
 * Created by IceAnt on 2018/5/21.
 */
public interface ChannelService {
    ChannelBean getChannelDetailById(String id);

    List<String> getChildrenChannelIdById(String channelId, boolean needSelf);
}
