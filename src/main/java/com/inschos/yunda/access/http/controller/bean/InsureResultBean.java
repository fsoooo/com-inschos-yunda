package com.inschos.yunda.access.http.controller.bean;

public class InsureResultBean {
    public long id;//用户id
    public long custId;//用户id
    public String warrantyUuid;//'warranty_uuid内部保单唯一标识'
    public String warrantyStatus;//保单状态 1待处理, 2待支付,3待生效, 4保障中,5可续保，6已失效，7已退保  8已过保',
    public String warrantyStatusText;//保单状态文案 1待处理, 2待支付,3待生效, 4保障中,5可续保，6已失效，7已退保  8已过保',
    public long createdAt;
    public long updatedAt;
}
