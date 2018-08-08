package com.inschos.yunda.access.http.controller.action;

import com.inschos.yunda.access.http.controller.bean.BaseResponseBean;
import com.inschos.yunda.access.http.controller.bean.InsurePrepareBean;
import com.inschos.yunda.access.http.controller.bean.TestBean;
import com.inschos.yunda.assist.kit.HttpKit;
import com.inschos.yunda.assist.kit.JsonKit;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class TestAction extends BaseAction {
    private static final Logger logger = Logger.getLogger(TestAction.class);

    public String CallBackYunda(HttpServletRequest httpServletRequest) {
        TestBean.Requset request = JsonKit.json2Bean(HttpKit.readRequestBody(httpServletRequest), TestBean.Requset.class);
        logger.info("韵达推送数据"+JsonKit.bean2Json(request));
        TestBean.Response response = new TestBean.Response();
        response.code = "200";
        response.remark = "";
        response.data = "";
        response.result = "true";
        return JsonKit.bean2Json(response);
    }
}
