package org.project.trandit.domain.pay;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.trandit.domain.common.BaseTimeEntity;
import org.project.trandit.domain.match.Matching;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matching_id")
    private Matching matching;      //어떤 매칭에 대한 결제인지

    private int totalAmount;        // 총 결제 금액
    private int commissionAmount;   // 플랫폼 수수료
    private int truckerAmount;      // 트럭기사 정산 금액

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;   // PAID, REFUNDED, FAILED
}
