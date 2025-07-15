package org.project.trandit.trucker.trucker.service;

import lombok.RequiredArgsConstructor;
import org.project.trandit.domain.member.*;
import org.project.trandit.global.exception.NotFoundException;
import org.project.trandit.global.util.AuthUtils;
import org.project.trandit.trucker.trucker.dto.CompanyRegisterRequest;
import org.project.trandit.trucker.trucker.dto.TruckerProfileResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TruckerService {
    private final MemberRepository memberRepository;
    private final CompanyRepository companyRepository;
    @Transactional(readOnly = true)
    public TruckerProfileResponse truckerDetails(Member member) {

        if (member.getRole() != Role.TRUCKER){
            throw new AccessDeniedException("화물주 전용 정보 입니다.");
        }
        Member foundMember = memberRepository.findById(member.getId())
                .orElseThrow(()-> new NotFoundException("사용자 정보가 존재하지 않습니다."));

        Company company = foundMember.getCompany();

        return TruckerProfileResponse.builder()
                .id(member.getId())
                .company_name(company.getName())
                .company_registrationNumber(company.getRegistrationNumber())
                .company_address(company.getAddress())
                .company_phone(company.getPhone())
                .name(member.getName())
                .email(member.getEmail())
                .phone(member.getPhone())
                .role(member.getRole().name())
                .build();
    }

    @Transactional
    public void registerCompany(Member member, CompanyRegisterRequest request) {
        if (member.getRole() != Role.TRUCKER){
            throw new IllegalArgumentException("화물주 전용입니다. 다시 로그인 하여 주세요");
        }
        if (member.getCompany() != null){
            throw new IllegalArgumentException("회사 정보가 이미 등록되어있습니다.");
        }

        Company company = Company.builder()
                .name(request.getName())
                .address(request.getAddress())
                .registrationNumber(request.getRegistrationNumber())
                .phone(request.getPhone())
                .build();

        Company savedCompany = companyRepository.save(company);
        member.setCompany(savedCompany);
        memberRepository.save(member);
    }
}
