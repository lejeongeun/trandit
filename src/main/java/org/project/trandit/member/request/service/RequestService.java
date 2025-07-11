package org.project.trandit.member.request.service;

import lombok.RequiredArgsConstructor;
import org.project.trandit.domain.member.Member;
import org.project.trandit.domain.request.Request;
import org.project.trandit.domain.request.RequestRepository;
import org.project.trandit.domain.request.RequestStatus;
import org.project.trandit.global.util.AuthUtilss;
import org.project.trandit.member.request.dto.RequestCreateDto;
import org.project.trandit.member.request.dto.RequestResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepository requestRepository;
    private final AuthUtilss authUtil;

    public void createRequest(RequestCreateDto requestCreate) {
        Member member = authUtil.getCurrentMember();

        Request request = Request.builder()
                .departureAddress(requestCreate.getDepartureAddress())
                .arrivalAddress(requestCreate.getArrivalAddress())
                .departureTime(requestCreate.getDepartureTime())
                .vehicleType(requestCreate.getVehicleType())
                .needForklift(requestCreate.isNeedForkLift())
                .workerCount(requestCreate.getWorkerCount())
                .requester(member)
                .status(RequestStatus.PENDING)
                .build();

        requestRepository.save(request);
    }

    public List<RequestResponseDto> getMyRequests() {
        Member requester = authUtil.getCurrentMember();

        return requestRepository.findByRequester(requester).stream()
                .map(RequestResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public void update(RequestCreateDto requestCreate, Long id) {
        Member member = authUtil.getCurrentMember();

        Request request = requestRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않은 요청입니다."));

        if(!request.getRequester().equals(member)) {
            throw new IllegalArgumentException("본인이 작성한 글만 수정이 가능합니다.");
        }

        request.setDepartureAddress(requestCreate.getDepartureAddress());
        request.setArrivalAddress(requestCreate.getArrivalAddress());
        request.setDepartureTime(requestCreate.getDepartureTime());
        request.setVehicleType(requestCreate.getVehicleType());
        request.setWorkerCount(requestCreate.getWorkerCount());

    }

    public void delete(Long id) {
        Member member = authUtil.getCurrentMember();

        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않은 요청입니다."));

        if(!request.getRequester().equals(member)) {
            throw new IllegalArgumentException("본인이 작성한 글만 삭제가 가능합니다.");
        }

        requestRepository.deleteById(id);
    }
}
