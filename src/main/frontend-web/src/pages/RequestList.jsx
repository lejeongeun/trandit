import React, { useState, useCallback, useMemo, useEffect } from "react";
import "../styles/RequestList.css";
import RequestTable from "../components/RequestTable";
import RequestDetails from "../components/RequestDetails";
import CommonButton from '../components/CommonButton';
import { getRequests } from '../api/api';

function TypeTabs({ type, setType }) {
    const types = ["전체", "요청", "제안", "매칭"];
    return (
        <div className="type-tabs">
            {types.map((t) => (
                <CommonButton
                    key={t}
                    variant={type === t ? "contained" : "outlined"}
                    onClick={() => setType(t)}
                >
                    {t}
                </CommonButton>
            ))}
        </div>
    );
}

function FilterButtons({ filter, setFilter }) {
    const filters = ["전체", "대기", "완료"];
    return (
        <div className="filter-buttons">
            {filters.map((f) => (
                <CommonButton
                    key={f}
                    variant={filter === f ? "contained" : "outlined"}
                    onClick={() => setFilter(f)}
                >
                    {f}
                </CommonButton>
            ))}
        </div>
    );
}

function SearchSortBar({ search, setSearch, sortKey, setSortKey, sortOrder, setSortOrder }) {
    return (
        <div className="search-sort-bar">
            <input
                type="text"
                placeholder="요청자, 출발지, 도착지 검색"
                value={search}
                onChange={e => setSearch(e.target.value)}
            />
            <select value={sortKey} onChange={e => setSortKey(e.target.value)}>
                <option value="id">번호</option>
                <option value="date">날짜</option>
                <option value="status">상태</option>
            </select>
            <CommonButton onClick={() => setSortOrder(o => o === "asc" ? "desc" : "asc")}>{sortOrder === "asc" ? "▲" : "▼"}</CommonButton>
        </div>
    );
}

function Modal({ open, onClose, children }) {
    if (!open) return null;
    return (
        <div className="modal-overlay" onClick={onClose}>
            <div className="modal-content" onClick={e => e.stopPropagation()}>
                <CommonButton className="modal-close" onClick={onClose}>&times;</CommonButton>
                {children}
            </div>
        </div>
    );
}

function Toast({ message, onClose }) {
    if (!message) return null;
    setTimeout(onClose, 2000);
    return (
        <div className="toast">
            {message}
        </div>
    );
}

export default function RequestList() {
    const [selectedItem, setSelectedItem] = useState(null);
    const [data, setData] = useState([]);
    const [filter, setFilter] = useState("전체");
    const [type, setType] = useState("전체");
    const [search, setSearch] = useState("");
    const [sortKey, setSortKey] = useState("id");
    const [sortOrder, setSortOrder] = useState("desc");
    const [toast, setToast] = useState("");
    // 페이지네이션 상태 추가
    const [currentPage, setCurrentPage] = useState(1);
    const itemsPerPage = 10;

    useEffect(() => {
        const fetchRequests = async () => {
            try {
                const response = await getRequests();
                setData(response.data);
            } catch (error) {
                console.error("Error fetching requests:", error);
            }
        };
        fetchRequests();
    }, []);

    const handleStatusChange = useCallback((id, newStatus) => {
        setData((prevData) =>
            prevData.map((item) =>
                item.id === id ? { ...item, status: newStatus } : item
            )
        );
    }, []);

    const processedData = useMemo(() => {
        let result = [...data];
        // 타입 필터
        if (type !== "전체") {
            result = result.filter(item => item.type === type);
        }
        // 상태 필터
        if (filter !== "전체") {
            result = result.filter(item => item.status === filter);
        }
        // 검색
        if (search.trim()) {
            const s = search.trim();
            result = result.filter(item =>
                item.requester.includes(s) ||
                (item.origin && item.origin.includes(s)) ||
                (item.destination && item.destination.includes(s))
            );
        }
        // 정렬
        result.sort((a, b) => {
            let v1 = a[sortKey], v2 = b[sortKey];
            if (sortKey === "date") {
                v1 = v1.replace(/-/g, "");
                v2 = v2.replace(/-/g, "");
            }
            if (v1 < v2) return sortOrder === "asc" ? -1 : 1;
            if (v1 > v2) return sortOrder === "asc" ? 1 : -1;
            return 0;
        });
        return result;
    }, [data, type, filter, search, sortKey, sortOrder]);

    // 페이지네이션 데이터 슬라이스
    const totalItems = processedData.length;
    const totalPages = Math.ceil(totalItems / itemsPerPage);
    const pagedData = processedData.slice((currentPage - 1) * itemsPerPage, currentPage * itemsPerPage);

    // 페이지 변경 핸들러
    const handlePageChange = (page) => {
        if (page < 1 || page > totalPages) return;
        setCurrentPage(page);
    };

    // 필터/검색/정렬 변경 시 1페이지로 이동
    React.useEffect(() => { setCurrentPage(1); }, [type, filter, search, sortKey, sortOrder]);

    return (
        <div className="request-list-container">
            <h2>요청/제안/매칭 관리 페이지</h2>
            <TypeTabs type={type} setType={setType} />
            <FilterButtons filter={filter} setFilter={setFilter} />
            <SearchSortBar
                search={search}
                setSearch={setSearch}
                sortKey={sortKey}
                setSortKey={setSortKey}
                sortOrder={sortOrder}
                setSortOrder={setSortOrder}
            />
            <div style={{ overflowX: 'auto', width: '100%' }}>
                <RequestTable
                    filterData={pagedData}
                    selectItem={selectedItem}
                    setselectItem={setSelectedItem}
                />
            </div>
            {/* 페이지네이션 UI */}
            <div className="pagination">
                <CommonButton onClick={() => handlePageChange(currentPage - 1)} disabled={currentPage === 1}>이전</CommonButton>
                {Array.from({ length: totalPages }, (_, i) => (
                    <CommonButton
                        key={i + 1}
                        onClick={() => handlePageChange(i + 1)}
                        className={currentPage === i + 1 ? 'active' : ''}
                    >
                        {i + 1}
                    </CommonButton>
                ))}
                <CommonButton onClick={() => handlePageChange(currentPage + 1)} disabled={currentPage === totalPages}>다음</CommonButton>
            </div>
            <Modal open={!!selectedItem} onClose={() => setSelectedItem(null)}>
                {selectedItem && (
                    <RequestDetails
                        selectItem={selectedItem}
                        handleStatusChange={handleStatusChange}
                        setselectItem={setSelectedItem}
                        onNotify={msg => setToast(msg)}
                    />
                )}
            </Modal>
            <Toast message={toast} onClose={() => setToast("")} />
        </div>
    );
}
