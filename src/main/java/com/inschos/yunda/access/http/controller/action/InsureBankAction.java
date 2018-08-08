package com.inschos.yunda.access.http.controller.action;

import com.fasterxml.jackson.core.type.TypeReference;
import com.inschos.yunda.access.http.controller.bean.*;
import com.inschos.yunda.annotation.CheckParamsKit;
import com.inschos.yunda.assist.kit.HttpClientKit;
import com.inschos.yunda.assist.kit.JsonKit;
import com.inschos.yunda.data.dao.BankVerifyDao;
import com.inschos.yunda.data.dao.InsureSetupDao;
import com.inschos.yunda.model.BankVerify;
import com.inschos.yunda.model.InsureSetup;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.inschos.yunda.access.http.controller.bean.IntersCommonUrlBean.*;

@Component
public class InsureBankAction extends BaseAction {

    private static final Logger logger = Logger.getLogger(InsureBankAction.class);

    @Autowired
    private InsureSetupDao insureSetupDao;

    @Autowired
    private BankVerifyDao bankVerifyDao;

    @Autowired
    private CommonAction commonAction;

    @Autowired
    private InsureUserAction insureUserAction;

    /**
     * 银行卡授权公共方法
     *
     * @param request
     * @return String
     */
    public InsureBankBean.doBankAuthorizeResponse doBankAuthorize(InsureBankBean.doBankAuthorizeRequest request) {
        InsureBankBean.doBankAuthorizeResponse bankAuthorizeResponse = new InsureBankBean.doBankAuthorizeResponse();
        if (request.bankCode == null || request.bankPhone == null || request.idCard == null || request.Name == null || request.requestId == null || request.vdCode == null) {
            CheckParamsKit.Entry<String, String> defaultEntry = CheckParamsKit.getDefaultEntry();
            defaultEntry.details = "银行卡授权参数为空";
            List<CheckParamsKit.Entry<String, String>> list = new ArrayList<>();
            list.add(defaultEntry);
            bankAuthorizeResponse.message = list;
            bankAuthorizeResponse.code = 500;
            return bankAuthorizeResponse;
        }
        String interName = "银行卡授权";
        BankVerify bankVerify = new BankVerify();
        bankVerify.verify_id = request.requestId;
        bankVerify.verify_code = request.vdCode;
        BankVerify bankVerifyRes = bankVerifyDao.findBankVerify(bankVerify);
        if (bankVerifyRes != null) {
            //避免重复请求接口
            if (bankVerifyRes.verify_status == "2") {
                bankAuthorizeResponse.code = 200;
                bankAuthorizeResponse.data.verifyStatus = true;
                return bankAuthorizeResponse;
            }
        }
        String result = commonAction.httpRequest(toBankAuthorize, JsonKit.bean2Json(request), interName, request.token);
        InsureBankBean.doBankAuthorizeResponse bankAuthorizeReturnResponse = JsonKit.json2Bean(result, InsureBankBean.doBankAuthorizeResponse.class);
        if (bankAuthorizeReturnResponse.code == 200) {
            long date = new Date().getTime();
            bankVerify.verify_status = "2";
            bankVerify.updated_at = date;
            long updateRes = bankVerifyDao.updateBankVerify(bankVerify);
            bankAuthorizeResponse.code = 200;
            bankAuthorizeResponse.data.verifyStatus = true;
            return bankAuthorizeReturnResponse;
        } else {
            bankAuthorizeResponse.code = 500;
            bankAuthorizeResponse.data.verifyStatus = false;
            return bankAuthorizeReturnResponse;
        }
    }

