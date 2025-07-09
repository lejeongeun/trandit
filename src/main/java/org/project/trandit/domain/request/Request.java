package org.project.trandit.domain.request;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.trandit.domain.common.BaseTimeEntity;
import org.project.trandit.domain.match.Matching;
import org.project.trandit.domain.member.Member;
import org.project.trandit.domain.offer.PriceOffer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Request extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String departureAddress; //출발지
    private String arrivalAddress; // 도착지
    private LocalDateTime departureTime; // 요청 출발 일시

    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType; // 요청 차량 종류(1톤, 3톤, 5톤 트럭 등)

    private boolean needForklift; // 지계차 필요 여부
    private int workerCount; // 요청 인부 수

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member requester; // 요청자 (화주)

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PriceOffer> offers = new ArrayList<>(); // 제안 리스트
}
