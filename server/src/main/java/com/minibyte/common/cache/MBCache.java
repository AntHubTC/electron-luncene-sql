package com.minibyte.common.cache;

import java.util.List;
import java.util.function.Supplier;

public interface MBCache<T> {
    /**
     * 通过cacheId拿取缓存对象
     * @param cacheId
     * @return
     */
    T getObj(String cacheId);

    /**
     * 通过cacheId设置缓存对象
     * @param cacheId
     * @param cacheObj
     */
    void setObj(String cacheId, T cacheObj);

    /**
     * 通过cacheId设置缓存对象。  如果缓存查询不到就从getObjHandler查询
     * @param cacheId
     * @param cacheObj
     * @param getObjHandler
     */
    void setObj(String cacheId, T cacheObj, Supplier<T> getObjHandler);

    /**
     * 清楚制定缓存
     * @param cacheId
     */
    void clearCache(String cacheId);

    /**
     * 清除所有缓存
     */
    void invalidateCache();

    List<T> listCacheObj();
}
