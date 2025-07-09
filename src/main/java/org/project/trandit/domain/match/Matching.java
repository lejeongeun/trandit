package org.project.trandit.domain.match;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.trandit.domain.common.BaseTimeEntity;
import org.project.trandit.domain.offer.PriceOffer;
import org.project.trandit.domain.request.Request;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Matching extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposal_id")
    private PriceOffer proposal; // 어떤 제안이 선택됐는가

    @Enumerated(EnumType.STRING)
    private MatchingStatus status; // WAITING, IN_PROGRESS, COMPLETED

}
