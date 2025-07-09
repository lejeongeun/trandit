package org.project.trandit.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.project.trandit.auth.dto.LoginRequestDto;
import org.project.trandit.auth.dto.RefreshTokenRequestDto;
import org.project.trandit.auth.dto.TokenResponseDto;
import org.project.trandit.auth.dto.RegisterRequestDto;
import org.project.trandit.auth.service.AuthService;
import org.project.trandit.auth.service.RefreshTokenService;
import org.project.trandit.global.exception.JwtValidationException;
import org.project.trandit.security.JwtTokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequestDto request){
        authService.register(request);
        return ResponseEntity.ok(Map.of("message", "회원 가입이 완료되었습니다."));

    }


    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@RequestBody LoginRequestDto request){
        TokenResponseDto responseDto = authService.login(request);
        return ResponseEntity.ok(responseDto);
    }
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refreshToken(@RequestBody RefreshTokenRequestDto request){
        String refreshToken = request.getRefreshToken();

        if (!jwtTokenProvider.validateToken(refreshToken)){
            throw new JwtValidationException("유효하지 않은 Refresh Token 입니다.");
        }
        // 이메일 추출
        String email = jwtTokenProvider.getEmailFromToken(refreshToken);

        // Redis에서 저장된 RefreshToken 조회
        String storedRefreshToken = refreshTokenService.get(email);

        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)){
            throw new JwtValidationException("저장된 Refresh Token과 일치하지 않습니다.");
        }

        // AccessToken 새 발급
//        String newAccessToken = jwtTokenProvider.createAccessToken(email);
        return null;

    }


}
