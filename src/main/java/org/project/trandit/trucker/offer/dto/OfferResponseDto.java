package org.project.trandit.trucker.offer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.trandit.domain.offer.OfferStatus;
import org.project.trandit.domain.offer.PriceOffer;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfferResponseDto {
    private Long id; // offer의 번호
    private Long requestId; // request 요청의 번호
    private int price;
    private String message;
    private OfferStatus offerStatus;

    public static OfferResponseDto fromEntity(PriceOffer offer){
        return OfferResponseDto.builder()
                .id(offer.getId())
                .price(offer.getPrice())
                .message(offer.getMessage())
                .offerStatus(offer.getStatus())
                .build();
    }

}
