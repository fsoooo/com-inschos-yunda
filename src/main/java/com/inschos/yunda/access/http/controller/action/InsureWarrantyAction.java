package com.inschos.yunda.access.http.controller.action;


import com.inschos.yunda.access.http.controller.bean.*;
import com.inschos.yunda.assist.kit.JsonKit;
import com.inschos.yunda.assist.kit.TimeKit;
import com.inschos.yunda.data.dao.WarrantyRecordDao;
import com.inschos.yunda.model.WarrantyRecord;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.inschos.yunda.access.http.controller.bean.IntersCommonUrlBean.*;

@Component
public class InsureWarrantyAction extends BaseAction {

    private static final Logger logger = Logger.getLogger(InsureSetupAction.class);

    @Autowired
    private WarrantyRecordDao warrantyRecordDao;

    @Autowired
    private CommonAction commonAction;

    /**
     * 获取保单列表(不同状态的)
     * 从远程接口取数据
     *
     * @param actionBean
     * @return
     */
    public String findInsureWarrantyList(ActionBean actionBean) {
        InsureWarrantyBean.warrantyRecordRequest request = JsonKit.json2Bean(actionBean.body, InsureWarrantyBean.warrantyRecordRequest.class);
        BaseResponseBean response = new BaseResponseBean();
        if (request == null) {
            return json(BaseResponseBean.CODE_FAILURE, "参数解析失败", response);
        }
        InsureWarrantyBean.warrantyListRequest warrantyListRequest = new InsureWarrantyBean.warrantyListRequest();
        warrantyListRequest.custId = Long.valueOf(actionBean.userId);
        warrantyListRequest.accountUuid = Long.valueOf(actionBean.accountUuid);
        if (request.warrantyStatus == null) {
            request.warrantyStatus = "4";//保障中
        }
        warrantyListRequest.warrantyStatus = request.warrantyStatus;
        String interName = "获取保单列表";
        String result = commonAction.httpRequest(toWarrantyList, JsonKit.bean2Json(warrantyListRequest), interName);
        InsureWarrantyBean.warrantyListResponse warrantyListResponse = JsonKit.json2Bean(result, InsureWarrantyBean.warrantyListResponse.class);
        response.data = warrantyListResponse.data;
        return json(BaseResponseBean.CODE_SUCCESS, interName + "成功", response);
    }

    /**
     * 获取保单详情
     * 根据warranty_uuid,从远程接口取数据
     *
     * @param actionBean
     * @return
     */
    public String findInsureWarrantyInfo(ActionBean actionBean) {
        InsureWarrantyBean.warrantyRecordRequest request = JsonKit.json2Bean(actionBean.body, InsureWarrantyBean.warrantyRecordRequest.class);
        BaseResponseBean response = new BaseResponseBean();
        if (request == null) {
            return json(BaseResponseBean.CODE_FAILURE, "参数解析失败", response);
        }
        InsureWarrantyBean.warrantyInfoRequest warrantyInfoRequest = new InsureWarrantyBean.warrantyInfoRequest();
        warrantyInfoRequest.custId = Long.valueOf(actionBean.userId);
        warrantyInfoRequest.accountUuid = Long.valueOf(actionBean.accountUuid);
        warrantyInfoRequest.warrantyUuid = request.warrantyUuid;
        String warrantyInfoRes = findInsureWarrantyInfoById(warrantyInfoRequest);
        return warrantyInfoRes;
    }

    /**
     * 获取保单详情
     * 根据warranty_uuid,从远程接口取数据
     *
     * @param request
     * @return
     */
    public String findInsureWarrantyInfoById(InsureWarrantyBean.warrantyInfoRequest request) {
        BaseResponseBean response = new BaseResponseBean();
        if (request == null) {
            return json(BaseResponseBean.CODE_FAILURE, "参数解析失败", response);
        }
        if (request.warrantyUuid == null || request.custId == 0 || request.accountUuid == 0) {
            return json(BaseResponseBean.CODE_FAILURE, "必要参数为空", response);
        }
        String interName = "获取保单详情";
        String result = commonAction.httpRequest(toWarrantyInfo, JsonKit.bean2Json(request), interName);
        InsureWarrantyBean.warrantyInfoResponse warrantyInfoResponse = JsonKit.json2Bean(result, InsureWarrantyBean.warrantyInfoResponse.class);
        response.data = warrantyInfoResponse.data;
        return json(BaseResponseBean.CODE_SUCCESS, interName + "成功", response);
    }

    /**
     * 获取购保结果
     * 从远程接口取数据,本地数据做备用
     *
     * @param actionBean
     * @return
     */
    public String findInsureResult(ActionBean actionBean) {
        InsureSetupBean request = JsonKit.json2Bean(actionBean.body, InsureSetupBean.class);
        BaseResponseBean response = new BaseResponseBean();
        if (request == null) {
            return json(BaseResponseBean.CODE_FAILURE, "参数解析失败", response);
        }
        WarrantyRecord warrantyRecord = new WarrantyRecord();
        warrantyRecord.cust_id = Long.valueOf(actionBean.userId);
        warrantyRecord.day_start = TimeKit.currentTimeMillis();//获取当前时间戳(毫秒值)
        warrantyRecord.day_end = TimeKit.getDayEndTime();//获取当天结束时间戳(毫秒值)
        WarrantyRecord insureResult = warrantyRecordDao.findInsureResult(warrantyRecord);
        if (insureResult == null) {
            InsureWarrantyBean.insureResultRequest insureResultRequest = new InsureWarrantyBean.insureResultRequest();
            insureResultRequest.custId = Long.valueOf(actionBean.userId);
            insureResultRequest.accountUuid = Long.valueOf(actionBean.accountUuid);
            String interName = "获取投保结果";
            String result = commonAction.httpRequest(toInsureResult, JsonKit.bean2Json(insureResultRequest), interName);
            InsureWarrantyBean.insureResultResponse insureResultResponse = JsonKit.json2Bean(result, InsureWarrantyBean.insureResultResponse.class);
            response.data = insureResultResponse.data;
            return json(BaseResponseBean.CODE_SUCCESS, interName + "成功", response);
        } else {
            InsureResultBean insureResultBean = new InsureResultBean();
            insureResultBean.id = insureResult.id;
            insureResultBean.custId = insureResult.cust_id;
            insureResultBean.warrantyUuid = insureResult.warranty_uuid;
            insureResultBean.warrantyStatus = insureResult.warranty_status;
            insureResultBean.warrantyStatusText = insureResult.warranty_status_text;
            insureResultBean.createdAt = insureResult.created_at;
            insureResultBean.updatedAt = insureResult.updated_at;
            response.data = insureResultBean;
            return json(BaseResponseBean.CODE_SUCCESS, "获取投保结果成功", response);
        }
    }
}
