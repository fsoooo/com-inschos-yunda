package com.inschos.yunda.access.rpc.bean;

/**
 * Created by IceAnt on 2018/4/17.
 */
public class AccountBean {

    public final static int USER_TYPE_AGENT = 4;

    public int code;

    public String message;

    /** 所属业管uuid*/
    public String managerUuid;

    /** 登录账号uuid*/
    public String accountUuid;

    /** 1：个人账号，2：企业账号，3：业管账号，4：代理人，*/
    public int userType;

    /** 用户ID*/
    public String userId;

    /** 用户名*/
    public String username;


    /** 个人账号手机号*/
    public String phone;

    /** 邮箱*/
    public String email;

    public int sysId;
}
