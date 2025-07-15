package org.project.trandit.trucker.request_view.controller;

import lombok.RequiredArgsConstructor;
import org.project.trandit.trucker.request_view.dto.RequestViewResponseDto;
import org.project.trandit.trucker.request_view.service.RequestViewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.swing.plaf.PanelUI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trucker")
public class RequestViewController {
    private final RequestViewService requestViewService;

    @GetMapping("/request-view")
    public ResponseEntity<List<RequestViewResponseDto>> getAllRequestList(){
        List<RequestViewResponseDto> allRequestList = requestViewService.getAllRequestList();
        return ResponseEntity.ok(allRequestList);
    }
    @GetMapping("/request-view/{id}")
    public ResponseEntity<RequestViewResponseDto> getRequestDetails(@PathVariable Long id){
        RequestViewResponseDto requestDetails = requestViewService.getRequestDetails(id);
        return ResponseEntity.ok(requestDetails);

    }


}
