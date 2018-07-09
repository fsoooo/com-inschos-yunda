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
    public String findUserInfo(ActionBean actionBean) {
        BaseResponseBean response = new BaseResponseBean();
        InsureUserBean.userInfoRequest userInfoRequset = new  InsureUserBean.userInfoRequest();
        InsureUserBean.userInfoResponse userInfoResponse = new InsureUserBean.userInfoResponse();
        userInfoRequset.custId = Long.valueOf(actionBean.userId);
        userInfoRequset.accountUuid = Long.valueOf(actionBean.accountUuid);
        //先查库,再请求接口
        userInfoResponse = findUserInfoById(userInfoRequset);
        if(userInfoResponse!=null||userInfoResponse.code==200){
            response.data = userInfoResponse.data;
            return json(BaseResponseBean.CODE_SUCCESS, "接口请求成功", response);
        }
        userInfoResponse = findUserInfoInterById(userInfoRequset);
        response.data = userInfoResponse.data;
        return json(BaseResponseBean.CODE_SUCCESS, "接口请求成功", response);
    }

    /**
     * 通过cust_id,account_id获取用户信息(查库)
     *
     * @param request
     * @return
     */
    public InsureUserBean.userInfoResponse findUserInfoById(InsureUserBean.userInfoRequest request) {
        InsureUserBean.userInfoResponse response = new InsureUserBean.userInfoResponse();
        if(request.custId==0||request.accountUuid==0){
            response.code = 500;
            CheckParamsKit.Entry<String, String> defaultEntry = CheckParamsKit.getDefaultEntry();
            defaultEntry.details = "必要参数为空";
            List<CheckParamsKit.Entry<String, String>> list = new ArrayList<>();
            list.add(defaultEntry);
            response.message = list;
            return response;
        }
        StaffPerson staffPerson = new StaffPerson();
        staffPerson.cust_id = request.custId;
        staffPerson.account_uuid = request.accountUuid;
        StaffPerson staffPersonInfo = staffPersonDao.findStaffPersonInfo(staffPerson);
        if(staffPerson==null){
            response.code = 500;
            CheckParamsKit.Entry<String, String> defaultEntry = CheckParamsKit.getDefaultEntry();
            defaultEntry.details = "获取用户信息失败";
            List<CheckParamsKit.Entry<String, String>> list = new ArrayList<>();
            list.add(defaultEntry);
            response.message = list;
            return response;
        }
        response.data.id = staffPersonInfo.id;
        response.data.custId = staffPersonInfo.cust_id;
        response.data.accountUuid = staffPersonInfo.account_uuid;
        response.data.loginToken = staffPersonInfo.login_token;
        response.data.name = staffPersonInfo.name;
        response.data.papersCode = staffPersonInfo.papers_code;
        response.data.phone = staffPersonInfo.phone;
        response.data.papersType = staffPersonInfo.papers_type;
        response.data.createdAt = staffPersonInfo.created_at;
        response.data.updatedAt = staffPersonInfo.updated_at;
        return response;
    }

    /**
     * 通过cust_id,account_id获取用户信息(接口)
     *
     * @param request
     * @return
     */
    public InsureUserBean.userInfoResponse findUserInfoInterById(InsureUserBean.userInfoRequest request) {
        StaffPerson staffPerson = new StaffPerson();
        staffPerson.cust_id = request.custId;
        staffPerson.account_uuid = request.accountUuid;
        StaffPerson staffPersonInfo = staffPersonDao.findStaffPersonInfo(staffPerson);
        InsureUserBean.userInfoResponse userInfoResponse = new InsureUserBean.userInfoResponse();
        String interName = "获取用户信息";
        if (staffPersonInfo == null) {
            //没有查到用户信息,从接口里拿,然后插入数据同时返回
            InsureSetupBean.accountInfoRequest accountInfoRequest = new InsureSetupBean.accountInfoRequest();
            accountInfoRequest.custId = request.custId;
            accountInfoRequest.accountUuid = request.accountUuid;
            String result = commonAction.httpRequest(toAccountInfo, JsonKit.bean2Json(accountInfoRequest), interName);
            InsureSetupBean.accountInfoResponse accountInfoResponse = JsonKit.json2Bean(result, InsureSetupBean.accountInfoResponse.class);
            //获取数据成功,数据入库
            long date = new Date().getTime();
            staffPerson.name = accountInfoResponse.data.name;
            staffPerson.papers_code = accountInfoResponse.data.idCard;
            staffPerson.phone = accountInfoResponse.data.phone;
            staffPerson.login_token = accountInfoResponse.data.loginToken;
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

    /**
     * 通过联合登录参数获取登录信息(cust_id,account_id,login_token)
     * @param request
     * @return
     */
    public String findUserInfoJointLogin(InsureUserBean.userInfoRequest request){
        BaseResponseBean response = new BaseResponseBean();
        InsureUserBean.userInfoRequest userInfoRequset = new  InsureUserBean.userInfoRequest();
        InsureUserBean.userInfoResponse userInfoResponse = new InsureUserBean.userInfoResponse();
        userInfoRequset.name = request.name;
        userInfoRequset.papersCode = request.papersCode;
        userInfoRequset.phone = request.phone;
        //先查库,再请求接口
        userInfoResponse = findUserInfoByCode(userInfoRequset);
        if(userInfoResponse!=null||userInfoResponse.code==200){
            response.data = userInfoResponse.data;
            return json(BaseResponseBean.CODE_SUCCESS, "接口请求成功", response);
        }
        userInfoResponse = findUserInfoInterByCode(userInfoRequset);
        response.data = userInfoResponse.data;
        return json(BaseResponseBean.CODE_SUCCESS, "接口请求成功", response);
    }

    /**
     * 通过cust_id,account_id获取用户信息(查库)
     *
     * @param request
     * @return
     */
    public InsureUserBean.userInfoResponse findUserInfoByCode(InsureUserBean.userInfoRequest request) {
        InsureUserBean.userInfoResponse response = new InsureUserBean.userInfoResponse();
        if(request.custId==0||request.accountUuid==0){
            response.code = 500;
            CheckParamsKit.Entry<String, String> defaultEntry = CheckParamsKit.getDefaultEntry();
            defaultEntry.details = "必要参数为空";
            List<CheckParamsKit.Entry<String, String>> list = new ArrayList<>();
            list.add(defaultEntry);
            response.message = list;
            return response;
        }
        StaffPerson staffPerson = new StaffPerson();
        staffPerson.name = request.name;
        staffPerson.papers_code = request.papersCode;
        staffPerson.phone = request.phone;
        StaffPerson staffPersonInfo = staffPersonDao.findStaffPersonInfo(staffPerson);
        if(staffPerson==null){
            response.code = 500;
            CheckParamsKit.Entry<String, String> defaultEntry = CheckParamsKit.getDefaultEntry();
            defaultEntry.details = "获取用户信息失败";
            List<CheckParamsKit.Entry<String, String>> list = new ArrayList<>();
            list.add(defaultEntry);
            response.message = list;
            return response;
        }
        response.data.id = staffPersonInfo.id;
        response.data.custId = staffPersonInfo.cust_id;
        response.data.accountUuid = staffPersonInfo.account_uuid;
        response.data.loginToken = staffPersonInfo.login_token;
        response.data.name = staffPersonInfo.name;
        response.data.papersCode = staffPersonInfo.papers_code;
        response.data.phone = staffPersonInfo.phone;
        response.data.papersType = staffPersonInfo.papers_type;
        response.data.createdAt = staffPersonInfo.created_at;
        response.data.updatedAt = staffPersonInfo.updated_at;
        return response;
    }

    /**
     * 通过cust_id,account_id获取用户信息(接口)
     *
     * @param request
     * @return
     */
    public InsureUserBean.userInfoResponse findUserInfoInterByCode(InsureUserBean.userInfoRequest request) {
        StaffPerson staffPerson = new StaffPerson();
        staffPerson.name = request.name;
        staffPerson.papers_code = request.papersCode;
        staffPerson.phone = request.phone;
        StaffPerson staffPersonInfo = staffPersonDao.findStaffPersonInfo(staffPerson);
        InsureUserBean.userInfoResponse userInfoResponse = new InsureUserBean.userInfoResponse();
        String interName = "获取用户信息";
        if (staffPersonInfo == null) {
            //没有查到用户信息,从接口里拿,然后插入数据同时返回
            InsureSetupBean.accountInfoRequest accountInfoRequest = new InsureSetupBean.accountInfoRequest();
            accountInfoRequest.custId = request.custId;
            accountInfoRequest.accountUuid = request.accountUuid;
            String result = commonAction.httpRequest(toAccountInfo, JsonKit.bean2Json(accountInfoRequest), interName);
            InsureSetupBean.accountInfoResponse accountInfoResponse = JsonKit.json2Bean(result, InsureSetupBean.accountInfoResponse.class);
            //获取数据成功,数据入库
            long date = new Date().getTime();
            staffPerson.name = accountInfoResponse.data.name;
            staffPerson.papers_code = accountInfoResponse.data.idCard;
            staffPerson.phone = accountInfoResponse.data.phone;
            staffPerson.login_token = accountInfoResponse.data.loginToken;
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
