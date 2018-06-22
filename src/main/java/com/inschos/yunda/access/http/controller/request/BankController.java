package com.inschos.yunda.access.http.controller.request;

import com.inschos.yunda.access.http.controller.action.BankAction;
import com.inschos.yunda.access.http.controller.action.IntersAction;
import com.inschos.yunda.access.http.controller.bean.ActionBean;
import com.inschos.yunda.annotation.GetActionBeanAnnotation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * User: wangsl
 * Date: 2018/06/22
 * Time: 17:12
 * 韵达项目对外接口:联合登录接口,授权查询接口,预投保接口
 */
@Controller
@RequestMapping("/webapi")
//TODO 路由统一也用小驼峰命名规则
public class BankController {

    private static final Logger logger = Logger.getLogger(BankController.class);
    @Autowired
    private BankAction bankAction;

    /**
     * 添加银行卡
     *
     * @return
     * @params actionBean
     */
    @GetActionBeanAnnotation
    @RequestMapping("/addBank/**")
    @ResponseBody
    public String addBank(ActionBean actionBean) {
        return bankAction.addBank(actionBean);
    }

    /**
     * 获取银行卡列表
     *
     * @return
     * @params actionBean
     */
    @GetActionBeanAnnotation
    @RequestMapping("/findBankList/**")
    @ResponseBody
    public String findBankList(ActionBean actionBean) {
        return bankAction.findBankList(actionBean);
    }

    /**
     * 获取银行卡详情
     *
     * @return
     * @params actionBean
     */
    @GetActionBeanAnnotation
    @RequestMapping("/findBankInfo/**")
    @ResponseBody
    public String findBankInfo(ActionBean actionBean) {
        return bankAction.findBankInfo(actionBean);
    }

    /**
     * 获取银行卡状态
     *
     * @return
     * @params actionBean
     */
    @GetActionBeanAnnotation
    @RequestMapping("/findBankStatus/**")
    @ResponseBody
    public String findBankStatus(ActionBean actionBean) {
        return bankAction.findBankStatus(actionBean);
    }

    /**
     * 更改银行卡状态
     *
     * @return
     * @params actionBean
     */
    @GetActionBeanAnnotation
    @RequestMapping("/updateBankStatus/**")
    @ResponseBody
    public String updateBankStatus(ActionBean actionBean) {
        return bankAction.updateBankStatus(actionBean);
    }

    /**
     * 获取银行卡转账授权书详情
     *
     * @return
     * @params actionBean
     */
    @GetActionBeanAnnotation
    @RequestMapping("/findBankAuthorizeInfo/**")
    @ResponseBody
    public String findBankAuthorizeInfo(ActionBean actionBean) {
        return bankAction.findBankAuthorizeInfo(actionBean);
    }

    /**
     * 银行卡授权操作
     *
     * @return
     * @params actionBean
     */
    @GetActionBeanAnnotation
    @RequestMapping("/doBankAuthorize/**")
    @ResponseBody
    public String doBankAuthorize(ActionBean actionBean) {
        return bankAction.doBankAuthorize(actionBean);
    }


}
