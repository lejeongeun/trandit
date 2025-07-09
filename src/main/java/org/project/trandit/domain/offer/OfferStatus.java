package org.project.trandit.domain.offer;

public enum OfferStatus {
    PENDING, // 대기
    APPROVED, // 승인
    REJECTED, // 거절
    REQUEST_CANCEL // 취소 요청(PENDING 상태일 때만 가능)
}
