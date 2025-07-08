package org.project.trandit.domain.member;

import jakarta.persistence.*;
import org.project.trandit.domain.common.BaseTimeEntity;
import org.project.trandit.domain.request.TransportRequest;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Member extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password;
    private String name;

    @Enumerated(EnumType.STRING)
    private RoleType roleType = RoleType.MEMBER;

    @OneToMany(mappedBy = "user")
    private List<TransportRequest> requests = new ArrayList<>();
}
