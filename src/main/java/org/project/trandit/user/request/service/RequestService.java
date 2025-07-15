package org.project.trandit.user.request.service;

import lombok.RequiredArgsConstructor;
import org.project.trandit.domain.member.Member;
import org.project.trandit.domain.request.Request;
import org.project.trandit.domain.request.RequestRepository;
import org.project.trandit.domain.request.RequestStatus;
import org.project.trandit.global.util.AuthUtils;
import org.project.trandit.user.request.dto.RequestCreateDto;
import org.project.trandit.user.request.dto.RequestResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepository requestRepository;
    private final AuthUtils authUtil;

    @Transactional
    public void createRequest(RequestCreateDto requestCreate) {
        Member member = authUtil.getCurrentMember();

        Request request = Request.builder()
                .departureAddress(requestCreate.getDepartureAddress())
                .arrivalAddress(requestCreate.getArrivalAddress())
                .departureLat(requestCreate.getDepartureLat())
                .departureLng(requestCreate.getDepartureLng())
                .arrivalLat(requestCreate.getArrivalLat())
                .arrivalLng(requestCreate.getArrivalLng())
                .departureTime(requestCreate.getDepartureTime())
                .vehicleType(requestCreate.getVehicleType())
                .needForklift(requestCreate.isNeedForkLift())
                .workerCount(requestCreate.getWorkerCount())
                .requester(member)
                .status(RequestStatus.PENDING)
                .build();

        requestRepository.save(request);
    }

    @Transactional(readOnly = true)
    public List<RequestResponseDto> getMyRequests() {
        Member requester = authUtil.getCurrentMember();

        return requestRepository.findByRequester(requester).stream()
                .map(RequestResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RequestResponseDto getMyRequest(Long id) {
        Member requester = authUtil.getCurrentMember();

        return requestRepository.findByRequesterAndId(requester, id)
                .map(RequestResponseDto::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않은 요청입니다."));
    }

    @Transactional
    public void update(RequestCreateDto requestCreate, Long id) {
        Member currentMember = authUtil.getCurrentMember();

        Request request = requestRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않은 요청입니다."));

        if(!request.getRequester().getId().equals(currentMember.getId())) {
            throw new IllegalArgumentException("본인이 작성한 요청만 수정이 가능합니다.");
        }

        request.setDepartureAddress(requestCreate.getDepartureAddress());
        request.setArrivalAddress(requestCreate.getArrivalAddress());
        request.setDepartureLat(requestCreate.getDepartureLat());
        request.setDepartureLng(requestCreate.getDepartureLng());
        request.setArrivalLat(requestCreate.getArrivalLat());
        request.setArrivalLng(requestCreate.getArrivalLng());
        request.setDepartureTime(requestCreate.getDepartureTime());
        request.setNeedForklift(requestCreate.isNeedForkLift());
        request.setVehicleType(requestCreate.getVehicleType());
        request.setWorkerCount(requestCreate.getWorkerCount());

    }

    @Transactional
    public void delete(Long id) {
        Member member = authUtil.getCurrentMember();

        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않은 요청입니다."));

        if(!request.getRequester().getId().equals(member.getId())) {
            throw new IllegalArgumentException("본인이 작성한 요청만 삭제가 가능합니다.");
        }

        requestRepository.deleteById(id);
    }


}
