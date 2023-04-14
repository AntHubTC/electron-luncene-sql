package com.minibyte.config;

import lombok.Getter;


public class RedisCacheNameConfig {

    public final static String RM_FRAME_VENDOR_CACHE = "test_test";

    public enum CacheTTLConfig {

        RM_FRAME_VENDOR_CACHE_CONFIG(RM_FRAME_VENDOR_CACHE, 2 * 60 * 60L);

        @Getter
        private String cacheName;//缓存名称

        @Getter
        private Long ttlSecond;//过期时间 单位秒

        private CacheTTLConfig(String cacheName, Long ttlSecond) {
            this.cacheName = cacheName;
            this.ttlSecond = ttlSecond;
        }
    }
}
