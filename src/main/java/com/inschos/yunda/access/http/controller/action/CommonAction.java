package com.inschos.yunda.access.http.controller.action;

import com.inschos.yunda.access.http.controller.bean.*;
import com.inschos.yunda.assist.kit.HttpClientKit;
import com.inschos.yunda.assist.kit.JsonKit;
import com.inschos.yunda.data.dao.BankVerifyDao;
import com.inschos.yunda.data.dao.StaffPersonDao;
import com.inschos.yunda.model.BankVerify;
import com.inschos.yunda.model.StaffPerson;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

import static com.inschos.yunda.access.http.controller.bean.IntersCommonUrlBean.*;

@Component
public class CommonAction extends BaseAction {
    private static final Logger logger = Logger.getLogger(CommonAction.class);
    @Autowired
    private StaffPersonDao staffPersonDao;

    @Autowired
    private BankVerifyDao bankVerifyDao;

    /**
     * http请求公共函数
     *
     * @param url       请求地址
     * @param json      请求报文,格式是json
     * @param interName 接口名称
     * @return String
     */
    public String httpRequest(String url, String json, String interName) {
        BaseResponseBean response = new BaseResponseBean();
        if (interName == null) {
            interName = "";
        }
        try {
            String result = HttpClientKit.post(url, JsonKit.bean2Json(json));
            if (result == null) {
                return json(BaseResponseBean.CODE_FAILURE, interName + "接口请求失败", response);
            }
            if (!isJSONValid(result)) {
                return json(BaseResponseBean.CODE_FAILURE, interName + "接口返回报文解析失败", response);
            }
            BaseResponseBean baseResponse = JsonKit.json2Bean(result, BaseResponseBean.class);
            if (baseResponse.code != 200 || baseResponse.code == 500) {
                return json(BaseResponseBean.CODE_FAILURE, interName + "接口服务请求失败", response);
            }
            response.data = baseResponse.data;
            return json(BaseResponseBean.CODE_SUCCESS, interName + "接口请求成功", response);
        } catch (IOException e) {
            return json(BaseResponseBean.CODE_FAILURE, interName + "接口请求失败", response);
        }
    }

    /**
     * 获取授权/签约状态
     *
     * @param request
     * @return
     */
    public CommonBean.findAuthorizeResponse findWechatContractStatus(CommonBean.findAuthorizeRequset request) {
        String interName = "获取授权/签约状态";
        String result = httpRequest(toAuthorizeQuery, JsonKit.bean2Json(request), interName);
        CommonBean.findAuthorizeResponse authorizeResponse = JsonKit.json2Bean(result, CommonBean.findAuthorizeResponse.class);
        return authorizeResponse;
    }

    /**
     * 执行微信签约操作(获取微信签约URL)
     *
     * @param request
     * @return
     */
    public CommonBean.doWecahtContractResponse doWechatContract(CommonBean.doWecahtContractRequset request) {
        String interName = "执行微信签约操作";
        String result = httpRequest(toWechatContract, JsonKit.bean2Json(request), interName);
        CommonBean.doWecahtContractResponse response = JsonKit.json2Bean(result, CommonBean.doWecahtContractResponse.class);
        return response;
    }

    /**
     * 执行银行卡授权
     *
     * @param request
     * @return
     */
    public CommonBean.doBankAuthorizeResponse doBankAuthorize(CommonBean.doBankAuthorizeRequset request) {
        String interName = "执行银行卡授权";
        String result = httpRequest(toBankAuthorize, JsonKit.bean2Json(request), interName);
        CommonBean.doBankAuthorizeResponse response = JsonKit.json2Bean(result, CommonBean.doBankAuthorizeResponse.class);
        return response;
    }

