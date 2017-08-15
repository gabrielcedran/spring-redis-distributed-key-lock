package br.com.cedran.redis.distributed.lock.mechanism;


import br.com.cedran.redis.distributed.lock.annotation.DistributedCacheLock;
import br.com.cedran.redis.distributed.lock.exception.LockException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
public class CacheManagerCustom<T, R> {

    @Autowired
    private CacheManager cacheManager;

    @Value("${br.com.cedran.lockCacheName:lock}")
    private String lockCacheName;

    @Retryable(
            value = { LockException.class },
            maxAttempts = 5,
            backoff = @Backoff(delay = 500))
    public void updateWithLock(Function<T, R> operationWhenExistent, Supplier<R> operationWhenNonExistent, String cacheName, Object key) {
        UUID pid = UUID.randomUUID();

        Cache lockCache = tryToLock(key, pid);

        Cache cache = cacheManager.getCache(cacheName);
        Cache.ValueWrapper valueWrapper = cache.get(key);

        R transformed = Optional.ofNullable(valueWrapper)
                .map(object -> (T) object.get())
                .map(operationWhenExistent)
                .orElseGet(operationWhenNonExistent);

        verifyLock(key, pid, lockCache);

        cache.put(key, transformed);

        lockCache.evict(key);
    }

    private void verifyLock(Object key, UUID pid, Cache lockCache) {
        Cache.ValueWrapper lockValue = lockCache.get(key);
        UUID locked = Optional.ofNullable(lockValue)
                .map(Cache.ValueWrapper::get)
                .map(o -> UUID.fromString(o.toString()))
                .orElse(null);
        if(!pid.equals(locked)) {
            throw new LockException();
        }
    }

    private Cache tryToLock(Object key, UUID pid) {
        Cache lockCache = cacheManager.getCache(lockCacheName);
        Cache.ValueWrapper lockValue = lockCache.get(key);
        UUID locked = Optional.ofNullable(lockValue)
                .map(Cache.ValueWrapper::get)
                .map(o -> UUID.fromString(o.toString()))
                .orElse(null);
        if(locked != null && !locked.equals(pid)) {
            throw new LockException();
        }
        lockCache.put(key, pid);
        return lockCache;
    }


    public void updateWithLock(Function<T, R> operationWhenExistent, Supplier<R> operationWhenNonExistent, String cacheName, Object key, Integer maxRetries, Integer delay) {
        UUID pid = UUID.randomUUID();

        RetryTemplate template = new RetryTemplate();
        SimpleRetryPolicy policy = new SimpleRetryPolicy(maxRetries, Collections.singletonMap(LockException.class, true));
        template.setRetryPolicy(policy);

        ExponentialBackOffPolicy backoff = new ExponentialBackOffPolicy();
        backoff.setInitialInterval(delay);
        template.setBackOffPolicy(backoff);

        template.execute((RetryCallback<Object, RuntimeException>) retryContext -> {
            updateWithLock(operationWhenExistent, operationWhenNonExistent, cacheName, key);
            return null;
        });
    }

}
