import { Link, useLocation } from "react-router-dom";
import { FaTachometerAlt, FaListAlt, FaUsers, FaFileInvoiceDollar } from "react-icons/fa";
import '../styles/Sidebar.css';

export default function Sidebar() {
  const location = useLocation();
  const menu = [
    { to: "/dashboard", label: "대시보드", icon: <FaTachometerAlt /> },
    { to: "/requests", label: "요청리스트", icon: <FaListAlt /> },
    { to: "/users", label: "사용자관리", icon: <FaUsers /> },
    { to: "/settlement", label: "정산 내역", icon: <FaFileInvoiceDollar /> },
  ];
  return (
    <nav className="sidebar-nav">
      <ul>
        {menu.map(item => (
          <li key={item.to} className={location.pathname === item.to ? "active" : ""}>
            <Link to={item.to}>
              <span className="icon">{item.icon}</span>
              <span className="label">{item.label}</span>
            </Link>
          </li>
        ))}
      </ul>
    </nav>    
  );
} 