import React from 'react';
import './styles/Navbar.css';
import { useTheme } from './Components/Theme';
import { Link, useNavigate } from 'react-router-dom';
import useUser from './Components/User';  // Import the custom hook

const DashboardNavbar = ({ onLogout }) => {
  const { isDarkMode, toggleTheme } = useTheme();
  const navigate = useNavigate();
  const { user, loading, error } = useUser();  // Fetch user info

  const handleLogout = () => {
    localStorage.clear();
    navigate('/signin');
  };

  if (loading) {
    return <p>Loading...</p>;  // Show loading while fetching
  }

  if (error) {
    return <p>Error: {error}</p>;  // Show error if there was a problem
  }

  return (
    <nav className={`navbar ${isDarkMode ? 'dark' : 'light'}`}>
      <div className="navbar-logo">
        <h1>DevKin</h1>
      </div>
      <ul className="navbar-links">
        <li><Link to="/dashboard" className="navbar-link">Dashboard</Link></li>
      </ul>
      <div className="navbar-auth">
        <label className="switch">
          <input type="checkbox" checked={isDarkMode} onChange={toggleTheme} />
          <span className="slider round"></span>
        </label>
        {user && <span className="navbar-username">{user.name}</span>}
        <button className="navbar-signup" onClick={handleLogout}>Logout</button>
      </div>
    </nav>
  );
};

export default DashboardNavbar;
