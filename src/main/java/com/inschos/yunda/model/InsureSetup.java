package com.inschos.yunda.model;

public class InsureSetup {
    public long id;
    public long cust_id;
    public String authorize_bank;//默认支付银行卡
    public long authorize_status;//银行卡授权状态:默认未授权1/已授权2
    public long auto_insure_status;//自动投保状态:默认未开启1/已开启2
    public String auto_insure_price;//每日购保价格
    public long auto_insure_type;//每日购保类型
    public long auto_insure_end;//自动投保关闭时间
    public long created_at;
    public long updated_at;
}
