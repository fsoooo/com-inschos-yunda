package com.inschos.yunda.access.http.controller.request;

import com.inschos.yunda.annotation.*;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * User: wangsl
 * Date: 2018/07/19
 * Time: 16:12
 * 测试面向切面
 */
@Controller
@RequestMapping("/test")
public class TestController {

    private static final Logger logger = Logger.getLogger(TestController.class);


    @TestAnnotationBefore
    @RequestMapping("/aop/before/**")
    @ResponseBody
    public String before(HttpServletRequest request) {
        return "add...........";
    }

    @TestAnnotationAfter
    @RequestMapping("/aop/after/**")
    @ResponseBody
    public String after(HttpServletRequest request) {
        return "add...........";
    }


    @TestAnnotationAround
    @RequestMapping("/aop/around/**")
    @ResponseBody
    public String around(HttpServletRequest request) {
        return "add...........";
    }

    @TestAnnotationAfterThrowing
    @RequestMapping("/aop/throw/**")
    @ResponseBody
    public String afterThrowing(HttpServletRequest request) {
        return "add...........";
    }

    @TestAnnotationAfterReturning
    @RequestMapping("/aop/return/**")
    @ResponseBody
    public String afterReturning(HttpServletRequest request) {
        return "add...........";
    }

}
