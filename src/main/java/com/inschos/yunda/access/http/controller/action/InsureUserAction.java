package com.inschos.yunda.access.http.controller.action;

import com.inschos.yunda.access.http.controller.bean.*;
import com.inschos.yunda.annotation.CheckParamsKit;
import com.inschos.yunda.assist.kit.*;
import com.inschos.yunda.data.dao.*;
import com.inschos.yunda.model.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.inschos.yunda.access.http.controller.bean.IntersCommonUrlBean.*;

@Component
public class InsureUserAction extends BaseAction {
    private static final Logger logger = Logger.getLogger(CommonAction.class);
    @Autowired
    private StaffPersonDao staffPersonDao;

    @Autowired
    private CommonAction commonAction;

    /**
     * 通过token获取用户信息
     *
     * @param actionBean
     * @return
     */
    public String findUserInfoByToken(ActionBean actionBean) {
        BaseResponseBean response = new BaseResponseBean();
        InsureUserBean.userInfoRequest userInfoRequset = new  InsureUserBean.userInfoRequest();
        InsureUserBean.userInfoResponse userInfoResponse = new InsureUserBean.userInfoResponse();
        userInfoRequset.custId = Long.valueOf(actionBean.userId);
        userInfoRequset.accountUuid = Long.valueOf(actionBean.accountUuid);
        userInfoResponse = findUserInfoById(userInfoRequset);
        response.data = userInfoResponse.data;
        return json(BaseResponseBean.CODE_SUCCESS, "接口请求成功", response);
    }

    /**
     * 通过cust_id,account_id获取用户信息
     *
     * @param request
     * @return
     */
    public InsureUserBean.userInfoResponse findUserInfoById(InsureUserBean.userInfoRequest request) {
        StaffPerson staffPerson = new StaffPerson();
        staffPerson.cust_id = request.custId;
        staffPerson.account_uuid = request.accountUuid;
        InsureUserBean.userInfoResponse userInfoResponse =  findUserInfoCommon(staffPerson);
        return userInfoResponse;
    }

    /**
     * 通过name,idCode,phone获取用户信息
     *
     * @param request
     * @return
     */
    public InsureUserBean.userInfoResponse findUserInfoByCode(InsureUserBean.userInfoRequest request) {
        StaffPerson staffPerson = new StaffPerson();
        staffPerson.name = request.name;
        staffPerson.papers_code = request.papersCode;
        staffPerson.phone = request.phone;
        InsureUserBean.userInfoResponse userInfoResponse =  findUserInfoCommon(staffPerson);
        return userInfoResponse;

    }

    /**
     * 获取用户信息公共参数
     * @param staffPerson
     * @return
     */
    private InsureUserBean.userInfoResponse findUserInfoCommon(StaffPerson staffPerson){
        StaffPerson staffPersonInfo = staffPersonDao.findStaffPersonInfoByCode(staffPerson);
        InsureUserBean.userInfoResponse userInfoResponse = new InsureUserBean.userInfoResponse();
        String interName = "获取用户信息";
        if (staffPersonInfo == null) {
            //没有查到用户信息,从接口里拿,然后插入数据同时返回
            InsureSetupBean.accountInfoRequest accountInfoRequest = new InsureSetupBean.accountInfoRequest();
            InsureSetupBean.accountInfoResponse accountInfoResponse = new InsureSetupBean.accountInfoResponse();
            if(staffPerson.cust_id!=0&&staffPerson.account_uuid!=0){
                accountInfoRequest.custId = staffPerson.cust_id;
                accountInfoRequest.accountUuid = staffPerson.account_uuid;
                String result = commonAction.httpRequest(toAccountInfo, JsonKit.bean2Json(accountInfoRequest), interName);
                accountInfoResponse = JsonKit.json2Bean(result, InsureSetupBean.accountInfoResponse.class);
            }else if(staffPerson.name!=null&&staffPerson.papers_code!=null&&staffPerson.phone!=null){
                accountInfoRequest.name = staffPerson.name;
                accountInfoRequest.idCard = staffPerson.papers_code;
                accountInfoRequest.phone = staffPerson.phone;
                String result = commonAction.httpRequest(toAccountInfo, JsonKit.bean2Json(accountInfoRequest), interName);
                accountInfoResponse = JsonKit.json2Bean(result, InsureSetupBean.accountInfoResponse.class);
            }
            //获取数据成功,数据入库
            long date = new Date().getTime();
            staffPerson.cust_id = accountInfoResponse.data.custId;
            staffPerson.account_uuid = accountInfoResponse.data.accountUuid;
            staffPerson.login_token = accountInfoResponse.data.loginToken;
            staffPerson.name = accountInfoResponse.data.name;
            staffPerson.papers_code = accountInfoResponse.data.idCard;
            staffPerson.phone = accountInfoResponse.data.phone;
            staffPerson.created_at = date;
            staffPerson.updated_at = date;
            long addRes = staffPersonDao.addStaffPerson(staffPerson);
            if (addRes != 0) {
                userInfoResponse.data.id = addRes;
            }
            userInfoResponse.data.custId = staffPerson.cust_id;
            userInfoResponse.data.accountUuid = staffPerson.account_uuid;
            userInfoResponse.data.loginToken = staffPerson.login_token;
            userInfoResponse.data.name = staffPerson.name;
            userInfoResponse.data.papersType = staffPerson.papers_type;
            userInfoResponse.data.papersCode = staffPerson.papers_code;
            userInfoResponse.data.phone = staffPerson.phone;
            userInfoResponse.data.createdAt = staffPerson.created_at;
            userInfoResponse.data.updatedAt = staffPerson.updated_at;
        } else {
            userInfoResponse.data.id = staffPersonInfo.id;
            userInfoResponse.data.custId = staffPersonInfo.cust_id;
            userInfoResponse.data.accountUuid = staffPersonInfo.account_uuid;
            userInfoResponse.data.loginToken = staffPersonInfo.login_token;
            userInfoResponse.data.name = staffPersonInfo.name;
            userInfoResponse.data.papersType = staffPersonInfo.papers_type;
            userInfoResponse.data.papersCode = staffPersonInfo.papers_code;
            userInfoResponse.data.phone = staffPersonInfo.phone;
            userInfoResponse.data.createdAt = staffPersonInfo.created_at;
            userInfoResponse.data.updatedAt = staffPersonInfo.updated_at;
        }
        return userInfoResponse;
    }
}
