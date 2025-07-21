import React, { useState, useMemo } from 'react';
import '../styles/UserList.css';
import CommonButton from '../components/CommonButton';

const dummyRoles = ["관리자", "사용자", "화물주"];
const dummyUsers = Array.from({ length: 20 }, (_, i) => ({
  id: i + 1,
  name: `사용자${i + 1}`,
  role: i % 5 === 0 ? '화물주' : (i % 3 === 0 ? '관리자' : '사용자'),
  email: `user${i + 1}@example.com`,
  joinDate: `2025-01-${String(1 + (i % 30)).padStart(2, '0')}`,
  isActive: i % 5 !== 0,
}));

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

export default function UserList() {
  const [selectedUser, setSelectedUser] = useState(null);
  const [users, setUsers] = useState(dummyUsers);
  const [search, setSearch] = useState("");
  const [roleFilter, setRoleFilter] = useState("전체");
  const [activeFilter, setActiveFilter] = useState("전체");
  const [modalOpen, setModalOpen] = useState(false);
  const [form, setForm] = useState({ name: "", email: "", role: "사용자", isActive: true });
  // 페이지네이션 상태 추가
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 10;

  const filteredUsers = useMemo(() => {
    let result = [...users];
    if (roleFilter !== "전체") {
      result = result.filter(u => u.role === roleFilter);
    }
    if (activeFilter !== "전체") {
      result = result.filter(u => (activeFilter === "활성" ? u.isActive : !u.isActive));
    }
    if (search.trim()) {
      const s = search.trim();
      result = result.filter(u => u.name.includes(s) || u.email.includes(s));
    }
    return result;
  }, [users, search, roleFilter, activeFilter]);

  // 페이지네이션 데이터 슬라이스
  const totalItems = filteredUsers.length;
  const totalPages = Math.ceil(totalItems / itemsPerPage);
  const pagedUsers = filteredUsers.slice((currentPage - 1) * itemsPerPage, currentPage * itemsPerPage);

  // 페이지 변경 핸들러
  const handlePageChange = (page) => {
    if (page < 1 || page > totalPages) return;
    setCurrentPage(page);
  };

  // 필터/검색 변경 시 1페이지로 이동
  React.useEffect(() => { setCurrentPage(1); }, [roleFilter, activeFilter, search]);

  const handleRoleChange = (id, newRole) => {
    setUsers(prev => prev.map(u => u.id === id ? { ...u, role: newRole } : u));
    setSelectedUser(prev => prev && prev.id === id ? { ...prev, role: newRole } : prev);
  };
  const handleActiveToggle = (id) => {
    setUsers(prev => prev.map(u => u.id === id ? { ...u, isActive: !u.isActive } : u));
    setSelectedUser(prev => prev && prev.id === id ? { ...prev, isActive: !prev.isActive } : prev);
  };

  // 사용자 등록 폼 핸들러
  const handleFormChange = e => {
    const { name, value, type, checked } = e.target;
    setForm(f => ({ ...f, [name]: type === 'checkbox' ? checked : value }));
  };
  const handleFormSubmit = e => {
    e.preventDefault();
    if (!form.name || !form.email) return;
    setUsers(prev => [
      ...prev,
      {
        id: prev.length + 1,
        name: form.name,
        email: form.email,
        role: form.role,
        isActive: form.isActive,
        joinDate: new Date().toISOString().slice(0, 10),
      },
    ]);
    setModalOpen(false);
    setForm({ name: "", email: "", role: "사용자", isActive: true });
  };

  return (
    <div className="user-list-container">
      <h2>사용자 & 화물주 계정 관리</h2>
      <div style={{ display: 'flex', gap: 12, marginBottom: 16, alignItems: 'center' }}>
        <CommonButton onClick={() => setModalOpen(true)} variant="primary">사용자 등록</CommonButton>
        <input
          type="text"
          placeholder="이름, 이메일 검색"
          value={search}
          onChange={e => setSearch(e.target.value)}
        />
        <select value={roleFilter} onChange={e => setRoleFilter(e.target.value)}>
          <option value="전체">전체 역할</option>
          <option value="관리자">관리자</option>
          <option value="사용자">사용자</option>
          <option value="화물주">화물주</option>
        </select>
        <select value={activeFilter} onChange={e => setActiveFilter(e.target.value)}>
          <option value="전체">전체 상태</option>
          <option value="활성">활성</option>
          <option value="비활성">비활성</option>
        </select>
      </div>
      <div style={{ overflowX: 'auto', width: '100%' }}>
        <table className="user-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>이름</th>
              <th>역할</th>
              <th>이메일</th>
              <th>가입일</th>
              <th>상태</th>
            </tr>
          </thead>
          <tbody>
            {pagedUsers.map(user => (
              <tr key={user.id} onClick={() => setSelectedUser(user)} className={selectedUser && selectedUser.id === user.id ? 'selected' : ''}>
                <td>{user.id}</td>
                <td>{user.name}</td>
                <td>{user.role}</td>
                <td>{user.email}</td>
                <td>{user.joinDate}</td>
                <td>{user.isActive ? '활성' : '비활성'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
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
      <Modal open={!!selectedUser} onClose={() => setSelectedUser(null)}>
        {selectedUser && (
          <div className="user-details-modal">
            <h3>상세 정보</h3>
            <p><strong>ID:</strong> {selectedUser.id}</p>
            <p><strong>이름:</strong> {selectedUser.name}</p>
            <p><strong>이메일:</strong> {selectedUser.email}</p>
            <p><strong>가입일:</strong> {selectedUser.joinDate}</p>
            <div style={{ margin: '12px 0' }}>
              <label>
                <strong>역할:</strong>
                <select
                  value={selectedUser.role}
                  onChange={e => handleRoleChange(selectedUser.id, e.target.value)}
                  style={{ marginLeft: 8 }}
                >
                  <option value="관리자">관리자</option>
                  <option value="사용자">사용자</option>
                  <option value="화물주">화물주</option>
                </select>
              </label>
            </div>
            <div style={{ margin: '12px 0' }}>
              <strong>상태:</strong> {selectedUser.isActive ? '활성' : '비활성'}
              <CommonButton
                style={{ marginLeft: 12 }}
                onClick={() => handleActiveToggle(selectedUser.id)}
                variant="secondary"
              >
                {selectedUser.isActive ? '정지' : '활성화'}
              </CommonButton>
            </div>
          </div>
        )}
      </Modal>
      <Modal open={modalOpen} onClose={() => setModalOpen(false)}>
        <form onSubmit={handleFormSubmit} style={{ display: 'flex', flexDirection: 'column', gap: 14, minWidth: 260 }}>
          <h3 style={{ margin: 0, color: '#2563eb' }}>사용자 등록</h3>
          <input name="name" placeholder="이름" value={form.name} onChange={handleFormChange} required />
          <input name="email" type="email" placeholder="이메일" value={form.email} onChange={handleFormChange} required />
          <select name="role" value={form.role} onChange={handleFormChange} required>
            {dummyRoles.map(r => <option key={r} value={r}>{r}</option>)}
          </select>
          <label style={{ fontSize: '0.98em', color: '#444' }}>
            <input name="isActive" type="checkbox" checked={form.isActive} onChange={handleFormChange} style={{ marginRight: 6 }} />
            활성화
          </label>
          <CommonButton type="submit" variant="primary">등록</CommonButton>
        </form>
      </Modal>
    </div>
  );
}
