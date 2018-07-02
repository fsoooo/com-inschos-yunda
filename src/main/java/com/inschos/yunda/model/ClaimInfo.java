package com.inschos.yunda.model;

import javax.xml.soap.Text;

public class ClaimInfo {
    public long id;
    public long claim_id;//理赔进度id
    public String claim_application;//理赔申请书
    public String medical_information;//病历、诊断证明、出院记录等医疗资料
    public String medical_invoice;//医疗发票
    public String fees_list;//费用清单
    public String idcard_copy;//伤者身份证复印件
    public String bank_account;//划款户名、帐号、开户行信息
    public String traffic_accident_certification;//交通事故责任认定书或公安部门出具的报/立案证明
    public String third_material;//涉及与双方交通事故的三者方的财产损失证明材料、医疗资料及双方赔偿协议
    public String disability_report;//二级以上（含二级）或保险人认可的医疗机构或司法鉴定机构出具的伤残鉴定报告
    public String death_certificate;//公安部门或医疗机构出具的被保险人死亡证明书，火化证，户籍注销证明复印件
    public String beneficiary_material;//受益人资料
    public int status;//审核状态：默认1未审核 /2审核通过/3需要重新提交资料/4审核驳回
    public Text remarks;//审核备注
    public long created_at;
    public long updated_at;
}
