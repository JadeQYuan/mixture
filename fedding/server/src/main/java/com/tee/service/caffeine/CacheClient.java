package com.tee.service.caffeine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tee.config.CaffeineConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static com.tee.config.CaffeineConfig.buildCache;

@Slf4j
@Component
public class CacheClient {

    @Autowired
    private CaffeineConfig.NativeCacheMagger nativeCacheMagger;
    @Autowired
    private com.github.benmanes.caffeine.cache.Cache<String, Object> globalCache;

    /**
     * 缓存数据
     *
     * @param key 缓存键
     * @param value 数据
     */
    public void set(String key, Object value) {
        if (!StringUtils.hasLength(key) || Objects.isNull(value)) {
            return;
        }
        globalCache.put(key, value);
    }

    /**
     * 移除缓存
     *
     * @param key 缓存键
     */
    public void remove(String key) {
        if (!StringUtils.hasLength(key)) {
            return;
        }
        globalCache.invalidate(key);
    }

    /**
     * 获取缓存
     *
     * @param key 缓存键
     * @return
     */
    public Object get(String key) {
        if (!StringUtils.hasLength(key)) {
            return null;
        }
        return globalCache.getIfPresent(key);
    }

    /**
     * 获取缓存
     *
     * @param key 缓存键
     * @param clazz 数据类型
     * @param <T>
     * @return
     */
    public <T> T get(String key, Class<T> clazz) {
        if (Objects.nonNull(clazz)) {
            Object obj = get(key);
            try {
                if (Objects.nonNull(obj) && obj.getClass().isAssignableFrom(clazz)) {
                    ObjectMapper mapper = new ObjectMapper();
                    return mapper.convertValue(obj, clazz);
                }
            } catch (Exception e) {
                log.error("getByNative failed.", e);
            }
        }
        return null;
    }

    /**
     * 获取全部缓存
     *
     * @return
     */
    public Map<String, Object> getAll() {
        return globalCache.asMap();
    }

    /**
     * 缓存数据
     *
     * @param cacheName 缓存名称
     * @param key 缓存键
     * @param value 缓存值
     */
    public void set(String cacheName, String key, Object value) {
        if (Objects.isNull(value)) {
            return;
        }
        set(Optional.ofNullable(nativeCacheMagger.getCache(cacheName))
            .orElse(buildCache(cacheName, Duration.ofHours(1), 20, 100)), key, value);
    }

    /**
     * 缓存数据
     *
     * @param cache 缓存
     * @param key 缓存键
     * @param value 数据
     */
    public void set(Cache cache, String key, Object value) {
        if (Objects.isNull(value)) {
            return;
        }
        cache.put(key, value);
        nativeCacheMagger.putCache(cache).initializeCaches();
    }

    /**
     * 移除缓存
     *
     * @param cacheName 缓存名称
     * @param key 缓存键
     */
    public void remove(String cacheName, String key) {
        if (!StringUtils.hasLength(cacheName)) {
            return;
        }
        remove(nativeCacheMagger.getCache(cacheName), key);
    }

    /**
     * 移除缓存
     *
     * @param cache 缓存
     * @param key 缓存键
     */
    public void remove(Cache cache, String key) {
        if (Objects.isNull(cache) || !StringUtils.hasLength(key)) {
            return;
        }
        cache.evict(key);
    }

    /**
     * 移除全部
     *
     * @param cache
     * @return
     */
    public boolean removeAll(Cache cache) {
        if (Objects.isNull(cache)) {
            return true;
        }
        return cache.invalidate();
    }

    /**
     * 移除全部
     *
     * @param cacheName
     * @return
     */
    public boolean removeAll(String cacheName) {
        if (!StringUtils.hasLength(cacheName)) {
            return true;
        }
        return removeAll(nativeCacheMagger.getCache(cacheName));
    }

    /**
     * 加载所有缓存
     *
     * @return
     */
    public Collection<Cache> loadCaches() {
        return nativeCacheMagger.loadCaches();
    }

    /**
     * 获取缓存
     *
     * @param cacheName 缓存名称
     * @param key 缓存键
     * @return
     */
    public Object get(String cacheName, String key) {
        if (!StringUtils.hasLength(cacheName)) {
            return null;
        }
        return get(nativeCacheMagger.getCache(cacheName), key);
    }

    /**
     * 获取缓存
     *
     * @param cache 缓存
     * @param key 缓存键
     * @return
     */
    public Object get(Cache cache, String key) {
        if (Objects.isNull(cache) || !StringUtils.hasLength(key)) {
            return null;
        }
        return cache.get(key);
    }

    /**
     * 获取缓存
     *
     * @param cacheName 缓存名称
     * @param key 缓存键
     * @param clazz 数据类型
     * @param <T>
     * @return
     */
    public <T> T get(String cacheName, String key, Class<T> clazz) {
        if (!StringUtils.hasLength(cacheName)) {
            return null;
        }
        return get(nativeCacheMagger.getCache(cacheName), key, clazz);
    }

    /**
     * 获取缓存
     *
     * @param cache 缓存
     * @param key 缓存键
     * @param clazz 数据类型
     * @param <T>
     * @return
     */
    public <T> T get(Cache cache, String key, Class<T> clazz) {
        if (Objects.isNull(cache) || Objects.isNull(key) || Objects.isNull(clazz)) {
            return null;
        }
        return cache.get(key, clazz);
    }

    /**
     * 获取全部缓存
     *
     * @param cache 缓存
     * @return
     */
    public List<Object> getAll(Cache cache) {
        if (Objects.nonNull(cache) && cache instanceof CaffeineCache) {
            return ((CaffeineCache)cache).getNativeCache().asMap().values().stream().collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * 获取全部缓存
     *
     * @param cache 缓存
     * @param clazz 数据类型
     * @param <T>
     * @return
     */
    public <T> List<T> getAll(Cache cache, Class<T> clazz) {
        return getAll(cache).stream().map(obj -> {
            if (obj.getClass().isAssignableFrom(clazz)) {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.convertValue(obj, clazz);
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * 获取全部缓存
     *
     * @param cacheName 缓存名称
     * @return
     */
    public List<Object> getAll(String cacheName) {
        return getAll(nativeCacheMagger.getCache(cacheName));
    }

    /**
     * 获取全部缓存
     *
     * @param cacheName 缓存名称
     * @param clazz 数据类型
     * @return
     */
    public <T> List<T> getAll(String cacheName, Class<T> clazz) {
        return getAll(nativeCacheMagger.getCache(cacheName), clazz);
    }
}
