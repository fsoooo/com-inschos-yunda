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
     *
     * @param url       请求地址
     * @param json      请求报文,格式是json
     * @param interName 接口名称
     * @param token     接口token
     * @return String
     */
    public String httpRequest(String url, String json, String interName, String token) {
        BaseResponseBean response = new BaseResponseBean();
        if (interName == null) {
            interName = "";
        }
        try {
            logger.info(interName + "接口请求地址：" + url);
            logger.info(interName + "接口请求参数：" + json);
            String result = HttpClientKit.post(url + "?token=" + token,json);
            if (result == null) {
                return json(BaseResponseBean.CODE_FAILURE, interName + "接口请求失败", response);
            }
            logger.info(interName + "接口返回数据：" + result);
            if (!isJSONValid(result)) {
                return json(BaseResponseBean.CODE_FAILURE, interName + "接口返回报文解析失败", response);
            }
            return result;
//            response = JsonKit.json2Bean(result, BaseResponseBean.class);
//            return json(BaseResponseBean.CODE_SUCCESS, interName + "接口请求成功", response);
        } catch (IOException e) {
            e.printStackTrace();
            return json(BaseResponseBean.CODE_FAILURE, interName + "接口请求失败", response);
        }
    }
}
