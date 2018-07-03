package com.inschos.yunda.access.http.controller.action;


import com.inschos.yunda.access.http.controller.bean.ActionBean;
import com.inschos.yunda.access.http.controller.bean.BaseResponseBean;
import com.inschos.yunda.access.http.controller.bean.InsureClaimBean;
import com.inschos.yunda.assist.kit.JsonKit;
import com.inschos.yunda.assist.kit.TimeKit;
import com.inschos.yunda.data.dao.ClaimRecordDao;
import com.inschos.yunda.model.ClaimRecord;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class InsureClaimAction extends BaseAction {

    private static final Logger logger = Logger.getLogger(InsureClaimAction.class);

    @Autowired
    private ClaimRecordDao claimRecordDao;

    /**
     * 获取理赔进度列表
     *
     * @param actionBean
     * @return
     */
    public String findClaimProgressList(ActionBean actionBean) {
        InsureClaimBean.claimProgressListRequest request = JsonKit.json2Bean(actionBean.body, InsureClaimBean.claimProgressListRequest.class);
        BaseResponseBean response = new BaseResponseBean();
        if (request == null) {
            return json(BaseResponseBean.CODE_FAILURE, "参数解析失败", response);
        }
        ClaimRecord claimRecord = new ClaimRecord();
        claimRecord.cust_id = Long.valueOf(actionBean.userId);
        claimRecord.cust_id = Long.valueOf(actionBean.accountUuid);
        if (request.claimProgressStaus == 0) {//理赔进度状态:1进行中(待审核状态:申请理赔/申请理赔/等待审核)/2已完结(审核通过,审核失败)
            claimRecord.status = 1;
        } else {
            claimRecord.status = request.claimProgressStaus;
        }
        claimRecord.page = setPage(request.lastId, request.pageNum, request.pageSize);
        List<ClaimRecord> claimRecords = claimRecordDao.findClaimRecordList(claimRecord);
        if (claimRecords == null) {
            return json(BaseResponseBean.CODE_FAILURE, "获取理赔进度列表失败", response);
        }
        List<InsureClaimBean.claimProgressListResponse> returnResponseList = new ArrayList<>();
        for (ClaimRecord record : claimRecords) {
            InsureClaimBean.claimProgressListResponse returnResponse = new InsureClaimBean.claimProgressListResponse();
            returnResponse.name = record.name;
            returnResponse.productName = "英大快递保";
            returnResponse.claimProgressStaus = record.status;
            returnResponse.claimProgressStausText = InsureClaimBean.getListStatus(record.status);
            returnResponse.createdAt = record.created_at;
            returnResponse.createdAtText = TimeKit.format("yyyy.MM.dd", record.created_at);
            returnResponse.updatedAt = record.updated_at;
            returnResponse.updatedAtText = TimeKit.format("yyyy-MM-dd", record.updated_at);
            returnResponseList.add(returnResponse);
        }
        response.data = returnResponseList;
        return json(BaseResponseBean.CODE_SUCCESS, "获取理赔进度列表成功", response);
    }

    /**
     * 获取理赔进度详情
     *
     * @param actionBean
     * @return
     */
    public String findClaimProgressInfo(ActionBean actionBean) {
        InsureClaimBean.claimProgressInfoRequest request = JsonKit.json2Bean(actionBean.body, InsureClaimBean.claimProgressInfoRequest.class);
        BaseResponseBean response = new BaseResponseBean();
        if (request == null) {
            return json(BaseResponseBean.CODE_FAILURE, "参数解析失败", response);
        }
        if (request.claimId == 0) {
            return json(BaseResponseBean.CODE_FAILURE, "必要参数为空", response);
        }
        ClaimRecord claimRecord = new ClaimRecord();
        claimRecord.id = request.claimId;
        ClaimRecord result = claimRecordDao.findClaimVerify(claimRecord);
        if (result == null) {
            return json(BaseResponseBean.CODE_FAILURE, "获取理赔进度详情失败", response);
        }
        InsureClaimBean.claimProgressInfoResponse returnResponse = new InsureClaimBean.claimProgressInfoResponse();
        returnResponse.name = result.name;
        returnResponse.productName = "英大快递保";
        returnResponse.claimProgressStaus = result.status;
        returnResponse.claimProgressStausText = InsureClaimBean.getListStatus(result.status);
        returnResponse.createdAt = result.created_at;
        returnResponse.createdAtText = TimeKit.format("yyyy.MM.dd", result.created_at);
        returnResponse.updatedAt = result.updated_at;
        returnResponse.updatedAtText = TimeKit.format("yyyy-MM-dd", result.updated_at);
        //申请完成
        returnResponse.applyEnd = result.claimInfo.created_at;
        returnResponse.applyEndText = TimeKit.format("yyyy-MM-dd", result.claimInfo.created_at);
        //资料上传
        returnResponse.uploadEnd = result.claimInfo.created_at;
        returnResponse.uploadEndText = TimeKit.format("yyyy-MM-dd", result.claimInfo.created_at);
        //专员审核
        returnResponse.verifyEnd = result.claimInfo.updated_at;
        returnResponse.verifyEndText = TimeKit.format("yyyy-MM-dd", result.claimInfo.updated_at);
        //理赔结案
        returnResponse.claimEnd = result.claimInfo.updated_at;
        returnResponse.claimEndText = TimeKit.format("yyyy-MM-dd", result.claimInfo.updated_at);
        response.data = returnResponse;
        return json(BaseResponseBean.CODE_SUCCESS, "获取理赔进度详情成功", response);
    }

}
