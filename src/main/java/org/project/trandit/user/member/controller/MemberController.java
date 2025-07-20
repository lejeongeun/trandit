package org.project.trandit.user.member.controller;

import lombok.RequiredArgsConstructor;
import org.project.trandit.domain.member.Member;
import org.project.trandit.global.util.AuthUtils;
import org.project.trandit.user.member.dto.MemberProfileResponseDto;
import org.project.trandit.user.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final AuthUtils authUtils;

    @GetMapping("/me")
    public ResponseEntity<MemberProfileResponseDto> customerDetails(){
        Member member = authUtils.getCurrentMember();
        MemberProfileResponseDto getCustomerDetails = memberService.customerDetails(member);
        return ResponseEntity.ok(getCustomerDetails);
    }

}
