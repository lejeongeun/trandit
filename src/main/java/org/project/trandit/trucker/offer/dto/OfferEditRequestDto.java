package org.project.trandit.trucker.offer.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfferEditRequestDto {
    private int price;
    private String message;
}
