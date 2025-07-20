package org.project.trandit.trucker.offer.controller;

import lombok.RequiredArgsConstructor;
import org.project.trandit.trucker.offer.dto.OfferEditRequestDto;
import org.project.trandit.trucker.offer.dto.OfferRequestDto;
import org.project.trandit.trucker.offer.dto.OfferResponseDto;
import org.project.trandit.trucker.offer.service.OfferService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trucker/offer")
@RequiredArgsConstructor
public class OfferController {
    private final OfferService offerService;

    @PostMapping
    public ResponseEntity<Map<String, String>> offerRegister(@RequestBody OfferRequestDto requestDto){
        offerService.offerRegister(requestDto);
        return ResponseEntity.ok(Map.of("message", "가격 제안이 요청되었습니다."));

    }
    // 전체 목록 조회
    @GetMapping
    public ResponseEntity<List<OfferResponseDto>> getAllMyOffer(){
        List<OfferResponseDto> myOfferList = offerService.getAllMyOffer();
        return ResponseEntity.ok(myOfferList);
    }
    @GetMapping("/{id}")
    public ResponseEntity<OfferResponseDto> getDetailsMyOffer(@PathVariable Long id){
        OfferResponseDto myOffer = offerService.getDetailsMyOffer(id);
        return ResponseEntity.ok(myOffer);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, String>> editMyOffer(@PathVariable Long id,
                                                           @RequestBody OfferEditRequestDto editRequestDto){
        offerService.editMyOffer(id, editRequestDto);
        return ResponseEntity.ok(Map.of("message", "등록한 요청이 수정되었습니다."));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteOffer(@PathVariable Long id){
        offerService.deleteOffer(id);
        return ResponseEntity.ok(Map.of("message", "요청한 제안이 성공적으로 삭제되었습니다."));
    }


}
