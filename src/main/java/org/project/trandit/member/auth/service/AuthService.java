package org.project.trandit.member.auth.service;

import lombok.RequiredArgsConstructor;
import org.project.trandit.member.auth.dto.RegisterRequestDto;
import org.project.trandit.domain.member.Member;
import org.project.trandit.domain.member.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

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
}
