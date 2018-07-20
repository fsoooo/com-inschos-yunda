package com.inschos.yunda.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定义一个注解
 * 当不同类型需要同一个注解时,可以用大括号包括多个@Target({ElementType.METHOD,ElementType.METHOD,...})
 * @Retention(RetentionPolicy.RUNTIME) 运行时使用
 * 注解按生命周期来划分可分为3类：
 * 1、RetentionPolicy.SOURCE：注解只保留在源文件，当Java文件编译成class文件的时候，注解被遗弃；
 * 2、RetentionPolicy.CLASS：注解被保留到class文件，但jvm加载class文件时候被遗弃，这是默认的生命周期；
 * 3、RetentionPolicy.RUNTIME：注解不仅被保存到class文件中，jvm加载class文件之后，仍然存在；
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TestAnnotationAfterReturning {
    boolean isCheckAccess() default true;
}
