package org.project.trandit.domain.request;

public enum RequestStatus {
    PENDING, //화주가 운송 요청을 새로 생성한 상태
    MATCHED, // 화물주가 제인을 수락하고 매칭된 상태
    COMPLETED, //운송이 완료된 상태
    CANCELLED // 화주나 화물주의 취소
}
