package com.inschos.yunda.access.http.controller.action;

import com.fasterxml.jackson.core.type.TypeReference;
import com.inschos.yunda.access.http.controller.bean.ActionBean;
import com.inschos.yunda.access.http.controller.bean.BaseResponseBean;
import com.inschos.yunda.access.http.controller.bean.InsureClaimBean;
import com.inschos.yunda.access.http.controller.bean.InsureWarrantyBean;
import com.inschos.yunda.assist.kit.JsonKit;
import com.inschos.yunda.data.dao.ClaimRecordDao;
import com.inschos.yunda.data.dao.WarrantyRecordDao;
import com.inschos.yunda.model.ClaimInfo;
import com.inschos.yunda.model.ClaimRecord;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class InsureClaimYdAction extends BaseAction {

    private static final Logger logger = Logger.getLogger(InsureClaimYdAction.class);

    @Autowired
    private ClaimRecordDao claimRecordDao;

    @Autowired
    private WarrantyRecordDao warrantyRecordDao;

    @Autowired
    private InsureWarrantyAction insureWarrantyAction;

    /**
     * 提交理赔申请信息
     *
     * @param actionBean
     * @return
     */
    public String claimApplyPost(ActionBean actionBean) {
        InsureClaimBean.claimApplyRequest request = JsonKit.json2Bean(actionBean.body, InsureClaimBean.claimApplyRequest.class);
        BaseResponseBean response = new BaseResponseBean();
        if (request == null) {
            return json(BaseResponseBean.CODE_FAILURE, "参数解析失败", response);
        }
        ClaimRecord claimRecord = new ClaimRecord();
        long date = new Date().getTime();
        claimRecord.cust_id = Long.valueOf(actionBean.userId);
        claimRecord.account_uuid = Long.valueOf(actionBean.accountUuid);
        claimRecord.warranty_uuid = request.warrantyUuid;
        claimRecord.name = request.name;
        claimRecord.idcard = request.IdCard;
        claimRecord.email = request.email;
        claimRecord.address = request.address;
        claimRecord.phone = request.phone;
        if (request.claimTypes == null) {
            claimRecord.claim_type = "";
        } else {
            claimRecord.claim_type = JsonKit.bean2Json(request.claimTypes);
        }
        claimRecord.claim_start = request.claimStart;
        claimRecord.claim_area = request.claimArea;
        claimRecord.claim_desc = request.claimDescription;
        claimRecord.status = 1;//进度状态: 1申请理赔 2提交资料 3等待审核 4审核通过 -1 审核失败',
        claimRecord.created_at = date;
        claimRecord.updated_at = date;
        long addRes = claimRecordDao.addClaimRecord(claimRecord);
        if (addRes == 0) {
            return json(BaseResponseBean.CODE_FAILURE, "理赔申请提交失败", response);
        } else {
            InsureClaimBean.claimVerifyRequest claimId = new InsureClaimBean.claimVerifyRequest();
            claimId.claimId = claimRecord.id;
            response.data = claimId;
            return json(BaseResponseBean.CODE_SUCCESS, "理赔申请提交成功", response);
        }
    }

    /**
     * 获取理赔资料提交页信息
     * TODO 会返给端上 保单信息和出险类型，来确定页面的显示
     *
     * @param actionBean
     * @return
     */
    public String findClaimMaterialInfo(ActionBean actionBean) {
        InsureClaimBean.claimMaterialInfoRequest request = JsonKit.json2Bean(actionBean.body, InsureClaimBean.claimMaterialInfoRequest.class);
        BaseResponseBean response = new BaseResponseBean();
        if (request == null) {
            return json(BaseResponseBean.CODE_FAILURE, "参数解析失败", response);
        }
        if (request.claimId == 0) {
            return json(BaseResponseBean.CODE_FAILURE, "必要参数为空", response);
        }
        ClaimRecord claimRecord = new ClaimRecord();
        claimRecord.id = request.claimId;
        ClaimRecord claimRecordRes = claimRecordDao.findClaimRecord(claimRecord);
        if (claimRecordRes == null) {
            return json(BaseResponseBean.CODE_FAILURE, "获取理赔申请页信息失败", response);
        }
        String warrantyUuid = claimRecordRes.warranty_uuid;
        //TODO  json转集合
        List<InsureClaimBean.claimType> claimTypeList = JsonKit.json2Bean(claimRecordRes.claim_type, new TypeReference<List<InsureClaimBean.claimType>>() {
        });
        //TODO 判断集合里的数据返回(2和3不能同时存在)
        String claimType = "";
        if (claimTypeList.size() == 1) {
            if (claimTypeList.contains("1")) {
                claimType = "1";
            } else if (claimTypeList.contains("2")) {
                claimType = "2";
            } else if (claimTypeList.contains("3")) {
                claimType = "3";
            }
        } else if (claimTypeList.size() == 2) {
            if (claimTypeList.contains("2")) {
                claimType = "2";
            } else if (claimTypeList.contains("3")) {
                claimType = "3";
            }
        } else if (claimTypeList.size() == 3) {
            claimType = "3";
        }
        InsureWarrantyBean.warrantyInfoRequest warrantyInfoRequest = new InsureWarrantyBean.warrantyInfoRequest();
        warrantyInfoRequest.custId = Long.valueOf(actionBean.userId);
        warrantyInfoRequest.accountUuid = Long.valueOf(actionBean.accountUuid);
        warrantyInfoRequest.warrantyUuid = warrantyUuid;
        String warrantyRecordRes = insureWarrantyAction.findInsureWarrantyInfoById(warrantyInfoRequest);
        if (warrantyRecordRes == null) {
            return json(BaseResponseBean.CODE_FAILURE, "获取理赔申请页信息-保单信息失败", response);
        }
        InsureWarrantyBean.warrantyInfoResponse warrantyInfoResponse = JsonKit.json2Bean(warrantyRecordRes, InsureWarrantyBean.warrantyInfoResponse.class);
        InsureClaimBean.claimMaterialInfoResponse claimMaterialInfoResponse = new InsureClaimBean.claimMaterialInfoResponse();
        //TODO 保单数据还没做处理
        claimMaterialInfoResponse.claimType = claimType;
        claimMaterialInfoResponse.claimId = request.claimId;
        claimMaterialInfoResponse.insureName = claimRecordRes.name;
        claimMaterialInfoResponse.insureDays = warrantyInfoResponse.data.toString();
        claimMaterialInfoResponse.insurePrice = warrantyInfoResponse.data.toString();
        claimMaterialInfoResponse.productName = warrantyInfoResponse.data.toString();
        claimMaterialInfoResponse.warrantyCode = warrantyInfoResponse.data.toString();
        response.data = claimMaterialInfoResponse;
        return json(BaseResponseBean.CODE_SUCCESS, "获取理赔申请页信息成功", response);
    }

    /**
     * 提交理赔申请资料
     * TODO 逻辑：
     * 端上通过上一个接口获取需要上传资料的描述然后调用文件服务上传文件，上传文件成功后把文件服务返回的键值给服务端，入库存储
     *
     * @param actionBean
     * @return
     */
    public String claimMaterialPost(ActionBean actionBean) {
        InsureClaimBean.claimMaterialRequest request = JsonKit.json2Bean(actionBean.body, InsureClaimBean.claimMaterialRequest.class);
        BaseResponseBean response = new BaseResponseBean();
        if (request == null) {
            return json(BaseResponseBean.CODE_FAILURE, "参数解析失败", response);
        }
        if (request.claimType == null || request.claimId == 0) {
            return json(BaseResponseBean.CODE_FAILURE, "必要参数为空", response);
        }
        long date = new Date().getTime();
        ClaimInfo claimInfo = new ClaimInfo();
        claimInfo.claim_id = request.claimId;
        claimInfo.claim_application = request.claimApplication;//理赔申请书
        claimInfo.medical_information = request.medicalInformation;//病历、诊断证明、出院记录等医疗资料
        claimInfo.medical_invoice = request.medicalInvoice;//医疗发票
        claimInfo.fees_list = request.feesList;//费用清单
        claimInfo.idcard_copy = request.idCardCopy;//伤者身份证复印件
        claimInfo.bank_account = request.bankAccount;//划款户名、帐号、开户行信息
        claimInfo.traffic_accident_certification = request.trafficAccidentCertification;//交通事故责任认定书
        claimInfo.third_material = request.thirdMaterial;//三者方的财产损失证明材料、医疗资料及双方赔偿协议
        switch (request.claimType) {
            case "2":
                claimInfo.disability_report = request.disabilityReport;//伤残鉴定书
                break;
            case "3":
                claimInfo.death_certificate = request.deathCertificate;//死亡鉴定书
                claimInfo.beneficiary_material = request.beneficiaryMaterial;//受益人资料
                break;
        }
        claimInfo.status = 1;
        claimInfo.remarks = "";
        claimInfo.created_at = date;
        claimInfo.updated_at = date;
        long addRes = claimRecordDao.addClaimInfo(claimInfo);
        if (addRes == 0) {
            return json(BaseResponseBean.CODE_SUCCESS, "提交理赔申请资料失败", response);
        } else {
            return json(BaseResponseBean.CODE_SUCCESS, "提交理赔申请资料成功", response);
        }
    }

    /**
     * 获取理赔审核页面信息
     *
     * @param actionBean
     * @return
     */
    public String findClaimVerifyInfo(ActionBean actionBean) {
        InsureClaimBean.claimVerifyRequest request = JsonKit.json2Bean(actionBean.body, InsureClaimBean.claimVerifyRequest.class);
        BaseResponseBean response = new BaseResponseBean();
        if (request == null) {
            return json(BaseResponseBean.CODE_FAILURE, "参数解析失败", response);
        }
        if (request.claimId == 0) {
            return json(BaseResponseBean.CODE_FAILURE, "必要参数为空", response);
        }
        ClaimRecord claimRecord = new ClaimRecord();
        claimRecord.id = request.claimId;
        ClaimRecord claimVerifyRes = claimRecordDao.findClaimVerify(claimRecord);
        if (claimVerifyRes == null) {
            return json(BaseResponseBean.CODE_FAILURE, "获取理赔审核页面信息失败", response);
        }
        InsureClaimBean.claimVerifyResponse claimVerifyResponse = new InsureClaimBean.claimVerifyResponse();
        //TODO 获取出险人信息
        claimVerifyResponse.name = claimVerifyRes.name;
        claimVerifyResponse.IdCard = claimVerifyRes.idcard;
        claimVerifyResponse.phone = claimVerifyRes.phone;
        claimVerifyResponse.email = claimVerifyRes.email;
        claimVerifyResponse.address = claimVerifyRes.address;
        //TODO 获取出险信息
        claimVerifyResponse.claimId = claimVerifyRes.id;
        claimVerifyResponse.claimType = claimVerifyRes.claim_type;
        claimVerifyResponse.claimStart = claimVerifyRes.claim_start;
        claimVerifyResponse.claimArea = claimVerifyRes.claim_area;
        claimVerifyResponse.claimDescription = claimVerifyRes.claim_desc;
        //TODO 获取保单信息
        InsureWarrantyBean.warrantyInfoRequest warrantyInfoRequest = new InsureWarrantyBean.warrantyInfoRequest();
        warrantyInfoRequest.custId = Long.valueOf(actionBean.userId);
        warrantyInfoRequest.accountUuid = Long.valueOf(actionBean.accountUuid);
        warrantyInfoRequest.warrantyUuid = claimVerifyRes.warranty_uuid;
        String warrantyRecordRes = insureWarrantyAction.findInsureWarrantyInfoById(warrantyInfoRequest);
        if (warrantyRecordRes == null) {
            return json(BaseResponseBean.CODE_FAILURE, "获取理赔申请页信息-保单信息失败", response);
        }
        InsureWarrantyBean.warrantyInfoResponse warrantyInfoResponse = JsonKit.json2Bean(warrantyRecordRes, InsureWarrantyBean.warrantyInfoResponse.class);
        claimVerifyResponse.productName = "英大快递保";
        claimVerifyResponse.insureStart = warrantyInfoResponse.data.toString();
        claimVerifyResponse.insureEnd = warrantyInfoResponse.data.toString();
        claimVerifyResponse.warrantyCode = warrantyInfoResponse.data.toString();
        claimVerifyResponse.insurePrice = warrantyInfoResponse.data.toString();
        //TODO 获取理赔资料
        claimVerifyResponse.claimApplication = claimVerifyRes.claimInfo.claim_application;
        claimVerifyResponse.medicalInformation = claimVerifyRes.claimInfo.medical_information;
        claimVerifyResponse.medicalInvoice = claimVerifyRes.claimInfo.medical_invoice;
        claimVerifyResponse.feesList = claimVerifyRes.claimInfo.fees_list;
        claimVerifyResponse.idCardCopy = claimVerifyRes.claimInfo.idcard_copy;
        claimVerifyResponse.bankAccount = claimVerifyRes.claimInfo.bank_account;
        claimVerifyResponse.trafficAccidentCertification = claimVerifyRes.claimInfo.traffic_accident_certification;
        claimVerifyResponse.thirdMaterial = claimVerifyRes.claimInfo.third_material;
        claimVerifyResponse.disabilityReport = claimVerifyRes.claimInfo.disability_report;
        claimVerifyResponse.deathCertificate = claimVerifyRes.claimInfo.death_certificate;
        claimVerifyResponse.beneficiaryMaterial = claimVerifyRes.claimInfo.beneficiary_material;
        response.data = claimVerifyResponse;
        return json(BaseResponseBean.CODE_SUCCESS, "获取理赔审核页面信息成功", response);
    }

    /**
     * 理赔审核页面提交
     *
     * @param actionBean
     * @return
     */
    public String doClaimVirify(ActionBean actionBean) {
        InsureClaimBean.doClaimVerifyRequest request = JsonKit.json2Bean(actionBean.body, InsureClaimBean.doClaimVerifyRequest.class);
        BaseResponseBean response = new BaseResponseBean();
        if (request == null) {
            return json(BaseResponseBean.CODE_FAILURE, "参数解析失败", response);
        }
        if (request.claimId == 0 || request.verifyStatus == 0) {
            return json(BaseResponseBean.CODE_FAILURE, "必要参数为空", response);
        }
        long date = new Date().getTime();
        ClaimInfo claimInfo = new ClaimInfo();
        claimInfo.claim_id = request.claimId;
        claimInfo.status = request.verifyStatus;
        claimInfo.remarks = request.verifyContent;
        claimInfo.updated_at = date;
        long updateRes = claimRecordDao.updateClaimVerify(claimInfo);
        if (updateRes == 0) {
            return json(BaseResponseBean.CODE_FAILURE, "理赔审核页面提交失败", response);
        } else {
            return json(BaseResponseBean.CODE_SUCCESS, "理赔审核页面提交成功", response);
        }
    }
}
