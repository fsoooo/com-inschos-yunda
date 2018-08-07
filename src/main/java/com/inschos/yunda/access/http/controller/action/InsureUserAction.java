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
        logger.info("获取用户信息token："+request.token);
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
        logger.info("获取用户信息公共参数："+JsonKit.bean2Json(staffPerson));
        StaffPerson staffPersonInfo = staffPersonDao.findStaffPersonInfoByCode(staffPerson);
        InsureUserBean.userInfoResponse userInfoResponse = new InsureUserBean.userInfoResponse();
        String interName = "获取用户信息";
        InsureUserBean.userInfoResponseData userInfoResponseData = new InsureUserBean.userInfoResponseData();
        logger.info("获取用户信息公共参数-查库："+JsonKit.bean2Json(staffPersonInfo));
        if(staffPersonInfo != null&&staffPersonInfo.name!=null&&staffPersonInfo.papers_code!=null&&staffPersonInfo.phone!=null){
            userInfoResponseData.id = staffPersonInfo.id;
            userInfoResponseData.custId = staffPersonInfo.cust_id;
            userInfoResponseData.accountUuid = staffPersonInfo.account_uuid;
            userInfoResponseData.loginToken = staffPersonInfo.login_token;
            userInfoResponseData.name = staffPersonInfo.name;
            userInfoResponseData.papersType = staffPersonInfo.papers_type;
            userInfoResponseData.papersCode = staffPersonInfo.papers_code;
            userInfoResponseData.phone = staffPersonInfo.phone;
            userInfoResponseData.createdAt = staffPersonInfo.created_at;
            userInfoResponseData.updatedAt = staffPersonInfo.updated_at;
        }else{
            //没有查到用户信息,从接口里拿,然后插入数据同时返回
            InsureSetupBean.accountInfoRequest accountInfoRequest = new InsureSetupBean.accountInfoRequest();
            InsureSetupBean.accountInfoResponse accountInfoResponse = new InsureSetupBean.accountInfoResponse();
            if (staffPerson.login_token == null) {
                return userInfoResponse;
            }
            String result = commonAction.httpRequest(toAccountInfo, "", interName,staffPerson.login_token);
            accountInfoResponse = JsonKit.json2Bean(result, InsureSetupBean.accountInfoResponse.class);
            //获取数据成功,数据入库 TODO 需要判断,数据库里没有的插入，数据库里已经有的执行更新操作
            long date = new Date().getTime();
            staffPerson.cust_id = accountInfoResponse.data.custId;
            staffPerson.account_uuid = accountInfoResponse.data.accountUuid;
            staffPerson.login_token = accountInfoResponse.data.loginToken;
            staffPerson.name = accountInfoResponse.data.name;
            staffPerson.papers_code = accountInfoResponse.data.idCard;
            staffPerson.phone = accountInfoResponse.data.phone;
            staffPerson.created_at = date;
            staffPerson.updated_at = date;
            if(staffPersonInfo==null){
                long addRes = staffPersonDao.addStaffPerson(staffPerson);
                if (addRes != 0) {
                    userInfoResponseData.id = addRes;
                }
            }else{
                staffPerson.id = staffPerson.id;
                long updateRes = staffPersonDao.updateStaffPerson(staffPerson);
                userInfoResponseData.id = staffPersonInfo.id;
            }
            userInfoResponseData.custId = staffPerson.cust_id;
            userInfoResponseData.accountUuid = staffPerson.account_uuid;
            userInfoResponseData.loginToken = staffPerson.login_token;
            userInfoResponseData.name = staffPerson.name;
            userInfoResponseData.papersType = staffPerson.papers_type;
            userInfoResponseData.papersCode = staffPerson.papers_code;
            userInfoResponseData.phone = staffPerson.phone;
            userInfoResponseData.createdAt = staffPerson.created_at;
            userInfoResponseData.updatedAt = staffPerson.updated_at;
        }
        userInfoResponse.data = userInfoResponseData;
        return userInfoResponse;
    }
}
