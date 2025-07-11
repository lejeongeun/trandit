package org.project.trandit.global.util;

import org.project.trandit.domain.member.Member;
import org.project.trandit.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthUtils {

    // 현재 인증된 사용자 정보 반환
    public CustomUserDetails getCurrentUserDetails(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof CustomUserDetails){
            return (CustomUserDetails) authentication.getPrincipal();
        }
        return null;
    }
    // 현재 로그인한 Member 반환
    public Member getCurrentMember(){
        CustomUserDetails userDetails = getCurrentUserDetails();
        return userDetails != null ? userDetails.getMember() : null;
    }

    // 로그인한 사용자의 email 반환
    public String getCurrentUserEmail(){
        Member member = getCurrentMember();
        return member != null ? member.getEmail() : null;
    }

    // 로그인 여부 확인
    public boolean isLoggedIn(){
        return getCurrentUserDetails() != null;
    }

    //현재 사용자 Role 반환
    public String getCurrentUserRole(){
        Member member = getCurrentMember();
        return member != null ? member.getRole().name() : null;
    }

}
