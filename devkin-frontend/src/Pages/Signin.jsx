import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useTheme } from '../Components/Theme';
import { useEffect } from 'react';
import '../styles/Sign.css';

const SignInPage = () => {
  const { isDarkMode } = useTheme();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    // Make a request to your authentication API
    const response = await fetch('http://localhost:8080/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ email, password }),
    });
    
    const data = await response.json();
    if (data.token) {
      // Save the token in localStorage
      localStorage.setItem('authToken', data.token);
      // Navigate to the dashboard
      navigate('/dashboard');
    } else {
      alert('Invalid credentials');
    }
  };

  useEffect(() => {
    document.body.className = isDarkMode ? 'dark-mode-body' : 'light-mode-body';
  }, [isDarkMode]);


  return (
    <div className={`form-container ${isDarkMode ? 'dark-mode' : 'light-mode'}`}>
      <h2>Sign In</h2>
      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="email">Email</label>
          <input
            type="email"
            id="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </div>
        <div>
          <label htmlFor="password">Password</label>
          <input
            type="password"
            id="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </div>
        <button type="submit">Sign In</button>
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

export default SignInPage;
