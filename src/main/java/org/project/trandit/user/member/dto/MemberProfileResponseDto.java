package org.project.trandit.user.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberProfileResponseDto {
    private String name;
    private String email;
    private String phone;
    private String role;
}
