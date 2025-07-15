package org.project.trandit.trucker.request_view.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.trandit.domain.request.Request;
import org.project.trandit.domain.request.VehicleType;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestViewResponseDto {
    private long id;
    private String departureAddress;
    private String arrivalAddress;

    private Double departureLat;
    private Double departureLng;
    private Double arrivalLat;
    private Double arrivalLng;

    private LocalDateTime departureTime;
    private VehicleType vehicleType;
    private boolean needForkLift;
    private int workerCount;
    private String status;

    public static RequestViewResponseDto fromEntity(Request request) {
        return RequestViewResponseDto.builder()
                .id(request.getId())
                .departureAddress(request.getDepartureAddress())
                .arrivalAddress(request.getArrivalAddress())
                .departureLat(request.getDepartureLat())
                .departureLng(request.getDepartureLng())
                .arrivalLat(request.getArrivalLat())
                .arrivalLng(request.getArrivalLng())
                .departureTime(request.getDepartureTime())
                .vehicleType(request.getVehicleType())
                .needForkLift(request.isNeedForklift())
                .workerCount(request.getWorkerCount())
                .status(request.getStatus().name())
                .build();
    }
}
