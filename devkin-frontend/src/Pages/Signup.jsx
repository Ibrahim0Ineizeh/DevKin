import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useTheme } from '../Components/Theme';
import { useEffect } from 'react';
import '../styles/Sign.css';

const SignUpPage = () => {
  const { isDarkMode } = useTheme();
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await fetch('http://localhost:8080/auth/signup', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username, email, password }),
      });
  
      const data = await response.json();
      if (data.token) {
        localStorage.setItem('authToken', data.token);
        navigate('/dashboard');
      } else {
        alert('Sign-up failed, please try again.');
      }
    } catch (error) {
      console.error('Error during sign-up:', error);
      alert('Error during sign-up, please check your network connection.');
    }
  };

  useEffect(() => {
    document.body.className = isDarkMode ? 'dark-mode-body' : 'light-mode-body';
  }, [isDarkMode]);

  return (
    <div className={`form-container ${isDarkMode ? 'dark-mode' : 'light-mode'}`}>
      <h2>Sign Up</h2>
      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="username">Username</label>
          <input
            type="text"
            id="username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
        </div>
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
