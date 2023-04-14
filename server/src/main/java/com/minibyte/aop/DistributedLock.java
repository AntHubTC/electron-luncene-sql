package com.minibyte.aop;

import com.minibyte.annotation.LockKey;
import com.minibyte.annotation.NeedLock;
import com.minibyte.common.exception.MBBizException;
import com.minibyte.util.RedisLock;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.UUID;

/**
 * @date 2021/4/14
 * @description
 */
@Slf4j
@Aspect
@Component
@Order(3)
public class DistributedLock {

    @Resource
    private RedisLock redisLock;

    @Pointcut("@annotation(com.minibyte.annotation.NeedLock)")
    public void lockAspect() {
    }

    @Around(value = "lockAspect()")
    public Object lockAround(ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        NeedLock needLock = method.getAnnotation(NeedLock.class);
        StringBuilder lockKey = new StringBuilder(needLock.prefix());
        if (!isNeed(args, method, lockKey)) {
            return pjp.proceed();
        }
        String value = UUID.randomUUID().toString();
        if (!redisLock.lock(lockKey.toString(), value, needLock.timeout(), needLock.timeUnit())) {
            log.info("acquire lock unsuccessfully, lockKey is {}", lockKey);
            throw new MBBizException("当前资源正在操作中，请稍后重试");
        }
        log.info("acquire lock successfully, lockKey is {}", lockKey);
        try {
            return pjp.proceed();
        } finally {
            redisLock.unlock(lockKey.toString(), value);
        }
    }

    private boolean isNeed(Object[] args, Method method, StringBuilder lockKey) throws NoSuchFieldException, IllegalAccessException {
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        boolean need = false;
        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                if (!Objects.equals(annotation.annotationType(), LockKey.class)) {
                    continue;
                }
                String[] columns = ((LockKey) annotation).columns();
                if (columns.length == 0) {
                    if (null == args[i]) {
                        log.error("动态参数不能为null!");
                        throw new MBBizException("无法完成操作");
                    }
                    lockKey.append(args[i]);
                } else {
                    for (String column : columns) {
                        Object clz = args[i];
                        Field declaredField = clz.getClass().getDeclaredField(column);
                        declaredField.setAccessible(true);
                        Object value = declaredField.get(clz);
                        if (null == value || StringUtils.isEmpty(value.toString())) {
                            log.error("分布式锁key为空");
                            throw new MBBizException("无法完成操作");
                        }
                        lockKey.append(value);
                    }
                }
                need = true;
            }
        }
        return need;
    }

}
