package com.inschos.yunda.model;

public class BankVerify {
    public long id;
    public long cust_id;
    public String bank_code;//银行卡号
    public String bank_phone;//银行卡绑定手机号
    public String verify_id;//获取验证码返回的验证参数id
    public String verify_code;//验证码，验证成功后存取
    public long verify_time;//验证码获取成功时间，用来限制重复触发  TODO 有多条时,根据此字段排序,取最新的
    public String verify_status;//验证码验证状态：默认1未验证/2验证成功
    public long created_at;
    public long updated_at;
}
