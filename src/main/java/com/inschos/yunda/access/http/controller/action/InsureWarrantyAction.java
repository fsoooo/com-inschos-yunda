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
        if (request.warrantyStatus == null) {
            request.warrantyStatus = "4";//保障中
        }
        warrantyListRequest.warrantyStatus = request.warrantyStatus;
        String interName = "获取保单列表";
        String result = commonAction.httpRequest(toWarrantyList, JsonKit.bean2Json(warrantyListRequest), interName,actionBean.token);
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
        warrantyInfoRequest.warrantyUuid = request.warrantyUuid;
        warrantyInfoRequest.token = actionBean.token;
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
        if (request.warrantyUuid == null) {
            return json(BaseResponseBean.CODE_FAILURE, "必要参数为空", response);
        }
        String interName = "获取保单详情";
        String result = commonAction.httpRequest(toWarrantyInfo, JsonKit.bean2Json(request), interName,request.token);
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
        String loginToken = actionBean.token;
        WarrantyRecord warrantyRecord = new WarrantyRecord();
        warrantyRecord.cust_id = Long.valueOf(actionBean.userId);
        warrantyRecord.day_start = TimeKit.currentTimeMillis();//获取当前时间戳(毫秒值)
        warrantyRecord.day_end = TimeKit.getDayEndTime();//获取当天结束时间戳(毫秒值)
        //TODO 获取当天的投保单(最新一条保单记录)
        WarrantyRecord insureResult = warrantyRecordDao.findInsureResult(warrantyRecord);
        if (insureResult == null) {
            return json(BaseResponseBean.CODE_SUCCESS,  "没有投保记录", response);
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
