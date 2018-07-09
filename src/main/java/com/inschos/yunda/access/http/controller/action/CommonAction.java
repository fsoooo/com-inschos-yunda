package com.inschos.yunda.access.http.controller.action;

import com.inschos.yunda.access.http.controller.bean.BaseResponseBean;
import com.inschos.yunda.access.http.controller.bean.CommonBean;
import com.inschos.yunda.assist.kit.HttpClientKit;
import com.inschos.yunda.assist.kit.JsonKit;
import com.inschos.yunda.data.dao.BankVerifyDao;
import com.inschos.yunda.data.dao.StaffPersonDao;
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
     * http请求公共函数
     * TODO 接口返回的code,服务请求方自行判断
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
            response = JsonKit.json2Bean(result, BaseResponseBean.class);
//            if (response.code != 200 || response.code == 500) {
//                return json(BaseResponseBean.CODE_FAILURE, interName + "接口服务请求失败", response);
//            }
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
}
