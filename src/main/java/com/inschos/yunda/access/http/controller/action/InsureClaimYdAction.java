package com.inschos.yunda.access.http.controller.action;

import com.inschos.yunda.access.http.controller.bean.*;
import com.inschos.yunda.assist.kit.JsonKit;
import com.inschos.yunda.data.dao.ClaimRecordDao;
import com.inschos.yunda.data.dao.WarrantyRecordDao;
import com.inschos.yunda.model.ClaimRecord;
import com.inschos.yunda.model.WarrantyRecord;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

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
            String claim_type = "";
            for (InsureClaimBean.claimType claimType : request.claimTypes) {
                claim_type = claim_type + "," + claimType.claimType;
            }
            claimRecord.claim_type = claim_type;
        }
        claimRecord.claim_start = request.claimStart;
        claimRecord.claim_area = request.claimArea;
        claimRecord.claim_desc = request.ClaimDescription;
        claimRecord.status = 1;//进度状态: 1申请理赔 2提交资料 3等待审核 4审核通过 -1 审核失败',
        claimRecord.created_at = date;
        claimRecord.updated_at = date;
        long addRes = claimRecordDao.addClaimRecord(claimRecord);
        if (addRes == 0) {
            return json(BaseResponseBean.CODE_FAILURE, "理赔申请提交失败", response);
        } else {
            return json(BaseResponseBean.CODE_SUCCESS, "理赔申请提交成功", response);
        }
    }

    /**
     * 获取理赔申请页信息
     *
     * @param actionBean
     * @return
     */
    public String findClaimMaterialInfo(ActionBean actionBean) {
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
        ClaimRecord claimRecordRes = claimRecordDao.findClaimRecord(claimRecord);
        if (claimRecordRes == null) {
            return json(BaseResponseBean.CODE_FAILURE, "获取理赔申请页信息失败", response);
        }
        String warrantyUuid = claimRecordRes.warranty_uuid;
        String claimTypes = claimRecordRes.claim_type;//出险类型 TODO 还没做处理
        InsureWarrantyBean.warrantyInfoRequest warrantyInfoRequest = new InsureWarrantyBean.warrantyInfoRequest();
        warrantyInfoRequest.custId = Long.valueOf(actionBean.userId);
        warrantyInfoRequest.accountUuid = Long.valueOf(actionBean.accountUuid);
        warrantyInfoRequest.warrantyUuid = warrantyUuid;
        String warrantyRecordRes = insureWarrantyAction.findInsureWarrantyInfoById(warrantyInfoRequest);
        if(warrantyRecordRes==null){
            return json(BaseResponseBean.CODE_FAILURE, "获取理赔申请页信息-保单信息失败", response);
        }
        InsureWarrantyBean.warrantyInfoResponse warrantyInfoResponse =  JsonKit.json2Bean(warrantyRecordRes, InsureWarrantyBean.warrantyInfoResponse.class);
        InsureClaimBean.claimVerifyResponse claimVerifyResponse = new InsureClaimBean.claimVerifyResponse();
        //TODO 还没做处理
        claimVerifyResponse.claimType = claimTypes;
        claimVerifyResponse.insureName = claimRecordRes.name;
        claimVerifyResponse.insureDays = warrantyInfoResponse.data.toString();
        claimVerifyResponse.insurePrice = claimTypes;
        claimVerifyResponse.productName = claimTypes;
        claimVerifyResponse.warrantyCode = claimTypes;
        response.data = claimVerifyResponse;
        return json(BaseResponseBean.CODE_SUCCESS, "获取理赔申请页信息成功", response);
    }

    /**
     * 提交理赔申请资料
     *
     * @param actionBean
     * @return
     */
    public String claimMaterialPost(ActionBean actionBean) {
        InsureSetupBean request = JsonKit.json2Bean(actionBean.body, InsureSetupBean.class);
        BaseResponseBean response = new BaseResponseBean();
        //判空
        if (request == null) {
            return json(BaseResponseBean.CODE_FAILURE, "参数解析失败", response);
        }
        return json(BaseResponseBean.CODE_SUCCESS, "业务完善中...", response);
    }

    /**
     * 获取理赔审核页面信息
     *
     * @param actionBean
     * @return
     */
    public String findClaimVerifyInfo(ActionBean actionBean) {
        InsureSetupBean request = JsonKit.json2Bean(actionBean.body, InsureSetupBean.class);
        BaseResponseBean response = new BaseResponseBean();
        //判空
        if (request == null) {
            return json(BaseResponseBean.CODE_FAILURE, "参数解析失败", response);
        }
        return json(BaseResponseBean.CODE_SUCCESS, "业务完善中...", response);
    }

    /**
     * 理赔审核页面提交
     *
     * @param actionBean
     * @return
     */
    public String doClaimVirify(ActionBean actionBean) {
        InsureSetupBean request = JsonKit.json2Bean(actionBean.body, InsureSetupBean.class);
        BaseResponseBean response = new BaseResponseBean();
        //判空
        if (request == null) {
            return json(BaseResponseBean.CODE_FAILURE, "参数解析失败", response);
        }
        return json(BaseResponseBean.CODE_SUCCESS, "业务完善中...", response);
    }
}
