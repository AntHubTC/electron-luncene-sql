package com.minibyte.common.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * 这个工程主要是用来做自己的工具的，没必要使用三方缓存，自己简单实现一个就行了。
 * @param <T>
 */
public abstract class AbstractMBCache<T> implements MBCache<T> {

    private Map<String, T> cache = new ConcurrentHashMap();
//    Cache<String, T> cache = CacheUtil.newLRUCache(Integer.MAX_VALUE);
    private Map<String, Supplier<T>> getObjHandlerMap = new ConcurrentHashMap<>();

    @Override
    public T getObj(String cacheId) {
        T obj = cache.get(cacheId);
        if (obj == null) {
            Supplier<T> objSupplier = getObjHandlerMap.get(cacheId);
            if (objSupplier != null) {
                synchronized (objSupplier) {
                    setObj(cacheId, obj = objSupplier.get());
                }
            }
        }
        return obj;
    }

    @Override
    public void setObj(String cacheId, T cacheObj) {
        if (null == cacheObj) {
            return; // ConcurrentHashMap 不能存放null
        }
        cache.put(cacheId, cacheObj);
    }

    @Override
    public void setObj(String cacheId, T cacheObj, Supplier<T> getObjHandler) {
        this.setObj(cacheId, cacheObj);
        getObjHandlerMap.put(cacheId, getObjHandler);

    }

    @Override
    public void clearCache(String cacheId) {
        cache.remove(cacheId);
    }

    @Override
    public void invalidateCache() {
//        cache.entrySet().stream().map(entry -> entry.getKey()).forEach(cache::remove);
        cache.clear();
    }

    @Override
    public List<T> listCacheObj() {
        List<T> list = new ArrayList<>();
        cache.values().forEach(list::add);
        return list;
    }

    public static void main(String[] args) {
        MBCache<String> cache = new AbstractMBCache<String>() {};
        cache.setObj("k1","v1");
        cache.setObj("k2","v2");

        final String k3Value = "12";
        cache.setObj("k3",null, () -> k3Value);

        System.out.println();

        System.out.println(cache.getObj("k3"));

        cache.invalidateCache();

        System.out.println();
    }
}
