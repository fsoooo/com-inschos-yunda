package com.inschos.yunda.access.http.controller.action;

import com.inschos.yunda.access.http.controller.bean.ActionBean;
import com.inschos.yunda.access.http.controller.bean.BaseResponseBean;
import com.inschos.yunda.access.http.controller.bean.InsureBankBean;
import com.inschos.yunda.assist.kit.HttpClientKit;
import com.inschos.yunda.assist.kit.JsonKit;
import com.inschos.yunda.data.dao.InsureSetupDao;
import com.inschos.yunda.model.InsureSetup;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

import static com.inschos.yunda.access.http.controller.bean.IntersCommonUrlBean.*;

@Component
public class InsureBankAction extends BaseAction {

    private static final Logger logger = Logger.getLogger(InsureBankAction.class);

    @Autowired
    private CommonAction commonAction;

    @Autowired
    private InsureSetupDao insureSetupDao;

    /**
     * 添加银行卡,要做英大短信验证
     *
     * @params actionBean
     */
    public String addBank(ActionBean actionBean) {
        InsureBankBean.bankRequest request = JsonKit.json2Bean(actionBean.body, InsureBankBean.bankRequest.class);
        BaseResponseBean response = new BaseResponseBean();
        if (request == null) {
            return json(BaseResponseBean.CODE_FAILURE, "参数解析失败", response);
        }
        if (request.bankCode == null || request.phone == null || request.verifyCode == null) {
            return json(BaseResponseBean.CODE_FAILURE, "必填参数为空", response);
        }
        InsureBankBean.addBankRequest addBankRequest = new InsureBankBean.addBankRequest();
        addBankRequest.custId = Long.valueOf(actionBean.userId);
        addBankRequest.accountUuid = Long.valueOf(actionBean.accountUuid);
        addBankRequest.name = request.name;
        addBankRequest.bankCode = request.bankCode;
        addBankRequest.phone = request.phone;
        //获取verifyId
        InsureBankBean.bankVerifyIdRequest bankVerifyIdRequest = new InsureBankBean.bankVerifyIdRequest();
        bankVerifyIdRequest.cust_id = Long.valueOf(actionBean.userId);
        bankVerifyIdRequest.bank_code = request.bankCode;
        bankVerifyIdRequest.bank_phone = request.phone;
        String verifyId = commonAction.findBankVerifyId(bankVerifyIdRequest);
        //检验短信验证码
        InsureBankBean.bankRequest verifyBankSmsRequest = new InsureBankBean.bankRequest();
        verifyBankSmsRequest.verifyId = verifyId;
        verifyBankSmsRequest.verifyCode = request.verifyCode;
        InsureBankBean.verifyBankSmsResponse verifyBankSmsResponse = commonAction.verifyBankSms(verifyBankSmsRequest);
        if (!verifyBankSmsResponse.data.verifyStatus) {
            return json(BaseResponseBean.CODE_FAILURE, "短信验证码校验失败", response);
        }
        String interName = "添加银行卡";
        String result = commonAction.httpRequest(toAddBank, JsonKit.bean2Json(addBankRequest), interName);
        InsureBankBean.addBankResponse addBankResponse = JsonKit.json2Bean(result, InsureBankBean.addBankResponse.class);
        response.data = addBankResponse.data;
        return json(BaseResponseBean.CODE_SUCCESS, interName + "成功", response);
    }

    /**
     * 获取银行卡列表,暂不考虑分页和状态
     *
     * @return
     * @params actionBean
     */
    public String findBankList(ActionBean actionBean) {
        BaseResponseBean response = new BaseResponseBean();
        InsureBankBean.bankListRequest bankListRequest = new InsureBankBean.bankListRequest();
        bankListRequest.custId = Long.valueOf(actionBean.userId);
        bankListRequest.accountUuid = Long.valueOf(actionBean.accountUuid);
        String interName = "获取银行卡列表";
        String result = commonAction.httpRequest(toBankList, JsonKit.bean2Json(bankListRequest), interName);
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
        bankInfoRequest.bankId = request.bankId;
        bankInfoRequest.custId = Long.valueOf(actionBean.userId);
        bankInfoRequest.accountUuid = Long.valueOf(actionBean.accountUuid);
        String interName = "获取银行卡详情";
        String result = commonAction.httpRequest(toBankInfo, JsonKit.bean2Json(bankInfoRequest), interName);
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
        InsureBankBean.bankListRequest bankListRequest = new InsureBankBean.bankListRequest();
        bankListRequest.custId = Long.valueOf(actionBean.userId);
        bankListRequest.accountUuid = Long.valueOf(actionBean.accountUuid);
        try {
            String bankListRes = HttpClientKit.post(toBankList, JsonKit.bean2Json(bankListRequest));
            if (bankListRes == null) {
                return -1;
            }
            InsureBankBean.bankListResponse bankListResponse = JsonKit.json2Bean(bankListRes, InsureBankBean.bankListResponse.class);
            if (bankListResponse.code == 500) {
                return -1;
            }
            long bankCount = bankListResponse.data.bankCount;
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
        updateBankStatusRequest.custId = Long.valueOf(actionBean.userId);
        updateBankStatusRequest.accountUuid = Long.valueOf(actionBean.accountUuid);
        updateBankStatusRequest.name = request.name;
        updateBankStatusRequest.bankCode = request.bankCode;
        updateBankStatusRequest.phone = request.phone;
        updateBankStatusRequest.bankUseStatus = request.bankUseStatus;
        updateBankStatusRequest.bankAuthorizeStatus = request.bankAuthorizeStatus;
        //当更换默认支付银行卡时,要做银行卡短信检验
        if (updateBankStatusRequest.bankUseStatus == 2) {
            //获取verifyId
            InsureBankBean.bankVerifyIdRequest bankVerifyIdRequest = new InsureBankBean.bankVerifyIdRequest();
            bankVerifyIdRequest.cust_id = Long.valueOf(actionBean.userId);
            bankVerifyIdRequest.bank_code = request.bankCode;
            bankVerifyIdRequest.bank_phone = request.phone;
            String verifyId = commonAction.findBankVerifyId(bankVerifyIdRequest);
            //检验短信验证码
            InsureBankBean.bankRequest verifyBankSmsRequest = new InsureBankBean.bankRequest();
            verifyBankSmsRequest.verifyId = verifyId;
            verifyBankSmsRequest.verifyCode = request.verifyCode;
            InsureBankBean.verifyBankSmsResponse verifyBankSmsResponse = commonAction.verifyBankSms(verifyBankSmsRequest);
            if (!verifyBankSmsResponse.data.verifyStatus) {
                return json(BaseResponseBean.CODE_FAILURE, "短信验证码校验失败", response);
            }
        }
        String interName = "更新银行卡状态";
        String result = commonAction.httpRequest(toUpdateBank, JsonKit.bean2Json(updateBankStatusRequest), interName);
        InsureBankBean.updateBankStatusResponse bankStatusResponse = JsonKit.json2Bean(result, InsureBankBean.updateBankStatusResponse.class);
        //根据接口返回状态,修改本地库的银行卡授权状态
       if(updateBankStatusRequest.bankUseStatus==2){
            insureSetup.cust_id = Long.valueOf(actionBean.userId);
            insureSetup.authorize_status = 2;//授权
            insureSetup.authorize_bank = updateBankStatusRequest.bankCode;
            insureSetup.updated_at = date;
            long updateRes = insureSetupDao.updateInsureAuthorize(insureSetup);
        }
        response.data = bankStatusResponse.data;
        return json(BaseResponseBean.CODE_SUCCESS, interName + "成功", response);
    }
}








