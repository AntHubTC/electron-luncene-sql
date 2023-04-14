package com.minibyte.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * @date 2021/4/13
 * @description 分布式锁注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NeedLock {
    String prefix() default "";

    long timeout() default 10;

    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