    /**
     * 添加银行卡,要做英大短信验证
     * 新增银行验证码记录表,逻辑如下
     * 1.每次发送验证码之前查询是否已发过或者是还在有效期内 findBankVerifyRepeat
     * 2.验证码获取成功,添加记录,并返回给端上记录表id addBankVerify
     * 3.校验验证码需要查询记录表获取verifyId findBankVerifyid
     * 4.验证码校验成功,更新记录表 updateBankVerify
     *
     * @params actionBean
     */
    public String addBank(ActionBean actionBean) {
        InsureBankBean.bankRequest request = JsonKit.json2Bean(actionBean.body, InsureBankBean.bankRequest.class);
        BaseResponseBean response = new BaseResponseBean();
        String token = actionBean.token;
        if (request == null) {
            return json(BaseResponseBean.CODE_FAILURE, "参数解析失败", response);
        }
        if (request.bankCode == null || request.phone == null || request.verifyCode == null) {
            return json(BaseResponseBean.CODE_FAILURE, "必填参数为空", response);
        }
        InsureBankBean.addBankRequest addBankRequest = new InsureBankBean.addBankRequest();
        addBankRequest.bankName = request.bankName;
        addBankRequest.bankCode = request.bankCode;
        addBankRequest.bankPhone = request.phone;
        addBankRequest.bankCity = request.bankCity;
        //获取verifyId
        InsureBankBean.bankVerifyIdRequest bankVerifyIdRequest = new InsureBankBean.bankVerifyIdRequest();
        bankVerifyIdRequest.cust_id = Long.valueOf(actionBean.userId);
        bankVerifyIdRequest.bank_code = request.bankCode;
        bankVerifyIdRequest.bank_phone = request.phone;
        String verifyId = findBankVerifyId(bankVerifyIdRequest);
        //银行卡授权
        InsureBankBean.doBankAuthorizeRequest bankAuthorizeRequest = new InsureBankBean.doBankAuthorizeRequest();
        bankAuthorizeRequest.token = actionBean.token;
        bankAuthorizeRequest.Name = request.name;
        bankAuthorizeRequest.bankCode = request.bankCode;
        bankAuthorizeRequest.bankPhone = request.phone;
        bankAuthorizeRequest.requestId = request.verifyId;
        bankAuthorizeRequest.vdCode = request.verifyCode;
        InsureBankBean.doBankAuthorizeResponse bankAuthorizeResponse = doBankAuthorize(bankAuthorizeRequest);
        logger.info("校验验证码" + JsonKit.bean2Json(bankAuthorizeResponse));
        if (bankAuthorizeResponse.code != 200 || bankAuthorizeResponse.data.verifyStatus == false) {
            return json(BaseResponseBean.CODE_FAILURE, "银行卡授权失败", response);
        }
        String interName = "添加银行卡";
        String result = commonAction.httpRequest(toAddBank, JsonKit.bean2Json(addBankRequest), interName, actionBean.token);
        BaseResponseBean addBankResponse = JsonKit.json2Bean(result, BaseResponseBean.class);
        if(addBankResponse.code == 200){
            return json(BaseResponseBean.CODE_SUCCESS, interName + "成功", response);
        }else{
            return json(BaseResponseBean.CODE_SUCCESS, interName + "失败", response);
        }
    }

    /**
     * 获取银行卡列表,暂不考虑分页和状态(token)
     * TODO 获取token
     *
     * @return
     * @params actionBean
     */
    public String findBankList(ActionBean actionBean) {
        BaseResponseBean response = new BaseResponseBean();
        String interName = "获取银行卡列表";
        String result = commonAction.httpRequest(toBankList, "", interName, actionBean.token);
        InsureBankBean.bankListResponse bankListResponse = JsonKit.json2Bean(result, InsureBankBean.bankListResponse.class);
        response.data = bankListResponse.data;
        return json(BaseResponseBean.CODE_SUCCESS, interName + "成功", response);
    }

