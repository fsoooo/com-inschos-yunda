package com.inschos.yunda.access.http.controller.request;

import com.inschos.yunda.access.http.controller.action.CommonAction;
import com.inschos.yunda.access.http.controller.action.InsureBankAction;
import com.inschos.yunda.access.http.controller.action.InsureUserAction;
import com.inschos.yunda.access.http.controller.bean.ActionBean;
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
    @Autowired
    private InsureUserAction insureUserAction;
    @Autowired
    private InsureBankAction insureBankAction;

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
        return insureUserAction.findUserInfoByToken(actionBean);
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
        return insureBankAction.findBankSms(actionBean);
    }


}
