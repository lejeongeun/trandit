import React, { useState, useMemo } from 'react';
import '../styles/SettlementList.css';
import CommonButton from '../components/CommonButton';

// 더미 데이터 (RequestList와 동일 구조)
const initialData = Array.from({ length: 50 }, (_, i) => {
  const fee = 100000 + (i % 10) * 50000;
  const commission = Math.round(fee * 0.1);
  return {
    id: i + 1,
    requester: `요청자${i + 1}`,
    status: i % 3 === 0 ? '완료' : (i % 3 === 1 ? '운송 중' : '대기'),
    date: `2025-07-${String(1 + (i % 30)).padStart(2, '0')}`,
    fee,
    commission,
  };
});

function Modal({ open, onClose, children }) {
  if (!open) return null;
  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={e => e.stopPropagation()}>
        <button className="modal-close" onClick={onClose}>&times;</button>
        {children}
      </div>
    </div>
  );
}

export default function SettlementList() {
  const [data, setData] = useState(initialData);
  const [search, setSearch] = useState("");
  const [from, setFrom] = useState("");
  const [to, setTo] = useState("");
  const [modalOpen, setModalOpen] = useState(false);
  const [form, setForm] = useState({ requester: "", fee: "", commission: "", date: "" });
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 10;

  const filtered = useMemo(() => {
    let result = [...data];
    return result.filter(d =>
      d.status === '완료' &&
      (!search || d.requester.includes(search)) &&
      (!from || d.date >= from) &&
      (!to || d.date <= to)
    );
  }, [data, search, from, to]);

  // 페이지네이션 데이터 슬라이스
  const totalItems = filtered.length;
  const totalPages = Math.ceil(totalItems / itemsPerPage);
  const pagedData = filtered.slice((currentPage - 1) * itemsPerPage, currentPage * itemsPerPage);

  // 페이지 변경 핸들러
  const handlePageChange = (page) => {
    if (page < 1 || page > totalPages) return;
    setCurrentPage(page);
  };

  // 필터/검색 변경 시 1페이지로 이동
  React.useEffect(() => { setCurrentPage(1); }, [search, from, to]);

  const totalFee = filtered.reduce((sum, d) => sum + d.fee, 0);
  const totalCommission = filtered.reduce((sum, d) => sum + d.commission, 0);

  // 엑셀 다운로드 더미 핸들러
  const handleDownload = () => {
    alert('엑셀 다운로드 기능은 실제 배포 시 구현 필요');
  };

  const handleFormChange = e => {
    const { name, value } = e.target;
    setForm(f => ({ ...f, [name]: value }));
    if (name === 'fee') {
      setForm(f => ({ ...f, commission: value ? Math.round(Number(value) * 0.1) : "" }));
    }
  };
  const handleFormSubmit = e => {
    e.preventDefault();
    if (!form.requester || !form.fee || !form.commission || !form.date) return;
    setData(prev => [
      ...prev,
      {
        id: prev.length + 1,
        requester: form.requester,
        status: '완료',
        date: form.date,
        fee: Number(form.fee),
        commission: Number(form.commission),
      },
    ]);
    setModalOpen(false);
    setForm({ requester: "", fee: "", commission: "", date: "" });
  };

  return (
    <div className="settlement-list-container">
      <h2>수수료 정산 내역</h2>
      <div style={{ display: 'flex', gap: 12, marginBottom: 16, alignItems: 'center' }}>
        <CommonButton onClick={() => setModalOpen(true)} variant="primary">정산 등록</CommonButton>
        <input
          type="text"
          placeholder="요청자 검색"
          value={search}
          onChange={e => setSearch(e.target.value)}
        />
        <input
          type="date"
          value={from}
          onChange={e => setFrom(e.target.value)}
        />
        <span>~</span>
        <input
          type="date"
          value={to}
          onChange={e => setTo(e.target.value)}
        />
        <CommonButton onClick={handleDownload} variant="secondary">엑셀 다운로드</CommonButton>
      </div>
      <table className="settlement-table" style={{ width: '100%', borderCollapse: 'collapse', background: '#fff', borderRadius: 8, boxShadow: '0 2px 8px rgba(0,0,0,0.06)' }}>
        <thead>
          <tr>
            <th>ID</th>
            <th>요청자</th>
            <th>운임</th>
            <th>수수료</th>
            <th>정산일</th>
          </tr>
        </thead>
        <tbody>
          {pagedData.map(d => (
            <tr key={d.id}>
              <td>{d.id}</td>
              <td>{d.requester}</td>
              <td>{d.fee.toLocaleString()}원</td>
              <td>{d.commission.toLocaleString()}원</td>
              <td>{d.date}</td>
            </tr>
          ))}
        </tbody>
        <tfoot>
          <tr style={{ background: '#f7faff', fontWeight: 600 }}>
            <td colSpan={2}>합계</td>
            <td>{totalFee.toLocaleString()}원</td>
            <td>{totalCommission.toLocaleString()}원</td>
            <td></td>
          </tr>
        </tfoot>
      </table>
      {/* 페이지네이션 UI */}
      <div className="pagination">
        <CommonButton onClick={() => handlePageChange(currentPage - 1)} disabled={currentPage === 1} variant="secondary">이전</CommonButton>
        {Array.from({ length: totalPages }, (_, i) => (
          <CommonButton
            key={i + 1}
            onClick={() => handlePageChange(i + 1)}
            className={currentPage === i + 1 ? 'active' : ''}
            variant="secondary"
          >
            {i + 1}
          </CommonButton>
        ))}
        <CommonButton onClick={() => handlePageChange(currentPage + 1)} disabled={currentPage === totalPages} variant="secondary">다음</CommonButton>
      </div>
      <Modal open={modalOpen} onClose={() => setModalOpen(false)}>
        <form onSubmit={handleFormSubmit} style={{ display: 'flex', flexDirection: 'column', gap: 14, minWidth: 260 }}>
          <h3 style={{ margin: 0, color: '#2563eb' }}>정산 등록</h3>
          <input name="requester" placeholder="요청자" value={form.requester} onChange={handleFormChange} required />
          <input name="fee" type="number" placeholder="운임(숫자)" value={form.fee} onChange={handleFormChange} required min={0} />
          <input name="commission" type="number" placeholder="수수료(자동계산)" value={form.commission} onChange={handleFormChange} required min={0} readOnly style={{ background: '#f3f4f6' }} />
          <input name="date" type="date" value={form.date} onChange={handleFormChange} required />
          <CommonButton type="submit" variant="primary">등록</CommonButton>
        </form>
      </Modal>
    </div>
  );
} 