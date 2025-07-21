import React, { useState } from 'react';
import CommonButton from './CommonButton';

const STATUS_OPTIONS = ["대기", "운송 중", "완료"];

function RequestDetails({ selectItem, handleStatusChange, setselectItem, onNotify }) {
    const [nextStatus, setNextStatus] = useState(selectItem.status);

    const handleChange = () => {
        if (nextStatus !== selectItem.status) {
            handleStatusChange(selectItem.id, nextStatus);
            if (onNotify) onNotify(`상태가 '${nextStatus}'(으)로 변경되었습니다.`);
        }
    };

    return (
        <div className="request-details">
            <h3>상세 정보</h3>
            <p>번호: {selectItem.id}</p>
            <p>요청자: {selectItem.requester}</p>
            <p>화물 종류: {selectItem.cargo}</p>
            <p>상태: {selectItem.status}</p>
            <p>날짜: {selectItem.date}</p>
            {selectItem.status === "완료" && (
                <>
                    <p>운임: {selectItem.fee?.toLocaleString()}원</p>
                    <p>수수료: {selectItem.commission?.toLocaleString()}원</p>
                </>
            )}
            <div style={{ margin: '12px 0' }}>
                <label>
                    <strong>상태 변경: </strong>
                    <select value={nextStatus} onChange={e => setNextStatus(e.target.value)} style={{ marginLeft: 8 }}>
                        {STATUS_OPTIONS.map(opt => (
                            <option key={opt} value={opt}>{opt}</option>
                        ))}
                    </select>
                </label>
                <CommonButton style={{ marginLeft: 10 }} onClick={handleChange} disabled={nextStatus === selectItem.status}>
                    변경
                </CommonButton>
            </div>
            {selectItem.status === "완료" && (
                <CommonButton onClick={() => onNotify ? onNotify(`수수료 정산 처리: ${selectItem.id}`) : alert(`수수료 정산 처리: ${selectItem.id}`)}>
                    수수료 정산
                </CommonButton>
            )}
        </div>
    );
}

export default RequestDetails;
