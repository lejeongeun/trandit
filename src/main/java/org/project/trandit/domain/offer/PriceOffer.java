package org.project.trandit.domain.offer;

import jakarta.persistence.*;
import org.project.trandit.domain.carrier.Carrier;
import org.project.trandit.domain.common.BaseTimeEntity;
import org.project.trandit.domain.request.TransportRequest;

@Entity
public class PriceOffer extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int proposedPrice;
    @Enumerated(EnumType.STRING)
    private OfferStatus status;

    @ManyToOne
    @JoinColumn(name = "request_id")
    private TransportRequest request;

    @ManyToOne
    @JoinColumn(name = "carrier_id")
    private Carrier carrier;
}
