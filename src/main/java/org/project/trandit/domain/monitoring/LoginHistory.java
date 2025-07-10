package org.project.trandit.domain.monitoring;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.trandit.domain.common.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor
public class LoginHistory extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String ipAddress; // 로그인 시도 이메일
    private boolean success; // 성공 여부
    private String userAgent; // 브라우저/디바이스 정보
}
