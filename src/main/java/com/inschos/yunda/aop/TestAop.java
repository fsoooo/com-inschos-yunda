package com.inschos.yunda.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Aspect
public class TestAop {

    //当调用这个注解时，环绕通知
    @Before("@annotation(com.inschos.yunda.annotation.TestAnnotationBefore)")
    public void before() {
        System.out.println("前置增强......");
    }

    //当调用这个注解时，环绕通知
    @After("@annotation(com.inschos.yunda.annotation.TestAnnotationAfter)")
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

    //异常抛出时
    @AfterThrowing("@annotation(com.inschos.yunda.annotation.TestAnnotationAfterThrowing)")
    // 声明ex时指定的类型会限制目标方法必须抛出指定类型的异常
    // 此处将ex的类型声明为Throwable，意味着对目标方法抛出的异常不加限制
    public void afterThrowing()
    {
        System.out.println("目标方法中抛出的异常");

    }

    //执行完成后
    @AfterReturning("@annotation(com.inschos.yunda.annotation.TestAnnotationAfterReturning)")
    public void afterReturn(){
        System.out.println("方法执行完成......");
    }

}
