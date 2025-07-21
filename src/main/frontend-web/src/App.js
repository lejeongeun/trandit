import { BrowserRouter, Routes, Route, Navigate, useLocation } from 'react-router-dom';
import Dashboard from './pages/Dashboard';
import RequestList from './pages/RequestList';
import UserList from './pages/UserList';
import SettlementList from './pages/SettlementList';
import Sidebar from './components/Sidebar';
import TopBar from './components/TopBar';
import Login from './pages/Login';

function PrivateRoute({ children }) {
  const token = localStorage.getItem('token');
  const location = useLocation();
  return token ? children : <Navigate to="/login" state={{ from: location }} replace />;
}

function Layout({ children }) {
  return (
    <>
      <TopBar />
      <Sidebar />
      <div className="main-content" style={{ paddingTop: 56 }}>{children}</div>
    </>
  );
}

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route
          path="/*"
          element={
            <PrivateRoute>
              <Layout>
                <Routes>
                  <Route path="/dashboard" element={<Dashboard />} />
                  <Route path="/requests" element={<RequestList />} />
                  <Route path="/users" element={<UserList />} />
                  <Route path="/settlement" element={<SettlementList />} />
                </Routes>
              </Layout>
            </PrivateRoute>
          }
        />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
