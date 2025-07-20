package org.project.trandit.trucker.offer.service;

import lombok.RequiredArgsConstructor;
import org.project.trandit.domain.member.Member;
import org.project.trandit.domain.offer.OfferStatus;
import org.project.trandit.domain.offer.PriceOffer;
import org.project.trandit.domain.offer.PriceOfferRepository;
import org.project.trandit.domain.request.Request;
import org.project.trandit.domain.request.RequestRepository;
import org.project.trandit.global.exception.NotFoundException;
import org.project.trandit.global.util.AuthUtils;
import org.project.trandit.trucker.offer.dto.OfferEditRequestDto;
import org.project.trandit.trucker.offer.dto.OfferRequestDto;
import org.project.trandit.trucker.offer.dto.OfferResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OfferService {

    private final PriceOfferRepository priceOfferRepository;
    private final AuthUtils authUtils;
    private final RequestRepository requestRepository;

    @Transactional
    public void offerRegister(OfferRequestDto offerRequestDto) {
        Member trucker = authUtils.getCurrentMember();
        Request request = requestRepository.findById(offerRequestDto.getRequestId())
                .orElseThrow(()-> new NotFoundException("해당 운송 요청을 찾을 수 없습니다."));

        PriceOffer priceOffer = PriceOffer.builder()
                .request(request)
                .price(offerRequestDto.getPrice())
                .message(offerRequestDto.getMessage())
                .status(OfferStatus.PENDING)
                .trucker(trucker)
                .build();

        priceOfferRepository.save(priceOffer);
    }

    @Transactional(readOnly = true)
    public List<OfferResponseDto> getAllMyOffer() {
        Member trucker = authUtils.getCurrentMember();

        return priceOfferRepository.findAll().stream()
                .map(OfferResponseDto::fromEntity)
                .collect(Collectors.toList());

    }

    public OfferResponseDto getDetailsMyOffer(Long id) {
        Member trucker = authUtils.getCurrentMember();
        PriceOffer priceOffer = priceOfferRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("해당 제안이 존재하지 않습니다."));
        if (!priceOffer.getTrucker().getId().equals(trucker.getId())){
            throw new IllegalArgumentException("자신의 제안만 조회 가능합니다.");
        }
        return OfferResponseDto.fromEntity(priceOffer);
    }

    public void editMyOffer(Long id, OfferEditRequestDto editRequestDto) {
        Member trucker = authUtils.getCurrentMember();
        PriceOffer priceOffer = priceOfferRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("존재하지 않는 제안입니다."));
        if (!priceOffer.getTrucker().getId().equals(trucker.getId())){
            throw new IllegalArgumentException("자신의 제안만 조회 가능합니다.");
        }
        priceOffer.setPrice(editRequestDto.getPrice());
        priceOffer.setMessage(editRequestDto.getMessage());

        priceOfferRepository.save(priceOffer);
    }

    public void deleteOffer(Long id) {
        Member trucker = authUtils.getCurrentMember();
        PriceOffer priceOffer = priceOfferRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("존재하지 않는 제안입니다."));
        if (!priceOffer.getTrucker().getId().equals(trucker.getId())){
            throw new IllegalArgumentException("자신의 제안만 조회 가능합니다.");
        }
        priceOfferRepository.delete(priceOffer);
    }
}
