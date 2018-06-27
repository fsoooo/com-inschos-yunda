package com.inschos.yunda.access.http.controller.action;

import com.inschos.yunda.access.http.controller.bean.*;
import com.inschos.yunda.assist.kit.HttpClientKit;
import com.inschos.yunda.assist.kit.JsonKit;
import com.inschos.yunda.data.dao.BankVerifyDao;
import com.inschos.yunda.data.dao.StaffPersonDao;
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

    @Autowired
    private BankVerifyDao bankVerifyDao;

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
        String userInfoRes = findUserInfoInter(staffPerson);
        return userInfoRes;
    }

    /**
     * 通过cust_id,account_id获取用户信息
     *
     * @param request
     * @return
     */
    public String findUserInfoById(InsureSetupBean.accountInfoRequest request) {
        BaseResponseBean response = new BaseResponseBean();
        //判空
        if (request == null) {
            return json(BaseResponseBean.CODE_FAILURE, "参数解析失败", response);
        }
        StaffPerson staffPerson = new StaffPerson();
        staffPerson.cust_id = request.custId;
        staffPerson.account_uuid = request.accountUuid;
        String userInfoRes = findUserInfoInter(staffPerson);
        return userInfoRes;
    }

    /**
     * 通过cust_id,account_id获取用户信息
     *
     * @param request
     * @return
     */
    public String findUserInfoInter(StaffPerson request) {
        BaseResponseBean response = new BaseResponseBean();
        StaffPerson staffPerson = new StaffPerson();
        staffPerson.cust_id = request.cust_id;
        staffPerson.account_uuid = request.account_uuid;
        StaffPerson staffPersonInfo = staffPersonDao.findStaffPersonInfo(staffPerson);
        if (staffPersonInfo == null) {
            InsureSetupBean.accountInfoRequest accountInfoRequest = new InsureSetupBean.accountInfoRequest();
            accountInfoRequest.custId = request.cust_id;
            accountInfoRequest.accountUuid = request.account_uuid;
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


    /**
     * 银行卡获取短信验证码
     * TODO 英大接口返回结果的同时会返回一个验证参数,返回给端上同时存在数据库里
     * TODO 获取短信验证码必要参数校验:银行卡号和绑定手机号
     *
     * @param actionBean
     * @return
     */
    public String findBankSms(ActionBean actionBean) {
        InsureBankBean.bankRequest request = JsonKit.json2Bean(actionBean.body, InsureBankBean.bankRequest.class);
        BaseResponseBean response = new BaseResponseBean();
        if (request == null) {
            return json(BaseResponseBean.CODE_FAILURE, "参数解析失败", response);
        }


        InsureBankBean.bankSmsRequest bankSmsRequest = new InsureBankBean.bankSmsRequest();
        InsureSetupBean.accountInfoRequest accountInfoRequest = new InsureSetupBean.accountInfoRequest();
        accountInfoRequest.custId = Long.valueOf(actionBean.userId);
        accountInfoRequest.accountUuid = Long.valueOf(actionBean.accountUuid);
        //TODO 获取用户基本信息
        String userInfoRes = findUserInfoById(accountInfoRequest);
        BaseResponseBean baseResponseBean = JsonKit.json2Bean(userInfoRes, BaseResponseBean.class);

        bankSmsRequest.phone = request.phone;
        bankSmsRequest.bankCode = request.bankCode;
        bankSmsRequest.name = request.bankCode;
        bankSmsRequest.idCard = request.bankCode;
        try {
            //TODO 请求http
            String bankSmsRes = HttpClientKit.post(toBankSms, JsonKit.bean2Json(bankSmsRequest));
            if (bankSmsRes == null) {
                return json(BaseResponseBean.CODE_FAILURE, "获取银行卡绑定验证码接口请求失败", response);
            }
            InsureBankBean.bankSmsResponse bankSmsResponse = JsonKit.json2Bean(bankSmsRes, InsureBankBean.bankSmsResponse.class);
            if (bankSmsResponse.code == 500) {
                return json(BaseResponseBean.CODE_FAILURE, "获取银行卡绑定验证码接口请求失败", response);
            }
            response.data = bankSmsResponse.data;
            return json(BaseResponseBean.CODE_SUCCESS, "接口请求成功", response);
        } catch (IOException e) {
            return json(BaseResponseBean.CODE_FAILURE, "获取银行卡绑定验证码接口请求失败", response);
        }
    }

    /**
     * 校验银行卡短信验证码
     * TODO 返回类型是返回String 还是 boolean
     * TODO 有两个状态值:是否获取短信验证码 bankSmsStatus/是否验证通过短信验证码 verifyBankSmsStatus
     * TODO 把状态值放在数据库里,有时间限制,过期失效
     * <p>
     * TODO 新增银行验证码记录表,逻辑如下
     * TODO 1.每次发送验证码之前查询是否已发过或者是还在有效期内 findBankVerifyRepeat
     * TODO 2.验证码获取成功,添加记录,并返回给端上记录表id addBankVerify
     * TODO 3.校验验证码需要查询记录表获取verifyId findBankVerifyid
     * TODO 4.验证码校验成功,更新记录表 updateBankVerify
     *
     * @param actionBean
     * @return
     */
    public String verifyBankSms(ActionBean actionBean) {
        InsureBankBean.bankRequest request = JsonKit.json2Bean(actionBean.body, InsureBankBean.bankRequest.class);
        BaseResponseBean response = new BaseResponseBean();
        if (request == null) {
            return json(BaseResponseBean.CODE_FAILURE, "参数解析失败", response);
        }
        InsureBankBean.verifyBankSmsRequest verifyBankSmsRequest = new InsureBankBean.verifyBankSmsRequest();
        verifyBankSmsRequest.requestId = request.verifyId;
        verifyBankSmsRequest.vdCode = request.verifyCode;
        try {
            //TODO 请求http
            String verifyBankSmsRes = HttpClientKit.post(toVerifyBankSms, JsonKit.bean2Json(verifyBankSmsRequest));
            if (verifyBankSmsRes == null) {
                return json(BaseResponseBean.CODE_FAILURE, "校验银行卡绑定验证码接口请求失败", response);
            }
            InsureBankBean.verifyBankSmsResponse verifyBankSmsResponse = JsonKit.json2Bean(verifyBankSmsRes, InsureBankBean.verifyBankSmsResponse.class);
            if (verifyBankSmsResponse.code == 500) {
                return json(BaseResponseBean.CODE_FAILURE, "校验银行卡绑定验证码接口请求失败", response);
            }
            response.data = verifyBankSmsResponse.data;
            return json(BaseResponseBean.CODE_SUCCESS, "接口请求成功", response);
        } catch (IOException e) {
            return json(BaseResponseBean.CODE_FAILURE, "校验银行卡绑定验证码接口请求失败", response);
        }
    }
}
