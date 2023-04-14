package com.minibyte.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @date 2021/4/13
 * @description 分布式锁key注解
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LockKey {
    /**
     * 如果动态参数在command对象中,那么就需要设置columns的值为command对象中的属性名可以为多个,否则不需要设置该值
     * <p>例1：public void test(@KeyParam({"id"}) MemberCommand member)
     * <p>例2：public void test(@KeyParam({"id","loginName"}) MemberCommand member)
     * <p>例3：public void test(@KeyParam String memberId)
     */
    String[] columns() default {"id"};
}