    /**
     * 获取银行卡详情
     *
     * @return
     * @params actionBean
     */
    public String findBankInfo(ActionBean actionBean) {
        InsureBankBean.bankRequest request = JsonKit.json2Bean(actionBean.body, InsureBankBean.bankRequest.class);
        BaseResponseBean response = new BaseResponseBean();
        if (request == null) {
            return json(BaseResponseBean.CODE_FAILURE, "参数解析失败", response);
        }
        InsureBankBean.bankInfoRequest bankInfoRequest = new InsureBankBean.bankInfoRequest();
        bankInfoRequest.id = request.bankId;
        String interName = "获取银行卡详情";
        String result = commonAction.httpRequest(toBankInfo, JsonKit.bean2Json(bankInfoRequest), interName, actionBean.token);
        InsureBankBean.bankInfoResponse bankInfoResponse = JsonKit.json2Bean(result, InsureBankBean.bankInfoResponse.class);
        response.data = bankInfoResponse.data;
        return json(BaseResponseBean.CODE_SUCCESS, interName + "成功", response);
    }

    /**
     * 获取银行卡数量(已经授权和正常使用的)
     *
     * @return
     * @params actionBean
     */
    public long findBankCount(ActionBean actionBean) {
        if (actionBean == null) {
            return -1;
        }
        if (actionBean.accountUuid == null || actionBean.userId == null) {
            return -1;
        }
        try {
            String bankListRes = HttpClientKit.post(toBankList, "");
            if (bankListRes == null) {
                return -1;
            }
            InsureBankBean.bankListResponse bankListResponse = JsonKit.json2Bean(bankListRes, InsureBankBean.bankListResponse.class);
            if (bankListResponse.code == 500) {
                return -1;
            }
            Object bankList = bankListResponse.data;
            List<InsureBankBean.bankInfoResponseData> bankLists = JsonKit.json2Bean(JsonKit.bean2Json(bankList), new TypeReference<List<InsureBankBean.bankInfoResponseData>>() {
            });
            long bankCount = 0;
            if (bankList != null) {
                bankCount = bankLists.size();
            }
            return bankCount;
        } catch (IOException e) {
            return -1;
        }
    }

