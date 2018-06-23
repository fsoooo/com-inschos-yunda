package com.inschos.yunda.access.http.controller.request;

import com.inschos.yunda.access.http.controller.action.IntersAction;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * User: wangsl
 * Date: 2018/06/23
 * Time: 17:12
 * 韵达项目对外接口:联合登录接口,授权查询接口,预投保接口
 */
@Controller
@RequestMapping("/webapi")
//TODO 路由统一也用小驼峰命名规则
public class InsureClaimController {

    private static final Logger logger = Logger.getLogger(InsureClaimController.class);


    //我的理赔
    //13.获取理赔列表
    //14.获取理赔详情
    //15.获取理赔状态
    //我要理赔
    //16.理赔提交（多步操作）
    //理赔审核
    //17.获取理赔详情
    //18.理赔审核


}
