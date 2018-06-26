package com.inschos.yunda.access.http.controller.action;


import com.inschos.yunda.access.http.controller.bean.*;
import com.inschos.yunda.assist.kit.HttpClientKit;
import com.inschos.yunda.assist.kit.JsonKit;
import com.inschos.yunda.data.dao.WarrantyRecordDao;
import com.inschos.yunda.model.WarrantyRecord;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import static com.inschos.yunda.access.http.controller.bean.IntersCommonUrlBean.*;

@Component
public class InsureWarrantyAction extends BaseAction {

    private static final Logger logger = Logger.getLogger(InsureSetupAction.class);

    @Autowired
    private WarrantyRecordDao warrantyRecordDao;

    /**
     * 获取保单列表(不同状态的)
     * TODO 从远程接口取数据
     *
     * @param actionBean
     * @return
     */
    public String findInsureWarrantyList(ActionBean actionBean) {
        InsureWarrantyBean.warrantyRecordRequest request = JsonKit.json2Bean(actionBean.body, InsureWarrantyBean.warrantyRecordRequest.class);
        BaseResponseBean response = new BaseResponseBean();
        //判空
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
        try {
            //TODO 请求http
            String warrantyListRes = HttpClientKit.post(toWarrantyList, JsonKit.bean2Json(warrantyListRequest));
            if (warrantyListRes == null) {
                return json(BaseResponseBean.CODE_FAILURE, "获取保单列表接口请求失败", response);
            }
            InsureWarrantyBean.warrantyListResponse warrantyInfoResponse = JsonKit.json2Bean(warrantyListRes, InsureWarrantyBean.warrantyListResponse.class);
            if (warrantyInfoResponse.code == 500) {
                return json(BaseResponseBean.CODE_FAILURE, "获取保单列表接口请求失败", response);
            }
            response.data = warrantyInfoResponse.data;
            return json(BaseResponseBean.CODE_SUCCESS, "接口请求成功", response);
        } catch (IOException e) {
            return json(BaseResponseBean.CODE_FAILURE, "获取保单列表接口请求失败", response);
        }
    }

    /**
     * 获取保单详情
     * TODO 根据warranty_uuid,从远程接口取数据
     *
     * @param actionBean
     * @return
     */
    public String findInsureWarrantyInfo(ActionBean actionBean) {
        InsureWarrantyBean.warrantyRecordRequest request = JsonKit.json2Bean(actionBean.body, InsureWarrantyBean.warrantyRecordRequest.class);
        BaseResponseBean response = new BaseResponseBean();
        //判空
        if (request == null) {
            return json(BaseResponseBean.CODE_FAILURE, "参数解析失败", response);
        }
        InsureWarrantyBean.warrantyInfoRequest warrantyInfoRequest = new InsureWarrantyBean.warrantyInfoRequest();
        warrantyInfoRequest.custId = Long.valueOf(actionBean.userId);
        warrantyInfoRequest.accountUuid = Long.valueOf(actionBean.accountUuid);
        warrantyInfoRequest.warrantyUuid = request.warrantyUuid;
        try {
            //TODO 请求http
            String warrantyInfoRes = HttpClientKit.post(toWarrantyInfo, JsonKit.bean2Json(warrantyInfoRequest));
            if (warrantyInfoRes == null) {
                return json(BaseResponseBean.CODE_FAILURE, "获取保单详情接口请求失败", response);
            }
            InsureWarrantyBean.warrantyInfoResponse warrantyInfoResponse = JsonKit.json2Bean(warrantyInfoRes, InsureWarrantyBean.warrantyInfoResponse.class);
            if (warrantyInfoResponse.code == 500) {
                return json(BaseResponseBean.CODE_FAILURE, "获取保单详情接口请求失败", response);
            }
            response.data = warrantyInfoResponse.data;
            return json(BaseResponseBean.CODE_SUCCESS, "接口请求成功", response);
        } catch (IOException e) {
            return json(BaseResponseBean.CODE_FAILURE, "获取保单详情接口请求失败", response);
        }
    }

    /**
     * 获取购保结果
     * TODO 从远程接口取数据,本地数据做备用
     *
     * @param actionBean
     * @return
     */
    public String findInsureResult(ActionBean actionBean) {
        InsureSetupBean request = JsonKit.json2Bean(actionBean.body, InsureSetupBean.class);
        BaseResponseBean response = new BaseResponseBean();
        //判空
        if (request == null) {
            return json(BaseResponseBean.CODE_FAILURE, "参数解析失败", response);
        }
        WarrantyRecord warrantyRecord = new WarrantyRecord();
        warrantyRecord.cust_id = Long.valueOf(actionBean.userId);
        Calendar calendar = Calendar.getInstance();
        long day_start = calendar.getTimeInMillis();
        warrantyRecord.day_start = day_start;
        warrantyRecord.day_end = day_start + 24 * 60 * 60 * 1000;
        WarrantyRecord insureResult = warrantyRecordDao.findInsureResult(warrantyRecord);
        if (insureResult == null) {
            InsureWarrantyBean.insureResultRequest insureResultRequest = new InsureWarrantyBean.insureResultRequest();
            insureResultRequest.custId = Long.valueOf(actionBean.userId);
            insureResultRequest.accountUuid = Long.valueOf(actionBean.accountUuid);
            try {
                //TODO 请求http
                String insureResultRes = HttpClientKit.post(toInsureResult, JsonKit.bean2Json(insureResultRequest));
                if (insureResultRes == null) {
                    return json(BaseResponseBean.CODE_FAILURE, "获取投保结果失败", response);
                }
                InsureWarrantyBean.insureResultResponse insureResultResponse = JsonKit.json2Bean(insureResultRes, InsureWarrantyBean.insureResultResponse.class);
                if (insureResultResponse.code == 500) {
                    return json(BaseResponseBean.CODE_FAILURE, "获取投保结果失败", response);
                }
                response.data = insureResultResponse.data;
                return json(BaseResponseBean.CODE_SUCCESS, "接口请求成功", response);
            } catch (IOException e) {
                return json(BaseResponseBean.CODE_FAILURE, "获取投保结果失败", response);
            }
        }else{
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
