package org.project.trandit.member.request.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.project.trandit.domain.request.VehicleType;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class RequestCreateDto {
    private String departureAddress;
    private String arrivalAddress;
    private LocalDateTime departureTime;
    private VehicleType vehicleType;
    private boolean needForkLift;
    private int workerCount;
}
