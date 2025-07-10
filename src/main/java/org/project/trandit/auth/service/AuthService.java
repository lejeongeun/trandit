package org.project.trandit.auth.service;

import lombok.RequiredArgsConstructor;
import org.project.trandit.auth.dto.LoginRequestDto;
import org.project.trandit.auth.dto.TokenResponseDto;
import org.project.trandit.auth.dto.RegisterRequestDto;
import org.project.trandit.domain.member.Member;
import org.project.trandit.domain.member.MemberRepository;
import org.project.trandit.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;


    @Transactional
    public void register(RegisterRequestDto request) {
        if (memberRepository.existsByEmail(request.getEmail())){
            throw new IllegalArgumentException("이미 사용중인 이메일 입니다.");
        }
        if (memberRepository.existsByPassword(request.getPassword())){
            throw new IllegalArgumentException("이미 사용중인 비밀번호입니다.");
        }
        String encoderPassword = passwordEncoder.encode(request.getPassword());

        Member member = Member.builder()
                .email(request.getEmail())
                .password(encoderPassword)
                .name(request.getName())
                .phone(request.getPhone())
                .role(request.getRole())
                .build();
        memberRepository.save(member);
    }

    @Transactional
    public TokenResponseDto login(LoginRequestDto request) {
        Member member = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if(!passwordEncoder.matches(request.getPassword(), member.getPassword())){
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtTokenProvider.createAccessToken(member.getEmail(), member.getRole().name());
        String refreshToken = jwtTokenProvider.createRefreshToken();

        refreshTokenService.save(member.getEmail(), refreshToken, jwtTokenProvider.getRefreshTokenValidity());
        return new TokenResponseDto(accessToken, refreshToken, member.getRole().name());
    }
}
