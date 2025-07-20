package org.project.trandit.trucker.offer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfferRequestDto {
    private Long requestId; // 제안할 Request Id
    private int price;
    private String message;


}
