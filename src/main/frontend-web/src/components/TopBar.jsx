import React from 'react';
import '../styles/TopBar.css';

function TopBar() {
  // 로그아웃 핸들러 (임시)
  const handleLogout = () => {
    // 토큰 삭제 등 실제 로그아웃 로직 필요
    localStorage.removeItem('token');
    window.location.href = '/';
  };

  return (
    <div className="topbar">
      <div className="topbar-content">
        <div className="topbar-left">
          <a href="/dashboard" className="topbar-logo">
            Trandit <span>Admin</span>
          </a>
        </div>
        <div className="topbar-right">
          <span className="topbar-admin">관리자</span>
          <button className="topbar-logout" onClick={handleLogout}>
            로그아웃
          </button>
        </div>
      </div>
    </div>
  );
}

export default TopBar; 