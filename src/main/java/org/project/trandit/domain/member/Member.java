package org.project.trandit.domain.member;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import org.project.trandit.domain.common.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email; // 로그인용 이메일

    @Column(nullable = false)
    private String password; // 해싱된 비밀번호

    private String name; // 이름
    private String phone; // 연락처

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // CUSTOMER(화주), TRUCKER(화물주), ADMIN

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company; // 트럭기사만 해당 (화주/관리자 = null)
}
