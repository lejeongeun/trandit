import React from 'react';
import '../styles/Dashboard.css';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';

// 실제 요청/매칭/수수료 더미 데이터 (RequestList와 유사)
const dummyData = Array.from({ length: 50 }, (_, i) => {
  const fee = 100000 + (i % 10) * 50000;
  const commission = Math.round(fee * 0.1);
  // 월을 1~6월로 분산
  const month = (i % 6) + 1;
  return {
    id: i + 1,
    requester: `요청자${i + 1}`,
    cargo: i % 2 === 0 ? "물류" : "화물",
    status: i % 3 === 0 ? "완료" : (i % 3 === 1 ? "운송 중" : "대기"),
    date: `2025-0${month}-${String(1 + (i % 28)).padStart(2, '0')}`,
    origin: ["서울", "인천", "부산", "대전"][i % 4],
    destination: ["대전", "부산", "서울", "광주"][i % 4],
    type: i % 3 === 0 ? "매칭" : (i % 2 === 0 ? "제안" : "요청"),
    fee,
    commission,
  };
});

const today = '2025-07-03';
const todayData = dummyData.filter(d => d.date === today);
const todayNewRequests = todayData.length;
const todayMatched = todayData.filter(d => d.status === '완료').length;
const todayCommission = todayData.filter(d => d.status === '완료').reduce((sum, d) => sum + d.commission, 0);
const waitingCount = dummyData.filter(d => d.status === '대기').length;
const totalMatched = dummyData.filter(d => d.status === '완료').length;
const matchingRate = Math.round((totalMatched / dummyData.length) * 100);
const totalUsers = 2345; // 예시

// 월별 운송 현황 (간단 집계)
const monthlyData = Array.from({ length: 6 }, (_, i) => {
  const month = i + 1;
  const name = `${month}월`;
  const monthData = dummyData.filter(d => d.date.startsWith(`2025-0${month}`));
  return {
    name,
    '요청 건수': monthData.length,
    '매칭 건수': monthData.filter(d => d.status === '완료').length,
  };
});

export default function Dashboard() {
  return (
    <div className="dashboard-container">
      <h2>대시보드</h2>
      <div className="kpi-cards">
        <div className="card">
          <h3>오늘 신규 요청</h3>
          <p>{todayNewRequests}</p>
        </div>
        <div className="card">
          <h3>매칭 대기</h3>
          <p>{waitingCount}</p>
        </div>
        <div className="card">
          <h3>오늘 매칭 성공</h3>
          <p>{todayMatched}</p>
        </div>
        <div className="card">
          <h3>오늘 수수료 수익</h3>
          <p>₩ {todayCommission.toLocaleString()}</p>
        </div>
        <div className="card">
          <h3>전체 사용자</h3>
          <p>{totalUsers.toLocaleString()}</p>
        </div>
        <div className="card">
          <h3>매칭률</h3>
          <p>{matchingRate}%</p>
        </div>
      </div>
      <div className="chart-container">
        <h3>월별 운송 현황</h3>
        <ResponsiveContainer width="100%" height={300}>
            <BarChart data={monthlyData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="name" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Bar dataKey="요청 건수" fill="#8884d8" />
                <Bar dataKey="매칭 건수" fill="#82ca9d" />
            </BarChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
}
