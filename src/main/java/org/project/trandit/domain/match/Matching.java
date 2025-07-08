package org.project.trandit.domain.match;

import jakarta.persistence.*;
import org.project.trandit.domain.carrier.Carrier;
import org.project.trandit.domain.common.BaseTimeEntity;
import org.project.trandit.domain.request.TransportRequest;

import java.time.LocalDateTime;

@Entity
public class Matching extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "request_id", nullable = false)
    private TransportRequest request;

    @ManyToOne
    @JoinColumn(name = "carrier_id", nullable = false)
    private Carrier carrier;

    private LocalDateTime matchedAt;
    private boolean paymentDone;
}