    /**
     * 更改银行卡状态
     * 逻辑如下：
     * 1.银行卡有两种状态:默认使用状态(bankUseStatus:默认是1没有使用/使用中2)和授权状态(bankAuthorizeStatus:默认是1正常/2删除)
     * 2.当只剩下一张银行卡时,不能取消授权
     * 3.当所有银行卡都取消授权,本地库要更改银行卡授权状态
     * 4.当改变默认使用银行卡时,要做银行卡短信验证,然后变更本地库
     * 5.取消授权，需要请求删除接口
     * 6.本地库记录默认使用银行卡，当默认使用银行卡改变是修改本地库
     *
     * @return
     * @params actionBean
     */
    public String updateBankStatus(ActionBean actionBean) {
        InsureBankBean.bankRequest request = JsonKit.json2Bean(actionBean.body, InsureBankBean.bankRequest.class);
        BaseResponseBean response = new BaseResponseBean();
        if (request == null) {
            return json(BaseResponseBean.CODE_FAILURE, "参数解析失败", response);
        }
        long bankCount = findBankCount(actionBean);
        if (bankCount < 0) {
            return json(BaseResponseBean.CODE_FAILURE, "获取银行卡信息失败", response);
        }
        long date = new Date().getTime();
        InsureSetup insureSetup = new InsureSetup();
        if (bankCount == 0) {
            //修改本地库的银行卡授权状态
            insureSetup.cust_id = Long.valueOf(actionBean.userId);
            insureSetup.authorize_status = 1;//未授权
            insureSetup.updated_at = date;
            long updateRes = insureSetupDao.updateInsureAuthorize(insureSetup);
            return json(BaseResponseBean.CODE_FAILURE, "您没有可用银行卡,请绑定后使用", response);
        }
        if (bankCount == 1) {
            if (request.bankAuthorizeStatus == 2) {
                return json(BaseResponseBean.CODE_FAILURE, "您还剩最后一张可使用银行卡", response);
            }
        }
        InsureBankBean.updateBankStatusRequest updateBankStatusRequest = new InsureBankBean.updateBankStatusRequest();
        updateBankStatusRequest.id = request.bankId;
        updateBankStatusRequest.bankCode = request.bankCode;
        updateBankStatusRequest.bankName = request.bankName;
        updateBankStatusRequest.bankPhone = request.phone;
        updateBankStatusRequest.bankCity = request.bankCity;
        //当更换默认支付银行卡时,要做银行卡短信检验
        if (request.bankUseStatus == 2) {
            //获取verifyId
            InsureBankBean.bankVerifyIdRequest bankVerifyIdRequest = new InsureBankBean.bankVerifyIdRequest();
            bankVerifyIdRequest.cust_id = Long.valueOf(actionBean.userId);
            bankVerifyIdRequest.bank_code = request.bankCode;
            bankVerifyIdRequest.bank_phone = request.phone;
            String verifyId = findBankVerifyId(bankVerifyIdRequest);
            //银行卡授权
            InsureBankBean.doBankAuthorizeRequest bankAuthorizeRequest = new InsureBankBean.doBankAuthorizeRequest();
            bankAuthorizeRequest.token = actionBean.token;
            bankAuthorizeRequest.Name = request.name;
            bankAuthorizeRequest.bankCode = request.bankCode;
            bankAuthorizeRequest.bankPhone = request.phone;
            bankAuthorizeRequest.requestId = request.verifyId;
            bankAuthorizeRequest.vdCode = request.verifyCode;
            InsureBankBean.doBankAuthorizeResponse bankAuthorizeResponse = doBankAuthorize(bankAuthorizeRequest);
            logger.info("校验验证码" + JsonKit.bean2Json(bankAuthorizeResponse));
            if (bankAuthorizeResponse.code != 200 || bankAuthorizeResponse.data.verifyStatus == false) {
                return json(BaseResponseBean.CODE_FAILURE, "银行卡授权失败", response);
            }
        }
        String interName = "更新银行卡状态";
        String result = "";
        if(request.bankAuthorizeStatus == 2){
            result = commonAction.httpRequest(toDeleteBank, JsonKit.bean2Json(updateBankStatusRequest), interName, actionBean.token);

        }
        BaseResponseBean bankStatusResponse = JsonKit.json2Bean(result, BaseResponseBean.class);
        //根据接口返回状态,修改本地库的银行卡授权状态
        if (request.bankUseStatus == 2) {
            insureSetup.cust_id = Long.valueOf(actionBean.userId);
            insureSetup.authorize_status = 2;//授权
            insureSetup.authorize_bank = updateBankStatusRequest.bankCode;
            insureSetup.updated_at = date;
            long updateRes = insureSetupDao.updateInsureAuthorize(insureSetup);
        }
        response.data = bankStatusResponse.data;
        return json(BaseResponseBean.CODE_SUCCESS, interName + "成功", response);
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
        InsureUserBean.userInfoRequest userInfoRequest = new InsureUserBean.userInfoRequest();
        userInfoRequest.token = actionBean.token;
        InsureUserBean.userInfoResponse userInfoResponse = insureUserAction.findUserInfoById(userInfoRequest);
        logger.info("获取个人信息(银)" + JsonKit.bean2Json(userInfoResponse));
        if (userInfoResponse.code != 200 && userInfoResponse.data == null) {
            return json(BaseResponseBean.CODE_FAILURE, "获取验证码失败,个人信息无法获取", response);
        }
        bankSmsRequest.bankPhone = request.phone;
        bankSmsRequest.bankCode = request.bankCode;
        bankSmsRequest.name = userInfoResponse.data.name;
        bankSmsRequest.idCard = userInfoResponse.data.papersCode;
        bankSmsRequest.origin = origin;
        logger.info("获取验证码信息：" + JsonKit.bean2Json(bankSmsRequest));
        //判断是否已经发过验证码，避免重复发送
        BankVerify bankVerify = new BankVerify();
        long date = new Date().getTime();
        bankVerify.cust_id = Long.valueOf(actionBean.userId);
        bankVerify.bank_code = request.bankCode;
        bankVerify.bank_phone = request.bankCode;
        BankVerify bankVerifyRepeat = bankVerifyDao.findBankVerify(bankVerify);
        if (bankVerifyRepeat != null) {
            //判断验证码是否已经过期,过期时间5分钟(暂定五分钟)
            if (bankVerifyRepeat.verify_time + 60 * 5 * 1000 > date) {
                return json(BaseResponseBean.CODE_FAILURE, "您已获取验证码成功,请稍后重试", response);
            }
        }
        String interName = "获取银行卡绑定验证码";
        String result = commonAction.httpRequest(toBankSms, JsonKit.bean2Json(bankSmsRequest), interName, actionBean.token);
        InsureBankBean.bankSmsResponse bankSmsResponse = JsonKit.json2Bean(result, InsureBankBean.bankSmsResponse.class);
        if (bankSmsResponse.code != 200) {
            String reason = "";
            if (bankSmsResponse.message != null) {
                for (CheckParamsKit.Entry<String, String> stringStringEntry : bankSmsResponse.message) {
                    reason = reason + "," + stringStringEntry.details;
                }
            }
            return json(BaseResponseBean.CODE_FAILURE, interName + "接口请求成功 " + reason, response);
        }
        //数据库添加记录
        bankVerify.verify_id = bankSmsResponse.data.requestId;
        bankVerify.verify_code = "";
        bankVerify.verify_time = date;
        bankVerify.verify_status = "1";//验证码验证状态：默认1未验证/2验证成功
        bankVerify.created_at = date;
        bankVerify.updated_at = date;
        long addBankVerifyRes = bankVerifyDao.addBankVerify(bankVerify);
        response.data = bankSmsResponse.data;
        return json(BaseResponseBean.CODE_SUCCESS, interName + "接口请求成功", response);
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
     * 银行卡授权操作
     *
     * @return
     * @params actionBean
     */
    public String findBankAuthorize(ActionBean actionBean) {
        InsureSetupBean.doBankAuthorizeRequest request = JsonKit.json2Bean(actionBean.body, InsureSetupBean.doBankAuthorizeRequest.class);
        BaseResponseBean response = new BaseResponseBean();
        if (request == null) {
            return json(BaseResponseBean.CODE_FAILURE, "参数解析失败", response);
        }
        InsureBankBean.doBankAuthorizeRequest bankAuthorizeRequest = new InsureBankBean.doBankAuthorizeRequest();
        bankAuthorizeRequest.token = actionBean.token;
        bankAuthorizeRequest.Name = request.name;
        bankAuthorizeRequest.bankCode = request.bankCode;
        bankAuthorizeRequest.bankPhone = request.phone;
        if (request.verifyId == null) {
            InsureBankBean.bankVerifyIdRequest bankVerifyIdRequest = new InsureBankBean.bankVerifyIdRequest();
            bankVerifyIdRequest.cust_id = Long.valueOf(actionBean.userId);
            bankVerifyIdRequest.bank_code = request.bankCode;
            bankVerifyIdRequest.bank_phone = request.phone;
            request.verifyId = findBankVerifyId(bankVerifyIdRequest);
        }
        bankAuthorizeRequest.requestId = request.verifyId;
        bankAuthorizeRequest.vdCode = request.verifyCode;
        InsureBankBean.doBankAuthorizeResponse bankAuthorizeResponse = doBankAuthorize(bankAuthorizeRequest);
        if (bankAuthorizeResponse == null || bankAuthorizeResponse.code != 200 || !bankAuthorizeResponse.data.verifyStatus) {
            return json(BaseResponseBean.CODE_FAILURE, "银行卡授权失败", response);
        }
        return json(BaseResponseBean.CODE_SUCCESS, "银行卡授权成功", response);
    }
}








