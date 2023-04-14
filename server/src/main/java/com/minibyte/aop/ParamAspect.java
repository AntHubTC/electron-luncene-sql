package com.minibyte.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Objects;
import java.util.Set;

/**
 * @date 2021/5/14
 * @description
 */
@Slf4j
@Aspect
@Component
@Order(1)
public class ParamAspect {

    @Resource
    private Validator validator;


    @Pointcut("@annotation(com.minibyte.annotation.NeedCheck)")
    public void paramCheck() {
    }

    @Before("paramCheck()")
    public void doBefore(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (Objects.isNull(args) || args.length == 0) {
            return;
        }
        for (Object o : args) {
            if (o == null) {
                continue;
            }
            Set<ConstraintViolation<Object>> result = validator.validate(o);
            if (result.size() > 0) {
                ConstraintViolation<Object> v = result.iterator().next();
                String message = v.getPropertyPath() + " " + v.getMessage();
                throw new ConstraintViolationException(message, result);
            }
        }
    }

    @AfterReturning(value = "paramCheck()", returning = "ret")
    public void doAfterReturning(Object ret) {
    }

    @Around("paramCheck()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        return joinPoint.proceed();
    }
}
