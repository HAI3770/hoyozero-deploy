package com.hoyozero.deploy.annotation;

import java.lang.annotation.*;

/**
 * 防重复提交注解
 * 用于标记需要防止重复提交的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RepeatSubmit {
    
    /**
     * 间隔时间(毫秒)，默认3000毫秒内不可重复提交
     */
    long interval() default 3000;
    
    /**
     * 提示消息
     */
    String message() default "请勿重复提交，请稍后再试";
}
