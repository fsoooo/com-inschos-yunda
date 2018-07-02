package com.inschos.yunda.access.http.controller.bean;

import java.util.List;

/**
 * User: wangsl
 * Date: 2018/07/02
 * Time: 14:12
 * 韵达项目-理赔-参数Bean
 */
public class InsureClaimBean {
    public static class claimApplyRequest {//理赔申请
        public String name;
        public String IdCard;
        public String phone;
        public String email;
        public String address;//当前住址
        public List<claimType> claimTypes;//出险类型
        public String claimStart;//出险时间
        public String claimArea;//出险地区
        public String ClaimDescription;//出险经过描述
    }

    public static class claimType {
        public String claimType;//出险类型:普通General/残疾Disabled/身故Dead
    }

    public static class claimMaterialGeneralRequest {//（一）普通案件保险金申请
        public String claimApplication;//理赔申请书
        public String medicalInformation;//病历、诊断证明、出院记录等医疗资料
        public String medicalInvoice;//医疗发票
        public String feesList;//费用清单(凭发票在医院打印)
        public String idCardCopy;//伤者身份证复印件
        public String bankAccount;//划款户名、帐号、开户行信息
        public String trafficAccidentCertification;//交通事故责任认定书或公安部门出具的报/立案证明
        public String thirdMaterial;//涉及与双方交通事故的三者方的财产损失证明材料、医疗资料及双方赔偿协议
    }

    public static class claimMaterialDisabledRequest {//（二）残疾保险金申请
        public claimMaterialGeneralRequest claimMaterialGeneralRequest;
        public String disabilityReport;//二级以上（含二级）或保险人认可的医疗机构或司法鉴定机构出具的伤残鉴定报告
    }

    public static class claimMaterialDeadRequest {//（三）身故保险金申请
        public claimMaterialGeneralRequest claimMaterialGeneralRequest;
        public String deathCertificate;//公安部门或医疗机构出具的被保险人死亡证明书，火化证，户籍注销证明复印件
        public String beneficiaryMaterial;//受益人资料:包括所有法定受益人与被保险人的亲属关系证明（身份证、户口本、结婚证等）,赔款分配协议或所有受益人委托某一受益人领取赔款的协议或公证书
    }

    public static class claimVerifyRequest {//获取理赔审核
        public long claimId;//理赔id
    }

    public static class doClaimVerifyRequest {//理赔审核提交
        public long claimId;//理赔id
        public long verifyStatus;//审核状态:1审核通过/2需要重新提交资料/3驳回
        public long verifyContent;//审核备注
    }

    public static class claimProgressListRequest {//获取理赔进度列表
        public String claimProgressStaus;//理赔进度状态:1进行中/2已完结
    }

    public static class claimProgressInfoRequest {//获取理赔进度详情
        public long claimId;//理赔id
    }
}
