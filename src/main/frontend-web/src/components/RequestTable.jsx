import React from 'react';

function RequestTable({ filterData, selectItem, setselectItem }) {
    return (
        <table className="request-table">
            <thead>
                <tr>
                    <th>번호</th>
                    <th>유형</th>
                    <th>요청자</th>
                    <th>화물 종류</th>
                    <th>상태</th>
                    <th>출발지</th>
                    <th>도착지</th>
                    <th>날짜</th>
                </tr>
            </thead>
            <tbody>
                {filterData.map((item) => (
                    <tr
                        key={item.id}
                        onClick={() => setselectItem(item)}
                        className={selectItem && selectItem.id === item.id ? "selected" : ""}
                    >
                        <td>{item.id}</td>
                        <td>{item.type}</td>
                        <td>{item.requester}</td>
                        <td>{item.cargo}</td>
                        <td>{item.status}</td>
                        <td>{item.origin}</td>
                        <td>{item.destination}</td>
                        <td>{item.date}</td>
                    </tr>
                ))}
            </tbody>
        </table>
    );
}

export default RequestTable;
