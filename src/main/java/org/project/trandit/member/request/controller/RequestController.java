package org.project.trandit.member.request.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.project.trandit.member.request.dto.RequestCreateDto;
import org.project.trandit.member.request.dto.RequestResponseDto;
import org.project.trandit.member.request.service.RequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/request")
public class RequestController {
    private final RequestService requestService;


    //운송요청 등록
    @PostMapping
    public ResponseEntity<Map<String, String>> request(@Valid @RequestBody RequestCreateDto requestCreate) {
        requestService.createRequest(requestCreate);
        return ResponseEntity.ok(Map.of("message", "create success"));
    }

    //나의 요청리스트 조회
    @GetMapping
    public ResponseEntity<List<RequestResponseDto>> getMyRequests() {
        List<RequestResponseDto> res = requestService.getMyRequests();
        return ResponseEntity.ok(res);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RequestResponseDto> getMyRequest(@PathVariable Long id) {
        RequestResponseDto res = requestService.getMyRequest(id);
        return ResponseEntity.ok(res);
    }

    //요청 수정
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> update(@Valid @RequestBody RequestCreateDto requestCreateDto,
                                                      @PathVariable Long id) {
        requestService.update(requestCreateDto, id);
        return ResponseEntity.ok(Map.of("message", "update success"));
    }

    //요청 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        requestService.delete(id);
        return ResponseEntity.ok(Map.of("message", "delete success"));
    }

}
