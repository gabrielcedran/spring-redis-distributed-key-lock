package br.com.cedran;

import br.com.cedran.mechanism.CacheManagerCustom;
import br.com.cedran.service.Redis;
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
    public void testDefaultSettings() {
        IntStream.range(1, 10)
            .parallel()
            .forEach(count ->
                redis.addToList(count, "counter")
            );
        Assert.assertEquals(9, redis.obtainList("counter").size());
        System.out.println(redis.obtainList("counter"));
    }

    @Test
    public void testDefaultSettings2() {
        IntStream.range(1, 10)
            .parallel()
            .forEach(count ->
                    redis.addToList2(count, "counter")
            );
        Assert.assertEquals(9, redis.obtainList("counter").size());
        System.out.println(redis.obtainList("counter"));
    }
}
