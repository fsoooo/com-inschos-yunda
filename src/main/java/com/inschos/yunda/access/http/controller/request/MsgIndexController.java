package com.inschos.yunda.access.http.controller.request;

import com.inschos.yunda.access.http.controller.action.MsgIndexAction;
import com.inschos.yunda.access.http.controller.bean.ActionBean;
import com.inschos.yunda.annotation.GetActionBeanAnnotation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * User: wangsl
 * Date: 2018/05/11
 * Time: 17:12
 * 发送消息逻辑:系统消息，订阅消息，私信，可以上传附件
 */
@Controller
@RequestMapping("/message")
//TODO 路由统一也用小驼峰命名规则
public class MsgIndexController {

    private static final Logger logger = Logger.getLogger(MsgIndexController.class);
    @Autowired
    private MsgIndexAction msgIndexAction;

    /**
     * test
     *
     * @params actionBean
     * @return json
     * @access public
     *
     */
    @GetActionBeanAnnotation
    @RequestMapping("/add/**")
    @ResponseBody
    public String addMessage(ActionBean actionBean) {
        return msgIndexAction.addMessage(actionBean);
    }
}
