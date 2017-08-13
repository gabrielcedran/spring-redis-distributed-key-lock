package br.com.cedran.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.SocketUtils;
import redis.embedded.RedisExecProvider;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Configuration
@EnableCaching
public class EmbeddedRedisConfig {

    private static final int REDIS_PORT = SocketUtils.findAvailableTcpPort();

    private RedisServer redisServer;

    @PostConstruct
    public void setUp() {
        redisServer = RedisServer.builder()
                .redisExecProvider(RedisExecProvider.defaultProvider())
                .port(REDIS_PORT)
                .setting("bind 127.0.0.1")
                .build();
        redisServer.start();
    }

    @PreDestroy
    public void tearDown() {
        redisServer.stop();
    }

    @Primary
    @Bean
    public JedisConnectionFactory cacheJedisConnectionFactory() {
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
        jedisConnectionFactory.setHostName("127.0.0.1");
        jedisConnectionFactory.setPort(REDIS_PORT);
        return jedisConnectionFactory;
    }

    @Bean
    public RedisTemplate<String, String> cacheRedisTemplate() {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(cacheJedisConnectionFactory());

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public CacheManager cacheManager() {
        RedisCacheManager redisCacheManager = new RedisCacheManager(cacheRedisTemplate());
        redisCacheManager.setUsePrefix(true);
        redisCacheManager.setCacheNames(null);
        redisCacheManager.setTransactionAware(true);
        redisCacheManager.setLoadRemoteCachesOnStartup(true);
        redisCacheManager.afterPropertiesSet();
        return redisCacheManager;
    }
}
