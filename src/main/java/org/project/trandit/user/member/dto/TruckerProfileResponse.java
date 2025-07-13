package org.project.trandit.user.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TruckerProfileResponse {
    private String company_name;
    private String company_registrationNumber; // 사업자등록번호
    private String company_address; // 주소
    private String company_phone; // 회사 번호
    private String name;
    private String email;
    private String phone;
    private String role;

}
