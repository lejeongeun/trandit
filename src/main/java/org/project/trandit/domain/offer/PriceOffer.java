package org.project.trandit.domain.offer;

import jakarta.persistence.*;
import lombok.*;
import org.project.trandit.domain.common.BaseTimeEntity;
import org.project.trandit.domain.member.Member;
import org.project.trandit.domain.request.Request;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceOffer extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int price; // 제안 가격
    private String message; // 추가 메세지

    @Enumerated(EnumType.STRING)
    private OfferStatus status; // 제안 상태(대기, 승인, 거절)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private Request request; // 대상 운송 요청

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trucker_id")
    private Member trucker; // 제안한 화물주
}
