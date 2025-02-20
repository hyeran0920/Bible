package com.library.bible.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

class RedisConfigTest {

    private RedisConfig redisConfig;

    @BeforeEach
    void setUp() {
        redisConfig = new RedisConfig();
    }

    @Test
    @DisplayName("Bean redisConnectionFactory()가 RedisConnectionFactory 객체를 생성한다.")
    void redisConnectionFactoryTest() {
        RedisConnectionFactory factory = redisConfig.redisConnectionFactory();
        assertNotNull(factory);
    }

    @Test
    @DisplayName("Bean redisTemplate()가 RedisTemplate 객체를 생성한다.")
    void redisTemplateTest() {
        // 내부적으로 redisConnectionFactory() 호출
        RedisTemplate<String, Object> template = redisConfig.redisTemplate();
        assertNotNull(template);
        assertNotNull(template.getConnectionFactory());
    }

    @Test
    @DisplayName("Bean cacheManager()가 RedisCacheManager 객체를 생성한다.")
    void cacheManagerTest() {
        // 실제 연결 없이 Mock을 넘겨서 동작 확인
        RedisConnectionFactory mockFactory = mock(RedisConnectionFactory.class);
        RedisCacheManager cacheManager = redisConfig.cacheManager(mockFactory);
        assertNotNull(cacheManager);
    }
}
