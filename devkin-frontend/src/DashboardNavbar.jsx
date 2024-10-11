import React from 'react';
import './styles/Navbar.css'; // Reusing the same CSS styles
import { useTheme } from './Components/Theme';
import { Link, useNavigate } from 'react-router-dom';

const DashboardNavbar = ({ username, onLogout }) => {
  const { isDarkMode, toggleTheme } = useTheme(); 
  const navigate = useNavigate();

  const handleLogout = () => {
    // Clear local storage
    localStorage.clear();
    // Redirect to the sign-in page
    navigate('/signin'); // Adjust this path as needed
  };

  return (
    <nav className={`navbar ${isDarkMode ? 'dark' : 'light'}`}>
      <div className="navbar-logo">
        <h1>DevKin</h1> {/* App logo */}
      </div>
      <ul className="navbar-links">
        <li><Link to="/dashboard" className="navbar-link">Dashboard</Link></li>
      </ul>
      <div className="navbar-auth">
        <label className="switch">
          <input type="checkbox" checked={isDarkMode} onChange={toggleTheme} />
          <span className="slider round"></span>
        </label>
        <span className="navbar-username">{username}</span>
        <button className="navbar-signup" onClick={handleLogout}>Logout</button>
      </div>
    </nav>
  );
}

export default DashboardNavbar;
