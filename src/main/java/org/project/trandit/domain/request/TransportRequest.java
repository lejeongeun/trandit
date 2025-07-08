package org.project.trandit.domain.request;

import jakarta.persistence.*;
import org.project.trandit.domain.common.BaseTimeEntity;
import org.project.trandit.domain.match.Matching;
import org.project.trandit.domain.member.Member;
import org.project.trandit.domain.offer.PriceOffer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class TransportRequest extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime requestDate; // 요청 등록 날짜 및 시간
    private String origin; // 출발지 주소
    private String destination; // 도착지 주소
    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType; // 요청 차량 종류

    private boolean needForklift;
    private int workerCount;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Member member;

    @OneToMany(mappedBy = "request")
    private List<PriceOffer> offers = new ArrayList<>();

    @OneToMany(mappedBy = "request")
    private Matching matching;
}
