package com.inschos.yunda.model;

import javafx.scene.text.Text;

public class ClaimRecord {
    public long id;
    public long cust_id;
    public String account_uuid;
    public String warranty_uuid;
    public String name;
    public String idcard;
    public String email;
    public String address;
    public String claim_type;//出险类型:普通General/残疾Disabled/身故Dead
    public long claim_start;//出险时间
    public String claim_area;//出险地区
    public Text claim_desc;//出险描述
    public int status;//进度状态: 1申请理赔 2提交资料 3等待审核 4审核通过 -1 审核失败',
    public long created_at;
    public long updated_at;

    public ClaimInfo claimInfo;
    public Page page;
}
