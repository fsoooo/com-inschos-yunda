package com.inschos.yunda.access.rpc.bean;

import java.util.List;

/**
 * Created by IceAnt on 2018/5/21.
 */
public class AgentJobBean {

    /** */
    public long id;

    /** 当前业管账号id*/
    public String manager_uuid;

    /** 渠道id*/
    public long channel_id;

    /** 关联person表主键*/
    public long person_id;

    /** 代理人手机号*/
    public String phone;

    /** 代理人姓名*/
    public String name;

    /** 工号*/
    public String job_num;

    public String email;

    /** 职位表id*/
    public long position_id;

    /** 标签 使用英文的,隔开 关联user_tag表的主键*/
    public String user_tag_id;

    /** 认证状态 1未认证 2认证通过 3认证失败*/
    public int authentication;

    /** 备注*/
    public String note;

    /** 入职时间*/
    public long entry_time;

    /** 任职状态： 0离职 1在职*/
    public int status;

    public List<String> channelIdList;

    public String search_cur_time;

}
