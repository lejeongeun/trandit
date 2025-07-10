package org.project.trandit.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.project.trandit.global.exception.JwtValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final CustomUserDetailsService userDetailsService;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenValidity;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenValidity;

    private SecretKey secretKey; // Jwt 서명을 위한 Key 객체

    @PostConstruct
    protected void init(){
        byte[] keyBytes = Base64.getEncoder().encode(secret.getBytes());
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createAccessToken(String email, String role){
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role);

        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenValidity);

        return Jwts.builder()
                .setClaims(claims) //payload
                .setIssuedAt(now) // 발급 시각
                .setExpiration(expiry) // 만료 시각
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // 리프레시 토큰 생성
    public String createRefreshToken(){
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenValidity);

        return Jwts.builder()
                .setIssuedAt(now) // 발급 시간
                .setExpiration(expiry) // 만료 시간
                .signWith(secretKey, SignatureAlgorithm.HS256) // 알고리즘으로 서명
                .compact(); // 최종 JWT를 문자열로 반환
    }
    public String createAccessTokenFromRefreshToken(String refreshToken){
        String email = getEmailFromToken(refreshToken);
        String role = getRoleFromToken(refreshToken);
        return createAccessToken(email, role);
    }

    // 토큰에서 사용자 이메일 추출
    public String getEmailFromToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject(); // 이메일 필드 반환
    }
    // 사용자 역할 추출
    public String getRoleFromToken(String token){
        return (String) Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role");
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
                    return true;
        } catch (SecurityException | MalformedJwtException e) {
            throw new JwtValidationException("잘못된 JWT 서명입니다.", e);
        } catch (ExpiredJwtException e) {
            throw new JwtValidationException("만료된 JWT 토큰입니다.", e);
        } catch (UnsupportedJwtException e) {
            throw new JwtValidationException("지원하지 않는 JWT입니다.", e);
        } catch (IllegalArgumentException e) {
            throw new JwtValidationException("JWT 클레임이 비어 있습니다.", e);
        }
    }
    public long getRefreshTokenValidity(){
        return refreshTokenValidity;
    }

    // 인증 객체 반환 -> SecurityContext로 등록되는 생체 인증 정보
    public Authentication getAuthentication(String token){
        String email = getEmailFromToken(token);

        // DB에서 사용자 정보 조회 (UserDetails 구현체 반환)
        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(email);

        // UsernamePasswordAuthenticationToken(Principal, Credentials, 권한목록)
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

    }
    // 앱(헤더)과 웹(cookie) 구분
    public String resolveToken(HttpServletRequest request) {
        // 앱
        String bearer = request.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")){
            return bearer.substring(7);
        }

        if (request.getCookies() != null){
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())){
                    return cookie.getValue();
                }

            }
        }
        return null;
    }
}
