package org.project.trandit.member.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.project.trandit.member.auth.dto.LoginRequestDto;
import org.project.trandit.member.auth.dto.RefreshTokenRequestDto;
import org.project.trandit.member.auth.dto.RegisterRequestDto;
import org.project.trandit.member.auth.service.AuthService;
import org.project.trandit.member.auth.service.RedisService;
import org.project.trandit.domain.member.Member;
import org.project.trandit.global.exception.JwtValidationException;
import org.project.trandit.security.CustomUserDetails;
import org.project.trandit.security.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;
    private final RedisService redisService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequestDto request){
        authService.register(request);
        return ResponseEntity.ok(Map.of("message", "회원 가입이 완료되었습니다."));

    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequestDto request,
                                                  HttpServletResponse response){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        Member member = ((CustomUserDetails) authentication.getPrincipal()).getMember();

        String accessToken = jwtTokenProvider.createAccessToken(member.getEmail(), member.getRole().name());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getEmail());

        redisService.save(member.getEmail(), refreshToken, jwtTokenProvider.getRefreshTokenValidity());

        if (!"CUSTOMER".equalsIgnoreCase(member.getRole().name())) {
            ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", accessToken)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(30 * 60)
                    .sameSite("Lax")
                    .build();
            response.addHeader("Set-Cookie", accessTokenCookie.toString());
        }
        return ResponseEntity.ok(Map.of("accessToken", accessToken));

    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshToken(@RequestBody RefreshTokenRequestDto request,
                                                         HttpServletResponse response){
        String refreshToken = request.getRefreshToken();

        // refresh token 유효성 검증
        if (!jwtTokenProvider.validateToken(refreshToken)){
            throw new JwtValidationException("유효하지 않은 Refresh Token 입니다.");
        }

        // RefreshToken에서 이메일 추출
        String email = jwtTokenProvider.getEmailFromToken(refreshToken);
        String role = jwtTokenProvider.getRoleFromToken(refreshToken);

        // Redis에서 저장된 RefreshToken 조회
        String storedRefreshToken = redisService.get(redisService.buildKey(email));
        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)){
            throw new JwtValidationException("저장된 Refresh Token과 일치하지 않습니다.");
        }

        // AccessToken 새 발급
        String newAccessToken = jwtTokenProvider.createAccessTokenFromRefreshToken(refreshToken);

        // 토큰 재발급 후
        if (!"CUSTOMER".equalsIgnoreCase(role)){
            ResponseCookie newAccessTokenCookie = ResponseCookie.from("accessToken", newAccessToken)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(30 * 60)
                    .sameSite("Lax")
                    .build();
            response.addHeader("Set-Cookie", newAccessTokenCookie.toString());
        }

        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                      HttpServletResponse response){
        if (userDetails == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "인증 정보가 없습니다."));
        }

        String email = userDetails.getMember().getEmail();
        redisService.delete(redisService.buildKey(email));

        ResponseCookie deleteCookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        response.addHeader("Set-Cookie",deleteCookie.toString());

        return ResponseEntity.ok(Map.of("message", "로그아웃 되었습니다."));
    }
    
}
