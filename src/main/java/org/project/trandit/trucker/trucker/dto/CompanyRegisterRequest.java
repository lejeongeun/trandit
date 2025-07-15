package org.project.trandit.trucker.trucker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyRegisterRequest {
    private String name;
    private String registrationNumber;
    private String address;
    private String phone;
}
