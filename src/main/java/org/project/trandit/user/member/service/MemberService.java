package org.project.trandit.user.member.service;

import lombok.RequiredArgsConstructor;
import org.project.trandit.domain.member.Member;
import org.project.trandit.domain.member.MemberRepository;
import org.project.trandit.domain.member.Role;
import org.project.trandit.global.exception.NotFoundException;
import org.project.trandit.user.member.dto.MemberProfileResponseDto;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberProfileResponseDto customerDetails(Member member){
        if (member.getRole() != Role.CUSTOMER){
            throw new AccessDeniedException("고객 전용 정보입니다.");
        }

        if (!memberRepository.existsById(member.getId())){
            throw new NotFoundException("사용자 정보가 존재하지 않습니다.");
        }
        return MemberProfileResponseDto.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .phone(member.getPhone())
                .role(member.getRole().name())
                .build();

    }


}
