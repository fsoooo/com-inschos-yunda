package com.inschos.yunda.model;

public class WarrantyRecord {
    public long cust_id;//用户id
    public String warranty_uuid;//'warranty_uuid内部保单唯一标识'
    public String warranty_status;//保单状态 1待处理, 2待支付,3待生效, 4保障中,5可续保，6已失效，7已退保  8已过保',
    public String warranty_status_text;//保单状态文案 1待处理, 2待支付,3待生效, 4保障中,5可续保，6已失效，7已退保  8已过保',
    public long created_at;
    public long updated_at;
}
