package com.minibyte.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @date 2021/1/26
 * @description 基于lettuce实现的redis锁
 */
@Slf4j
@Component
public class RedisLock {


    /**
     * 释放锁脚本，原子操作，lua脚本
     */
    private static final String UNLOCK_LUA;
    /**
     * 默认过期时间(30ms)
     */
    private static final long DEFAULT_EXPIRE = 30L;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    static {
        StringBuilder sb = new StringBuilder();
        sb.append("if redis.call(\"get\",KEYS[1]) == ARGV[1] ");
        sb.append("then ");
        sb.append("    return redis.call(\"del\",KEYS[1]) ");
        sb.append("else ");
        sb.append("    return 0 ");
        sb.append("end ");
        UNLOCK_LUA = sb.toString();
    }

    /**
     * 获取分布式锁，原子操作
     *
     * @param lockKey   锁
     * @param lockValue 唯一ID, 可以使用UUID.randomUUID().toString();
     * @return 是否枷锁成功
     */
    public boolean lock(String lockKey, String lockValue) {
        log.info("redis lock，key:{} value:{}", lockKey, lockValue);
        return this.lock(lockKey, lockValue, DEFAULT_EXPIRE, TimeUnit.SECONDS);
    }

    /**
     * 获取分布式锁，原子操作
     *
     * @param lockKey   锁
     * @param lockValue 唯一ID, 可以使用UUID.randomUUID().toString();
     * @param expire    过期时间
     * @param timeUnit  时间单位
     * @return 是否枷锁成功
     */
    public boolean lock(String lockKey, String lockValue, long expire, TimeUnit timeUnit) {
        try {
            RedisCallback<Boolean> callback = (connection) -> connection.set(lockKey.getBytes(StandardCharsets.UTF_8),
                    lockValue.getBytes(StandardCharsets.UTF_8), Expiration.seconds(timeUnit.toSeconds(expire)),
                    RedisStringCommands.SetOption.SET_IF_ABSENT);
            return redisTemplate.execute(callback);
        } catch (Exception e) {
            log.error("redis lock error ,lock key: {}, value : {}, error info : {}", lockKey, lockValue, e);
        }
        return false;
    }

    /**
     * 释放锁
     *
     * @param lockKey   锁
     * @param lockValue 唯一ID
     * @return 执行结果
     */
    public boolean unlock(String lockKey, String lockValue) {
        log.info("redis unlock，key:{} value:{}", lockKey, lockValue);
        try {
            RedisCallback<Boolean> callback = (connection) -> connection.eval(
                    UNLOCK_LUA.getBytes(), ReturnType.BOOLEAN, 1, lockKey.getBytes(StandardCharsets.UTF_8),
                    lockValue.getBytes(StandardCharsets.UTF_8));
            return redisTemplate.execute(callback);
        } catch (Exception e) {
            log.error("redis unlock error ,unlock key: {}, unlock : {}, error info : {}", lockKey, lockValue, e);
        }
        return false;
    }

    /**
     * 获取Redis锁的value值
     *
     * @param lockKey 锁
     */
    public String get(String lockKey) {
        try {
            RedisCallback<String> callback = (connection) -> new String(Objects.requireNonNull(
                    connection.get(lockKey.getBytes())), StandardCharsets.UTF_8);
            return redisTemplate.execute(callback);
        } catch (Exception e) {
            log.error("get redis value occurred an exception,the key is {}, error is {}", lockKey, e);
        }
        return null;
    }

    /**
     * key计算
     *
     * @param prefix prefix
     * @param var1   var1
     * @return result
     */
    public String genKey(String prefix, String var1) {
        return prefix + var1;
    }

}
