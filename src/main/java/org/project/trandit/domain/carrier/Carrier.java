package org.project.trandit.domain.carrier;

import jakarta.persistence.*;
import org.project.trandit.domain.common.BaseTimeEntity;
import org.project.trandit.domain.member.RoleType;
import org.project.trandit.domain.offer.PriceOffer;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Carrier extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String companyName;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private RoleType roleType = RoleType.CARRIER;

    @OneToMany(mappedBy = "carrier", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PriceOffer> offers = new ArrayList<>();
}
