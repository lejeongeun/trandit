package org.project.trandit.trucker.request_view.service;

import lombok.RequiredArgsConstructor;
import org.project.trandit.domain.request.Request;
import org.project.trandit.domain.request.RequestRepository;
import org.project.trandit.global.exception.NotFoundException;
import org.project.trandit.trucker.request_view.dto.RequestViewResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestViewService {

    private final RequestRepository requestRepository;
    @Transactional(readOnly = true)
    public List<RequestViewResponseDto> getAllRequestList() {
        return requestRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(RequestViewResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RequestViewResponseDto getRequestDetails(Long id) {
        Request request = requestRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("존재하지 않는 요청 건입니다."));

        return RequestViewResponseDto.fromEntity(request);
    }
}
