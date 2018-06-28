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
            BaseResponseBean baseResponse = JsonKit.json2Bean(result, BaseResponseBean.class);
            if (baseResponse.code != 200) {
                return json(BaseResponseBean.CODE_FAILURE, interName + "接口请求失败", response);
            }
            response.data = baseResponse.data;
            return json(BaseResponseBean.CODE_SUCCESS, interName + "接口请求成功", response);
        } catch (IOException e) {
            return json(BaseResponseBean.CODE_FAILURE, interName + "接口请求失败", response);
        }
    }

    /**
     * 通过token获取用户信息
     *
     * @param actionBean
     * @return
     */
    public String findUserInfo(ActionBean actionBean) {
        InsureSetupBean request = JsonKit.json2Bean(actionBean.body, InsureSetupBean.class);
        BaseResponseBean response = new BaseResponseBean();
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
        StaffPersonBean staffPersonBean = new StaffPersonBean();
        String interName = "获取用户信息";
        if (staffPersonInfo == null) {
            //没有查到用户信息,从接口里拿,然后插入数据同时返回
            InsureSetupBean.accountInfoRequest accountInfoRequest = new InsureSetupBean.accountInfoRequest();
            accountInfoRequest.custId = request.cust_id;
            accountInfoRequest.accountUuid = request.account_uuid;

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
                staffPersonBean.id = addRes;
            }
            staffPersonBean.custId = staffPerson.cust_id;
            staffPersonBean.accountUuid = staffPerson.account_uuid;
            staffPersonBean.loginToken = staffPerson.login_token;
            staffPersonBean.name = staffPerson.name;
            staffPersonBean.papersType = staffPerson.papers_type;
            staffPersonBean.papersCode = staffPerson.papers_code;
            staffPersonBean.phone = staffPerson.phone;
            staffPersonBean.createdAt = staffPerson.created_at;
            staffPersonBean.updatedAt = staffPerson.updated_at;
            response.data = staffPersonBean;
        } else {
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
        }
        return json(BaseResponseBean.CODE_SUCCESS, interName + "成功", response);
    }

    /**
     * 获取微信签约状态
     *
     * @param request
     * @return
     */
    public PayCommonBean.findWecahtContractResponse findWechatContractStatus(PayCommonBean.findWecahtContractRequset request) {
        String interName = "获取微信签约状态";
        String result = httpRequest(toAuthorizeQueryWechat, JsonKit.bean2Json(request), interName);
        PayCommonBean.findWecahtContractResponse wecahtContractResponse = JsonKit.json2Bean(result, PayCommonBean.findWecahtContractResponse.class);
        return wecahtContractResponse;
    }

    /**
     * 执行微信签约操作(获取微信签约URL)
     *
     * @param request
     * @return
     */
    public PayCommonBean.doWecahtContractResponse doWechatContract(PayCommonBean.doWecahtContractRequset request) {
        String interName = "执行微信签约操作";
        String result = httpRequest(toAuthorizeQueryWechat, JsonKit.bean2Json(request), interName);
        PayCommonBean.doWecahtContractResponse response = JsonKit.json2Bean(result, PayCommonBean.doWecahtContractResponse.class);
        return response;
    }

    /**
     * 获取银行卡授权状态
     *
     * @param request
     * @return
     */
    public PayCommonBean.findBankAuthorizeResponse findBankAuthorizeStatus(PayCommonBean.findBankAuthorizeRequset request) {
        String interName = "获取银行卡授权状态";
        String result = httpRequest(toAuthorizeQueryWechat, JsonKit.bean2Json(request), interName);
        PayCommonBean.findBankAuthorizeResponse response = JsonKit.json2Bean(result, PayCommonBean.findBankAuthorizeResponse.class);
        return response;
    }

    /**
     * 执行银行卡授权
     *
     * @param request
     * @return
     */
    public PayCommonBean.doBankAuthorizeResponse doBankAuthorize(PayCommonBean.doBankAuthorizeRequset request) {
        String interName = "执行银行卡授权";
        String result = httpRequest(toAuthorizeQueryWechat, JsonKit.bean2Json(request), interName);
        PayCommonBean.doBankAuthorizeResponse response = JsonKit.json2Bean(result, PayCommonBean.doBankAuthorizeResponse.class);
        return response;
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
        InsureSetupBean.accountInfoRequest accountInfoRequest = new InsureSetupBean.accountInfoRequest();
        accountInfoRequest.custId = Long.valueOf(actionBean.userId);
        accountInfoRequest.accountUuid = Long.valueOf(actionBean.accountUuid);
        //获取用户基本信息
        String userInfoRes = findUserInfoById(accountInfoRequest);
        StaffPersonBean.userInfoResponse userInfoResponse = JsonKit.json2Bean(userInfoRes, StaffPersonBean.userInfoResponse.class);
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
    public String verifyBankSms(InsureBankBean.bankRequest request) {
        BaseResponseBean response = new BaseResponseBean();
        String interName = "校验银行卡短信验证码";
        if (request == null) {
            return json(BaseResponseBean.CODE_FAILURE, "参数解析失败", response);
        }
        if (request.verifyId == null || request.verifyCode == null) {
            return json(BaseResponseBean.CODE_FAILURE, "必要参数不能为空", response);
        }
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
                return json(BaseResponseBean.CODE_SUCCESS, interName + "成功", response);
            }
        }
        String result = httpRequest(toVerifyBankSms, JsonKit.bean2Json(verifyBankSmsRequest), interName);
        InsureBankBean.verifyBankSmsResponse verifyBankSmsResponse = JsonKit.json2Bean(result, InsureBankBean.verifyBankSmsResponse.class);
        response.data = verifyBankSmsResponse.data;
        long date = new Date().getTime();
        bankVerify.verify_status = "2";
        bankVerify.updated_at = date;
        long updateRes = bankVerifyDao.updateBankVerify(bankVerify);
        return json(BaseResponseBean.CODE_SUCCESS, interName + "成功", response);
    }

}
