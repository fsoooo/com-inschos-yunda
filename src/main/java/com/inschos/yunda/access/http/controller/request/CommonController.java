package com.inschos.yunda.access.http.controller.request;

import com.inschos.yunda.access.http.controller.bean.*;
import com.inschos.yunda.access.http.controller.action.*;
import com.inschos.yunda.annotation.GetActionBeanAnnotation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * User: wangsl
 * Date: 2018/06/22
 * Time: 17:12
 * 韵达项目-公共方法：获取用户基本信息和发送短信验证码
 */
@Controller
@RequestMapping("/webapi")
public class CommonController {

    private static final Logger logger = Logger.getLogger(CommonController.class);
    @Autowired
    private CommonAction commonAction;

    /**
     * 通过token获取用户信息
     *
     * @return
     * @params actionBean
     */
    @GetActionBeanAnnotation
    @RequestMapping("/findUserInfo/**")
    @ResponseBody
    public String findUserInfo(ActionBean actionBean) {
        return commonAction.findUserInfo(actionBean);
    }

    /**
     * 获取银行卡绑定验证码
     *
     * @return
     * @params actionBean
     */
    @GetActionBeanAnnotation
    @RequestMapping("/findBankSms/**")
    @ResponseBody
    public String findBankSms(ActionBean actionBean) {
        return commonAction.findBankSms(actionBean);
    }


}
