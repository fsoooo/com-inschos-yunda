package com.inschos.yunda.access.http.controller.request;

import com.inschos.yunda.access.http.controller.action.InsureSetupAction;
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
 * 韵达项目-投保设置:获取自动投保设置,更改自动投保设置,获取微信签约链接,获取微信授权书详情
 */
@Controller
@RequestMapping("/webapi")
public class InsureSetupController {

    private static final Logger logger = Logger.getLogger(InsureSetupController.class);
    @Autowired
    private InsureSetupAction insureSetupAction;

    /**
     * 获取自动投保状态
     *
     * @return json
     * @params actionBean
     * @access public
     */
    @GetActionBeanAnnotation
    @RequestMapping("/findInsureAutoStatus/**")
    @ResponseBody
    public String findInsureAutoStatus(ActionBean actionBean) {
        return insureSetupAction.findInsureAutoStatus(actionBean);
    }

    /**
     * 更改自动投保
     *
     * @return json
     * @params actionBean
     * @access public
     */
    @GetActionBeanAnnotation
    @RequestMapping("/updateInsureAutoStatus/**")
    @ResponseBody
    public String updateInsureAutoStatus(ActionBean actionBean) {
        return insureSetupAction.updateInsureAutoStatus(actionBean);
    }

    /**
     * 获取授权/签约状态
     *
     * @return json
     * @params actionBean
     * @access public
     */
    @GetActionBeanAnnotation
    @RequestMapping("/findAuthorizeStatus/**")
    @ResponseBody
    public String findAuthorizeStatus(ActionBean actionBean) {
        return insureSetupAction.findAuthorizeStatus(actionBean);
    }

    /**
     * 获取签约信息(url)
     *
     * @return json
     * @params actionBean
     * @access public
     */
    @GetActionBeanAnnotation
    @RequestMapping("/findWhetContractUrl/**")
    @ResponseBody
    public String findWhetContractUrl(ActionBean actionBean) {
        return insureSetupAction.findWhetContractUrl(actionBean);
    }
}
