package org.project.trandit.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.project.trandit.auth.dto.LoginRequestDto;
import org.project.trandit.auth.dto.RefreshTokenRequestDto;
import org.project.trandit.auth.dto.TokenResponseDto;
import org.project.trandit.auth.dto.RegisterRequestDto;
import org.project.trandit.auth.service.AuthService;
import org.project.trandit.auth.service.RefreshTokenService;
import org.project.trandit.global.exception.JwtValidationException;
import org.project.trandit.security.CustomUserDetails;
import org.project.trandit.security.JwtTokenProvider;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
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
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequestDto request){
        authService.register(request);
        return ResponseEntity.ok(Map.of("message", "회원 가입이 완료되었습니다."));

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto request,
                                                  HttpServletResponse response){
        TokenResponseDto responseDto = authService.login(request);
        // 플랫폼 분기 처리
        if ("CUSTOMER".equalsIgnoreCase(responseDto.getRole())){
            return ResponseEntity.ok(responseDto);
        }else {
            ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", responseDto.getAccessToken())
                    .httpOnly(true)
                    .secure(false) // 배포 시 true
                    .path("/")
                    .maxAge(30 * 60) // 30분
                    .sameSite("Lax") // CSRF 방지
                    .build();

            response.addHeader("Set-Cookie", accessTokenCookie.toString());
            // refreshToken과 role은 json형식으로 반환 (accessToken은 쿠키로)
            return ResponseEntity.ok(Map.of(
                    "refreshToken", responseDto.getRefreshToken(),
                    "role", responseDto.getRole()
            ));
        }

    }
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refreshToken(@RequestBody RefreshTokenRequestDto request,
                                                         HttpServletResponse response){
        String refreshToken = request.getRefreshToken();

        // refresh token 유효성 검증
        if (!jwtTokenProvider.validateToken(refreshToken)){
            throw new JwtValidationException("유효하지 않은 Refresh Token 입니다.");
        }

        // RefreshToken에서 이메일 추출
        String email = jwtTokenProvider.getEmailFromToken(refreshToken);

        // Redis에서 저장된 RefreshToken 조회
        String storedRefreshToken = refreshTokenService.get(refreshTokenService.buildKey(email));
        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)){
            throw new JwtValidationException("저장된 Refresh Token과 일치하지 않습니다.");
        }

        // AccessToken 새 발급
        String newAccessToken = jwtTokenProvider.createAccessTokenFromRefreshToken(refreshToken);
        String role = jwtTokenProvider.getRoleFromToken(refreshToken);

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

        return ResponseEntity.ok(new TokenResponseDto(
                newAccessToken,
                refreshToken,
                role
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                      HttpServletResponse response){
        String email = userDetails.getMember().getEmail();
        refreshTokenService.delete(refreshTokenService.buildKey(email));
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
