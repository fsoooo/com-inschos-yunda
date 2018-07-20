package com.inschos.yunda.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Aspect
public class TestAopConfig {

    public void before() {
        System.out.println("(配置文件)前置增强......");
    }

}
