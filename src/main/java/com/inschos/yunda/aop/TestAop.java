package com.inschos.yunda.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Aspect
public class TestAop {

    //当调用这个注解时，环绕通知
    @Around("@annotation(com.inschos.yunda.annotation.TestAnnotationBefore)")
    public void before() {
        System.out.println("前置增强......");
    }

    //当调用这个注解时，环绕通知
    @Around("@annotation(com.inschos.yunda.annotation.TestAnnotationAfter)")
    public void after() {
        System.out.println("后置增强......");
    }

    //当调用这个注解时，环绕通知
    @Around("@annotation(com.inschos.yunda.annotation.TestAnnotationAround)")
    public void around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long date = new Date().getTime();
        //方法之前
        System.out.println("方法执行开始时间"+date);

        //执行被增强的方法
        proceedingJoinPoint.proceed();

        //方法之后
        System.out.println("方法执行结束时间"+date);
    }

}
