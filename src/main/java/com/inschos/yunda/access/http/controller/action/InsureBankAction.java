package com.inschos.yunda.access.http.controller.action;

import com.inschos.yunda.access.http.controller.bean.*;
import com.inschos.yunda.assist.kit.HttpClientKit;
import com.inschos.yunda.assist.kit.JsonKit;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.inschos.yunda.access.http.controller.bean.IntersCommonUrlBean.*;

@Component
public class InsureBankAction extends BaseAction {

    private static final Logger logger = Logger.getLogger(InsureBankAction.class);

    /**
     * 添加银行卡,TODO 要先做英大短信验证
     *
     * @params actionBean
     */
    public String addBank(ActionBean actionBean) {
        InsureBankBean.bankRequest request = JsonKit.json2Bean(actionBean.body, InsureBankBean.bankRequest.class);
        BaseResponseBean response = new BaseResponseBean();
        //判空
        if (request == null) {
            return json(BaseResponseBean.CODE_FAILURE, "参数解析失败", response);
        }
        InsureBankBean.addBankRequest addBankRequest = new InsureBankBean.addBankRequest();
        addBankRequest.custId = Long.valueOf(actionBean.userId);
        addBankRequest.accountUuid = Long.valueOf(actionBean.accountUuid);
        addBankRequest.name = request.name;
        addBankRequest.bankCode = request.bankCode;
        addBankRequest.phone = request.phone;
        try {
            //TODO 请求http
            String addBankRes = HttpClientKit.post(toAddBank, JsonKit.bean2Json(addBankRequest));
            if (addBankRes == null) {
                return json(BaseResponseBean.CODE_FAILURE, "添加银行卡接口请求失败", response);
            }
            InsureBankBean.addBankResponse addBankResponse = JsonKit.json2Bean(addBankRes, InsureBankBean.addBankResponse.class);
            if (addBankResponse.code == 500) {
                return json(BaseResponseBean.CODE_FAILURE, "添加银行卡接口请求失败", response);
            }
            response.data = addBankResponse.data;
            return json(BaseResponseBean.CODE_SUCCESS, "接口请求成功", response);
        } catch (IOException e) {
            return json(BaseResponseBean.CODE_FAILURE, "添加银行卡接口请求失败", response);
        }
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
        try {
            //TODO 请求http
            String bankListRes = HttpClientKit.post(toBankList, JsonKit.bean2Json(bankListRequest));
            if (bankListRes == null) {
                return json(BaseResponseBean.CODE_FAILURE, "获取银行卡列表接口请求失败", response);
            }
            InsureBankBean.bankListResponse bankListResponse = JsonKit.json2Bean(bankListRes, InsureBankBean.bankListResponse.class);
            if (bankListResponse.code == 500) {
                return json(BaseResponseBean.CODE_FAILURE, "获取银行卡列表接口请求失败", response);
            }
            response.data = bankListResponse.data;
            return json(BaseResponseBean.CODE_SUCCESS, "接口请求成功", response);
        } catch (IOException e) {
            return json(BaseResponseBean.CODE_FAILURE, "获取银行卡列表接口请求失败", response);
        }
    }

    /**
     * 获取银行卡详情
     *
     * @return
     * @params actionBean
     */
    public String findBankInfo(ActionBean actionBean) {
        InsureBankBean.bankRequest request = JsonKit.json2Bean(actionBean.body,  InsureBankBean.bankRequest.class);
        BaseResponseBean response = new BaseResponseBean();
        //判空
        if (request == null) {
            return json(BaseResponseBean.CODE_FAILURE, "参数解析失败", response);
        }
        InsureBankBean.bankInfoRequest bankInfoRequest = new InsureBankBean.bankInfoRequest();
        bankInfoRequest.bankId = request.bankId;
        bankInfoRequest.custId = Long.valueOf(actionBean.userId);
        bankInfoRequest.accountUuid = Long.valueOf(actionBean.accountUuid);
        try {
            //TODO 请求http
            String bankInfoRes = HttpClientKit.post(toBankInfo, JsonKit.bean2Json(bankInfoRequest));
            if (bankInfoRes == null) {
                return json(BaseResponseBean.CODE_FAILURE, "获取银行卡详情接口请求失败", response);
            }
            InsureBankBean.bankInfoResponse bankInfoResponse = JsonKit.json2Bean(bankInfoRes, InsureBankBean.bankInfoResponse.class);
            if (bankInfoResponse.code == 500) {
                return json(BaseResponseBean.CODE_FAILURE, "获取银行卡详情接口请求失败", response);
            }
            response.data = bankInfoResponse.data;
            return json(BaseResponseBean.CODE_SUCCESS, "接口请求成功", response);
        } catch (IOException e) {
            return json(BaseResponseBean.CODE_FAILURE, "获取银行卡详情接口请求失败", response);
        }
    }

    /**
     * 更改银行卡状态
     * 银行卡有两种状态:默认使用状态和授权状态(删除)
     * 当只剩下一张银行卡时,不能取消授权
     * 当所有银行卡都取消授权,本地库要更改银行卡授权状态
     * 当改变默认使用银行卡时,要做银行卡短信验证,然后变更本地库
     *
     * @return
     * @params actionBean
     */
    public String updateBankStatus(ActionBean actionBean) {
        InsureBankBean.bankRequest request = JsonKit.json2Bean(actionBean.body, InsureBankBean.bankRequest.class);
        BaseResponseBean response = new BaseResponseBean();
        //判空
        if (request == null) {
            return json(BaseResponseBean.CODE_FAILURE, "参数解析失败", response);
        }
        return json(BaseResponseBean.CODE_SUCCESS, "业务完善中...", response);
    }
}








