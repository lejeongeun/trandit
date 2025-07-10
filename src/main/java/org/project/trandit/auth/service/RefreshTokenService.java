package org.project.trandit.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RedisTemplate<String, String> redisTemplate;

    private static final String PREFIX = "RT:";

    // Redis 키 생성
    public String buildKey(String email){
        return PREFIX + email;
    }
    // 토큰 저장
    public void save(String email, String token, long expirationMillis){
        redisTemplate.opsForValue().set(buildKey(email), token, Duration.ofMillis(expirationMillis));
    }

    // token 조회
    public String get(String key){
        return redisTemplate.opsForValue().get(key);
    }

    public void delete(String key){
        redisTemplate.delete(key);
    }

}
