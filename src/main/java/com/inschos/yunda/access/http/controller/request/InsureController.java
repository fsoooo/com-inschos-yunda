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
 * Date: 2018/06/19
 * Time: 17:12
 * 韵达项目对外接口:联合登录接口,授权查询接口,预投保接口
 */
@Controller
@RequestMapping("/webapi")
//TODO 路由统一也用小驼峰命名规则
public class InsureController {

    private static final Logger logger = Logger.getLogger(InsureController.class);

}
