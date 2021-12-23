package com.nxsp.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 用springmvc拦截带有此注解的方法，登录后才能访问
@Target(ElementType.METHOD)// 注解可以写在方法上
@Retention(RetentionPolicy.RUNTIME)// 注解在运行时有效
public @interface LoginRequired {



}
