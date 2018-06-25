package com.inschos.yunda.access.rpc.bean;

/**
 * Created by IceAnt on 2018/5/21.
 */
public class ChannelBean {
    /**
     * id
     */
    public String id;

    /**
     * 父渠道id
     */
    public String pid;

    /**
     * 渠道名称
     */
    public String name;

    /**
     * 负责人ID组
     */
    public String leader_id;

    /**
     * 渠道分类 1:外部合作 2:内部分组
     */
    public String type;

    /**
     * 渠道种类 1-系统根渠道，2-系统未分配，3-用户
     */
    public String category;

    /**
     * 经度
     */
    public String longitude;

    /**
     * 纬度
     */
    public String latitude;

    /**
     * 账户ID
     */
    public String manager_uuid;
}