    /**
     * 通过token获取用户信息
     *
     * @param actionBean
     * @return
     */
    public String findUserInfo(ActionBean actionBean) {
        BaseResponseBean response = new BaseResponseBean();
        InsureSetupBean request = JsonKit.json2Bean(actionBean.body, InsureSetupBean.class);
        CommonBean.findUserInfoRequset findUserInfoRequset = new CommonBean.findUserInfoRequset();
        findUserInfoRequset.userId = actionBean.userId;
        findUserInfoRequset.accountUuid = actionBean.accountUuid;
        CommonBean.findUserInfoResponse userInfoRes = findUserInfoInter(findUserInfoRequset);
        response.data = userInfoRes.data;
        return json(BaseResponseBean.CODE_SUCCESS, "接口请求成功", response);
    }

    /**
     * 通过cust_id,account_id获取用户信息
     *
     * @param request
     * @return
     */
    public CommonBean.findUserInfoResponse findUserInfoById(CommonBean.findUserInfoRequset request) {
        CommonBean.findUserInfoRequset findUserInfoRequset = new CommonBean.findUserInfoRequset();
        findUserInfoRequset.userId = request.userId;
        findUserInfoRequset.accountUuid = request.accountUuid;
        CommonBean.findUserInfoResponse userInfoRes = findUserInfoInter(findUserInfoRequset);
        return userInfoRes;
    }

