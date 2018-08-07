package com.inschos.yunda.access.http.controller.action;

import com.inschos.yunda.access.http.controller.bean.ActionBean;
import com.inschos.yunda.access.http.controller.bean.BaseResponseBean;
import com.inschos.yunda.access.http.controller.bean.InsureSetupBean;
import com.inschos.yunda.access.http.controller.bean.InsureUserBean;
import com.inschos.yunda.assist.kit.JsonKit;
import com.inschos.yunda.data.dao.StaffPersonDao;
import com.inschos.yunda.model.StaffPerson;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.inschos.yunda.access.http.controller.bean.IntersCommonUrlBean.toAccountInfo;

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
        InsureUserBean.userInfoRequest userInfoRequset = new InsureUserBean.userInfoRequest();
        InsureUserBean.userInfoResponse userInfoResponse = new InsureUserBean.userInfoResponse();
        userInfoRequset.token = actionBean.token;
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
        staffPerson.login_token = request.token;
        logger.info("获取用户信息token：" + request.token);
        InsureUserBean.userInfoResponse userInfoResponse = findUserInfoCommon(staffPerson);
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
        InsureUserBean.userInfoResponse userInfoResponse = findUserInfoCommon(staffPerson);
        return userInfoResponse;

    }

    /**
     * 获取用户信息公共参数
     *
     * @param staffPerson
     * @return
     */
    private InsureUserBean.userInfoResponse findUserInfoCommon(StaffPerson staffPerson) {
        logger.info("获取用户信息公共参数：" + JsonKit.bean2Json(staffPerson));
        StaffPerson staffPersonInfo = null;
        if (staffPerson.phone != null) {
            staffPersonInfo = staffPersonDao.findStaffPersonInfoByPhone(staffPerson);
        } else if (staffPerson.papers_code != null) {
            staffPersonInfo = staffPersonDao.findStaffPersonInfoByCode(staffPerson);
        }
        InsureUserBean.userInfoResponse userInfoResponse = new InsureUserBean.userInfoResponse();
        String interName = "获取用户信息";
        InsureUserBean.userInfoResponseData userInfoResponseData = new InsureUserBean.userInfoResponseData();
        logger.info("获取用户信息公共参数-查库：" + JsonKit.bean2Json(staffPersonInfo));
        if (staffPersonInfo != null && staffPersonInfo.name != null && staffPersonInfo.papers_code != null && staffPersonInfo.phone != null) {
            userInfoResponseData.name = staffPersonInfo.name;
            userInfoResponseData.papersCode = staffPersonInfo.papers_code;
            userInfoResponseData.phone = staffPersonInfo.phone;
            userInfoResponseData.createdAt = staffPersonInfo.created_at;
            userInfoResponseData.updatedAt = staffPersonInfo.updated_at;
            logger.info("返回个人信息(查库)："+JsonKit.bean2Json(userInfoResponseData));
        } else {
            //没有查到用户信息,从接口里拿,然后插入数据同时返回
            InsureSetupBean.accountInfoRequest accountInfoRequest = new InsureSetupBean.accountInfoRequest();
            if (staffPerson.login_token == null) {
                return userInfoResponse;
            }
            String result = commonAction.httpRequest(toAccountInfo, "", interName, staffPerson.login_token);
            InsureSetupBean.accountInfoTokenResponse accountInfoData = JsonKit.json2Bean(result, InsureSetupBean.accountInfoTokenResponse.class);
            //获取数据成功,数据入库 TODO 需要判断,数据库里没有的插入，数据库里已经有的执行更新操作
            long date = new Date().getTime();
            staffPerson.name = accountInfoData.data.name;
            staffPerson.papers_code = accountInfoData.data.papersCode;
            staffPerson.phone = accountInfoData.data.phone;
            staffPerson.created_at = date;
            staffPerson.updated_at = date;
            if (staffPersonInfo == null) {
                long addRes = staffPersonDao.addStaffPerson(staffPerson);
                if (addRes != 0) {
                    userInfoResponseData.id = addRes;
                }
            } else {
                staffPerson.id = staffPerson.id;
                long updateRes = staffPersonDao.updateStaffPerson(staffPerson);
                userInfoResponseData.id = staffPersonInfo.id;
            }
            logger.info("返回个人信息(接口)："+JsonKit.bean2Json(accountInfoData));
            userInfoResponseData.name =  accountInfoData.data.name;
            userInfoResponseData.papersCode =  accountInfoData.data.papersCode;
            userInfoResponseData.phone =  accountInfoData.data.phone;
            userInfoResponseData.createdAt = date;
            userInfoResponseData.updatedAt = date;
            logger.info("返回个人信息(接口)："+JsonKit.bean2Json(userInfoResponseData));
        }
        logger.info("返回个人信息："+JsonKit.bean2Json(userInfoResponseData));
        userInfoResponse.data = userInfoResponseData;
        return userInfoResponse;
    }
}
