import React from 'react';
import { Link } from 'react-router-dom';
import './styles/Navbar.css';
import { useTheme } from './Components/Theme';

const Navbar = () => {
  const { isDarkMode, toggleTheme } = useTheme(); // Correct usage of the hook

  return (
    <nav className={`navbar ${isDarkMode ? 'dark' : 'light'}`}>
      <div className="navbar-logo">
        <h1>DevKin</h1>
      </div>
      <ul className="navbar-links">
        <li><Link to="/" className="navbar-link">Home</Link></li>
        <li><Link to="/about" className="navbar-link">About</Link></li>
      </ul>
      <div className="navbar-auth">
        <label className="switch">
          <input type="checkbox" checked={isDarkMode} onChange={toggleTheme} /> {/* Change to toggleTheme */}
          <span className="slider round"></span>
        </label>
        <Link to="/signin" className="navbar-link">Sign In</Link>
        <Link to="/signup" className="navbar-signup">Sign Up</Link> 
      </div>
    </nav>
  );
}

export default Navbar;
