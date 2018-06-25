package com.inschos.yunda.model;

public class InsureSetup {
    public long id;
    public String cust_id;
    public String authorize_bank;//默认支付银行卡
    public String authorize_status;//授权状态
    public String auto_insure_status;//自动投保状态
    public String auto_insure_price;//每日购保价格
    public String auto_insure_type;//每日购保类型
    public String auto_insure_end;//自动投保关闭时间
    public String created_at;
    public String updated_at;
}
