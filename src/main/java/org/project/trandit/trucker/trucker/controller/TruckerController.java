package org.project.trandit.trucker.trucker.controller;

import lombok.RequiredArgsConstructor;
import org.project.trandit.domain.member.Member;
import org.project.trandit.global.util.AuthUtils;
import org.project.trandit.trucker.trucker.dto.CompanyRegisterRequest;
import org.project.trandit.trucker.trucker.service.TruckerService;
import org.project.trandit.trucker.trucker.dto.TruckerProfileResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/trucker")
@RequiredArgsConstructor
public class TruckerController {
    private final TruckerService truckerService;
    private final AuthUtils authUtils;
    @GetMapping("/me")
    public ResponseEntity<TruckerProfileResponse> truckerDetails(){
        Member member = authUtils.getCurrentMember();
        TruckerProfileResponse getTruckerDetails = truckerService.truckerDetails(member);
        return ResponseEntity.ok(getTruckerDetails);
    }
    // 회사 등록하는 페이지
    @PostMapping("/company-register")
    public ResponseEntity<Map<String, String>> registerCompany(@RequestBody CompanyRegisterRequest request){
        Member member = authUtils.getCurrentMember();
        truckerService.registerCompany(member, request);
        return ResponseEntity.ok(Map.of("message", "회사 등록 완료"));
    }

}
