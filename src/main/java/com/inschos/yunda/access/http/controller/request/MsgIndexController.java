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

    private static final Logger logger = Logger.getLogger(MsgModelController.class);
    @Autowired
    private MsgIndexAction msgIndexAction;

    /**
     * 发送消息
     *
     * @params title|标题
     * @params content|内容
     * @params attachment|附件:上传附件的URL,可为空
     * @params type|消息                            类型:系统通知1/保单助手2/理赔进度3/最新任务4/客户消息5/活动消息6/顾问消息7/
     * @params fromId|发件人ID
     * @params fromType|发件人类型:用户类型:个人用户           1/企业用户 2/代理人 3/业管用户 4
     * @params toId|收件人id
     * @params toType|收件人类型:用户类型:个人用户             1/企业用户 2/代理人 3/业管用户 4
     * @params channelId|渠道id
     * @params status|读取状态:标识消息                   是否已被读取,未读0/已读1.避免重复向收件箱表插入数据,默认为0
     * @params sendTime|发送时间:默认为空。需要延时发送的，发送时间不为空
     * @params parentId|消息父级id
     * @return json
     * @access public
     */
    @GetActionBeanAnnotation
    @RequestMapping("/add/**")
    @ResponseBody
    public String addMessage(ActionBean actionBean) {
        return msgIndexAction.addMessage(actionBean);
    }

    /**
     * 操作消息 （收件箱 读取和删除）
     *
     * @params messageId   消息 id
     * @params operateId   操作代码:默认为1（删除/已读），2（还原/未读）
     * @params operateType 操作类型:read 更改读取状态，del 更改删除状态
     * @params operateAll  操作全部：1是，2否
     * @return json
     * @access public
     */
    @GetActionBeanAnnotation
    @RequestMapping("/update/**")
    @ResponseBody
    public String updateMessage(ActionBean actionBean) {
        return msgIndexAction.updateMsgRec(actionBean);
    }
}
