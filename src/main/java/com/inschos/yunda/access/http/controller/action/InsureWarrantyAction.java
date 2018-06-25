package com.inschos.yunda.access.http.controller.action;


import com.inschos.yunda.access.http.controller.bean.*;
import com.inschos.yunda.assist.kit.JsonKit;
import com.inschos.yunda.data.dao.WarrantyRecordDao;
import com.inschos.yunda.model.WarrantyRecord;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;

@Component
public class InsureWarrantyAction extends BaseAction {

    private static final Logger logger = Logger.getLogger(InsureSetupAction.class);

    @Autowired
    private WarrantyRecordDao warrantyRecordDao;

    /**
     * 获取保单列表
     *
     * @param actionBean
     * @return
     */
    public String findInsureWarrantyList(ActionBean actionBean) {
        InsureSetupBean request = JsonKit.json2Bean(actionBean.body, InsureSetupBean.class);
        BaseResponseBean response = new BaseResponseBean();
        //判空
        if (request == null) {
            return json(BaseResponseBean.CODE_FAILURE, "参数解析失败", response);
        }
        return json(BaseResponseBean.CODE_SUCCESS, "业务完善中...", response);
    }

    /**
     * 获取保单详情
     *
     * @param actionBean
     * @return
     */
    public String findInsureWarrantyInfo(ActionBean actionBean) {
        InsureSetupBean request = JsonKit.json2Bean(actionBean.body, InsureSetupBean.class);
        BaseResponseBean response = new BaseResponseBean();
        //判空
        if (request == null) {
            return json(BaseResponseBean.CODE_FAILURE, "参数解析失败", response);
        }
        return json(BaseResponseBean.CODE_SUCCESS, "业务完善中...", response);
    }

    /**
     * 获取保单状态
     *
     * @param actionBean
     * @return
     */
    public String findInsureWarrantyStatus(ActionBean actionBean) {
        InsureSetupBean request = JsonKit.json2Bean(actionBean.body, InsureSetupBean.class);
        BaseResponseBean response = new BaseResponseBean();
        //判空
        if (request == null) {
            return json(BaseResponseBean.CODE_FAILURE, "参数解析失败", response);
        }
        return json(BaseResponseBean.CODE_SUCCESS, "业务完善中...", response);
    }

    /**
     * 获取投保设置详情
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
            return json(BaseResponseBean.CODE_FAILURE, "获取投保结果失败", response);
        }
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
