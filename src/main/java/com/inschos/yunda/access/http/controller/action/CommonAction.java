package com.inschos.yunda.access.http.controller.action;

import com.inschos.yunda.access.http.controller.bean.ActionBean;
import com.inschos.yunda.access.http.controller.bean.BaseResponseBean;
import com.inschos.yunda.access.http.controller.bean.InsureSetupBean;
import com.inschos.yunda.access.http.controller.bean.StaffPersonBean;
import com.inschos.yunda.assist.kit.HttpClientKit;
import com.inschos.yunda.assist.kit.JsonKit;
import com.inschos.yunda.data.dao.*;
import com.inschos.yunda.model.InsureSetup;
import com.inschos.yunda.model.StaffPerson;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.inschos.yunda.access.http.controller.bean.IntersCommonUrlBean.*;

@Component
public class CommonAction extends BaseAction {

    private static final Logger logger = Logger.getLogger(CommonAction.class);

    @Autowired
    private StaffPersonDao staffPersonDao;

    /**
     * 通过token获取用户信息
     *
     * @param actionBean
     * @return
     */
    public String findUserInfo(ActionBean actionBean) {
        InsureSetupBean request = JsonKit.json2Bean(actionBean.body, InsureSetupBean.class);
        BaseResponseBean response = new BaseResponseBean();
        //判空
        if (request == null) {
            return json(BaseResponseBean.CODE_FAILURE, "参数解析失败", response);
        }
        StaffPerson staffPerson = new StaffPerson();
        staffPerson.cust_id = Long.valueOf(actionBean.userId);
        staffPerson.account_uuid = Long.valueOf(actionBean.accountUuid);
        StaffPerson staffPersonInfo = staffPersonDao.findStaffPersonInfo(staffPerson);
        if (staffPersonInfo == null) {
            InsureSetupBean.accountInfoRequest accountInfoRequest = new InsureSetupBean.accountInfoRequest();
            accountInfoRequest.custId = Long.valueOf(actionBean.userId);
            accountInfoRequest.accountUuid = Long.valueOf(actionBean.accountUuid);
            try {
                //TODO 请求http
                String accountInfoRes = HttpClientKit.post(toAccountInfo, JsonKit.bean2Json(accountInfoRequest));
                if (accountInfoRes == null) {
                    return json(BaseResponseBean.CODE_FAILURE, "获取用户信息接口请求失败", response);
                }
                InsureSetupBean.accountInfoResponse accountInfoResponse = JsonKit.json2Bean(accountInfoRes, InsureSetupBean.accountInfoResponse.class);
                if (accountInfoResponse.code == 500) {
                    return json(BaseResponseBean.CODE_FAILURE, "获取用户信息接口请求失败", response);
                }
                response.data = accountInfoResponse.data;
                return json(BaseResponseBean.CODE_SUCCESS, "接口请求成功", response);
            } catch (IOException e) {
                return json(BaseResponseBean.CODE_FAILURE, "获取用户信息接口请求失败", response);
            }
        } else {
            StaffPersonBean staffPersonBean = new StaffPersonBean();
            staffPersonBean.id = staffPersonInfo.id;
            staffPersonBean.custId = staffPersonInfo.cust_id;
            staffPersonBean.accountUuid = staffPersonInfo.account_uuid;
            staffPersonBean.loginToken = staffPersonInfo.login_token;
            staffPersonBean.name = staffPersonInfo.name;
            staffPersonBean.papersType = staffPersonInfo.papers_type;
            staffPersonBean.papersCode = staffPersonInfo.papers_code;
            staffPersonBean.phone = staffPersonInfo.phone;
            staffPersonBean.createdAt = staffPersonInfo.created_at;
            staffPersonBean.updatedAt = staffPersonInfo.updated_at;
            response.data = staffPersonBean;
            return json(BaseResponseBean.CODE_SUCCESS, "获取用户信息成功", response);
        }
    }
}