    /**
     * 通过cust_id,account_id获取用户信息
     *
     * @param request
     * @return
     */
    public CommonBean.findUserInfoResponse findUserInfoInter(CommonBean.findUserInfoRequset request) {
        StaffPerson staffPerson = new StaffPerson();
        staffPerson.cust_id = Long.valueOf(request.userId);
        staffPerson.account_uuid = Long.valueOf(request.accountUuid);
        StaffPerson staffPersonInfo = staffPersonDao.findStaffPersonInfo(staffPerson);
        CommonBean.findUserInfoResponse userInfoResponse = new CommonBean.findUserInfoResponse();
        String interName = "获取用户信息";
        if (staffPersonInfo == null) {
            //没有查到用户信息,从接口里拿,然后插入数据同时返回
            InsureSetupBean.accountInfoRequest accountInfoRequest = new InsureSetupBean.accountInfoRequest();
            accountInfoRequest.custId = Long.valueOf(request.userId);
            accountInfoRequest.accountUuid = Long.valueOf(request.accountUuid);
            String result = httpRequest(toAccountInfo, JsonKit.bean2Json(accountInfoRequest), interName);
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
     * 获取银行卡绑定验证码
     * 英大接口返回结果的同时会返回一个验证参数,返回给端上同时存在数据库里
     * 是否获取短信验证码,根据获取验证码的时间来判断,有时间限制,过期失效:verify_time
     * 获取短信验证码必要参数校验:银行卡号和绑定手机号
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
        if (request.phone == null || request.bankCode == null) {
            return json(BaseResponseBean.CODE_FAILURE, "银行卡号和手机号不能为空", response);
        }
        InsureBankBean.bankSmsRequest bankSmsRequest = new InsureBankBean.bankSmsRequest();
        CommonBean.findUserInfoRequset findUserInfoRequset = new CommonBean.findUserInfoRequset();
        findUserInfoRequset.userId = actionBean.userId;
        findUserInfoRequset.accountUuid = actionBean.accountUuid;
        //获取用户基本信息
        CommonBean.findUserInfoResponse userInfoResponse = findUserInfoById(findUserInfoRequset);
        bankSmsRequest.phone = request.phone;
        bankSmsRequest.bankCode = request.bankCode;
        bankSmsRequest.name = userInfoResponse.data.name;
        bankSmsRequest.idCard = userInfoResponse.data.papersCode;
        //判断是否已经发过验证码，避免重复发送
        BankVerify bankVerify = new BankVerify();
        long date = new Date().getTime();
        bankVerify.cust_id = Long.valueOf(actionBean.userId);
        bankVerify.bank_code = request.bankCode;
        bankVerify.bank_phone = request.bankCode;
        BankVerify bankVerifyRepeat = bankVerifyDao.findBankVerifyRepeat(bankVerify);
        if (bankVerifyRepeat != null) {
            //判断验证码是否已经过期,过期时间5分钟(暂定五分钟)
            if (bankVerifyRepeat.verify_time + 60 * 5 * 1000 > date) {
                return json(BaseResponseBean.CODE_FAILURE, "您已获取验证码成功,请稍后重试", response);
            }
        }
        String interName = "获取银行卡绑定验证码";
        String result = httpRequest(toBankSms, JsonKit.bean2Json(bankSmsRequest), interName);
        InsureBankBean.bankSmsResponse bankSmsResponse = JsonKit.json2Bean(result, InsureBankBean.bankSmsResponse.class);
        //数据库添加记录
        bankVerify.verify_id = bankSmsResponse.data.requestId;
        bankVerify.verify_code = "";
        bankVerify.verify_time = date;
        bankVerify.verify_status = "1";//验证码验证状态：默认1未验证/2验证成功
        bankVerify.created_at = date;
        bankVerify.updated_at = date;
        long addBankVerifyRes = bankVerifyDao.addBankVerify(bankVerify);
        response.data = bankSmsResponse.data;
        return json(BaseResponseBean.CODE_SUCCESS, "接口请求成功", response);
    }

    /**
     * 获取verifyId
     *
     * @param request
     * @return
     */
    public String findBankVerifyId(InsureBankBean.bankVerifyIdRequest request) {
        if (request == null) {
            return "";
        }
        if (request.cust_id == 0 || request.bank_code == null || request.bank_phone == null) {
            return "";
        }
        long date = new Date().getTime();
        BankVerify bankVerifyId = new BankVerify();
        bankVerifyId.cust_id = request.cust_id;
        bankVerifyId.bank_code = request.bank_code;
        bankVerifyId.bank_phone = request.bank_phone;
        bankVerifyId.verify_status = "1";
        bankVerifyId.verify_time = date - 60 * 5 * 100;
        BankVerify bankVerify = bankVerifyDao.findBankVerifyId(bankVerifyId);
        if (bankVerify == null) {
            return "";
        } else {
            String verifyId = bankVerify.verify_id;
            return verifyId;
        }
    }

    /**
     * 校验银行卡短信验证码
     * <p>
     * TODO 新增银行验证码记录表,逻辑如下
     * TODO 1.每次发送验证码之前查询是否已发过或者是还在有效期内 findBankVerifyRepeat
     * TODO 2.验证码获取成功,添加记录,并返回给端上记录表id addBankVerify
     * TODO 3.校验验证码需要查询记录表获取verifyId findBankVerifyid
     * TODO 4.验证码校验成功,更新记录表 updateBankVerify
     *
     * @param request
     * @return String
     */
    public InsureBankBean.verifyBankSmsResponse verifyBankSms(InsureBankBean.bankRequest request) {
        BaseResponseBean response = new BaseResponseBean();
        InsureBankBean.verifyBankSmsResponse verifyResponse = new InsureBankBean.verifyBankSmsResponse();
        String interName = "校验银行卡短信验证码";
        InsureBankBean.verifyBankSmsRequest verifyBankSmsRequest = new InsureBankBean.verifyBankSmsRequest();
        verifyBankSmsRequest.requestId = request.verifyId;
        verifyBankSmsRequest.vdCode = request.verifyCode;
        BankVerify bankVerify = new BankVerify();
        bankVerify.verify_id = request.verifyId;
        bankVerify.verify_code = request.verifyCode;
        BankVerify bankVerifyRes = bankVerifyDao.findBankVerify(bankVerify);
        if (bankVerifyRes != null) {
            //避免重复请求接口
            if (bankVerifyRes.verify_status == "2") {
                verifyResponse.data.verifyStatus = true;
                return verifyResponse;
            }
        }
        String result = httpRequest(toVerifyBankSms, JsonKit.bean2Json(verifyBankSmsRequest), interName);
        InsureBankBean.verifyBankSmsResponse verifyBankSmsResponse = JsonKit.json2Bean(result, InsureBankBean.verifyBankSmsResponse.class);
        long date = new Date().getTime();
        bankVerify.verify_status = "2";
        bankVerify.updated_at = date;
        long updateRes = bankVerifyDao.updateBankVerify(bankVerify);
        return verifyResponse;
    }
}
