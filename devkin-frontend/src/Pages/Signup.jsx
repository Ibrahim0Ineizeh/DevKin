import React from 'react';
import { useEffect } from 'react';
import { useTheme } from '../Components/Theme'; 
import '../styles/Sign.css';

const SignUpPage = () => {
  const { isDarkMode } = useTheme();

  useEffect(() => {
    document.body.className = isDarkMode ? 'dark-mode-body' : 'light-mode-body';
  }, [isDarkMode]);


  return (
    <div className={`form-container ${isDarkMode ? 'dark-mode' : 'light-mode'}`}>
      <h2>Sign Up</h2>
      <form>
        <div>
          <label htmlFor="username">Username</label>
          <input type="text" id="username" required />
        </div>
        <div>
          <label htmlFor="email">Email</label>
          <input type="email" id="email" required />
        </div>
        <div>
          <label htmlFor="password">Password</label>
          <input type="password" id="password" required />
        </div>
        <button type="submit">Sign Up</button>

        <div className="social-buttons">
          <div className="social-button github">
            Sign up with GitHub
          </div>
          <div className="social-button google">
            Sign up with Google
          </div>
        </div>
      </form>
    </div>
  );
};

export default SignUpPage;
