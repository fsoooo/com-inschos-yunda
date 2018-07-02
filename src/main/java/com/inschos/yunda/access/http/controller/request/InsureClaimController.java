package com.inschos.yunda.access.http.controller.request;

import com.inschos.yunda.access.http.controller.action.InsureClaimAction;
import com.inschos.yunda.access.http.controller.action.InsureClaimTkAction;
import com.inschos.yunda.access.http.controller.action.InsureClaimYdAction;
import com.inschos.yunda.access.http.controller.bean.ActionBean;
import com.inschos.yunda.annotation.GetActionBeanAnnotation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * User: wangsl
 * Date: 2018/06/29
 * Time: 17:12
 * 韵达项目-我的理赔:理赔发起提交，理赔审核，理赔记录查询
 */
@Controller
@RequestMapping("/webapi")
//TODO 路由统一也用小驼峰命名规则
public class InsureClaimController {

    private static final Logger logger = Logger.getLogger(InsureClaimController.class);

    @Autowired
    private InsureClaimAction insureClaimAction;
    @Autowired
    private InsureClaimTkAction insureClaimTkAction;
    @Autowired
    private InsureClaimYdAction insureClaimYdAction;

    /**
     * 提交理赔申请信息
     *
     * @return json
     * @params actionBean
     * @access public
     */
    @GetActionBeanAnnotation
    @RequestMapping("/claimApplyPost/**")
    @ResponseBody
    public String claimApplyPost(ActionBean actionBean) {
        return insureClaimYdAction.claimApplyPost(actionBean);
    }

    /**
     * 提交理赔申请资料
     *
     * @return json
     * @params actionBean
     * @access public
     */
    @GetActionBeanAnnotation
    @RequestMapping("/claimMaterialPost/**")
    @ResponseBody
    public String claimMaterialPost(ActionBean actionBean) {
        return insureClaimYdAction.claimMaterialPost(actionBean);
    }

    /**
     * 获取理赔进度列表
     *
     * @return json
     * @params actionBean
     * @access public
     */
    @GetActionBeanAnnotation
    @RequestMapping("/findClaimProgressList/**")
    @ResponseBody
    public String findClaimProgressList(ActionBean actionBean) {
        return insureClaimAction.findClaimProgressList(actionBean);
    }

    /**
     * 获取理赔进度列表
     *
     * @return json
     * @params actionBean
     * @access public
     */
    @GetActionBeanAnnotation
    @RequestMapping("/findClaimProgressInfo/**")
    @ResponseBody
    public String findClaimProgressInfo(ActionBean actionBean) {
        return insureClaimAction.findClaimProgressInfo(actionBean);
    }

    /**
     * 获取理赔审核页面信息
     *
     * @return json
     * @params actionBean
     * @access public
     */
    @GetActionBeanAnnotation
    @RequestMapping("/findClaimVerifyInfo/**")
    @ResponseBody
    public String findClaimVerifyInfo(ActionBean actionBean) {
        return insureClaimYdAction.findClaimVerifyInfo(actionBean);
    }

    /**
     * 理赔审核页面提交
     *
     * @return json
     * @params actionBean
     * @access public
     */
    @GetActionBeanAnnotation
    @RequestMapping("/doClaimVirify/**")
    @ResponseBody
    public String doClaimVirify(ActionBean actionBean) {
        return insureClaimYdAction.doClaimVirify(actionBean);
    }
}
