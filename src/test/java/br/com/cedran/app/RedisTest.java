package br.com.cedran.app;

import br.com.cedran.app.Service.Redis;
import br.com.cedran.redis.distributed.lock.annotation.EnableDistributedLock;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.stream.IntStream;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles({"test"})
@EnableDistributedLock
@Slf4j
public class RedisTest {

    @Autowired
    private Redis redis;

    @Autowired
    private CacheManager cacheManager;

    @Before
    public void beforeEachTest() {
        cacheManager.getCacheNames().forEach(cacheName -> cacheManager.getCache(cacheName).clear());
    }

    @Test
    public void testRetryWithAnnotation() {
        IntStream.range(1, 10)
            .parallel()
            .forEach(count ->
                redis.addToListNotConfigurable(count, "counter")
            );
        Assert.assertEquals(9, redis.obtainList("counter").size());
        log.info("Counter List: {}", redis.obtainList("counter"));
    }

    @Test
    public void testRetryWithTemplate() {
        IntStream.range(1, 10)
            .parallel()
            .forEach(count ->
                    redis.addToListConfigurable(count, "counter")
            );
        Assert.assertEquals(9, redis.obtainList("counter").size());
        log.info("Counter List: {}", redis.obtainList("counter"));
    }
}
