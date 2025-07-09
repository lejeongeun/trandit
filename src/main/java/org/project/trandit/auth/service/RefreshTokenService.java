package org.project.trandit.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final StringRedisTemplate redisTemplate;

    private static final String PREFIX = "RT:";

    public void save(String email, String token, long expiration){
        redisTemplate.opsForValue().set(PREFIX + email, token, Duration.ofMillis(expiration));
    }
    public String get(String email){
        return redisTemplate.opsForValue().get(PREFIX + email);
    }
    public void delete(String email){
        redisTemplate.delete(PREFIX + email);
    }

}
