import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useTheme } from '../Components/Theme';
import '../styles/Sign.css';
import '../styles/popup.css'; // Ensure you are using the correct CSS file

const SignInPage = () => {
  const { isDarkMode } = useTheme();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();
  const [showPopup, setShowPopup] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    try {
      const response = await fetch('http://localhost:8080/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ email, password }),
      });
      
      const data = await response.json();
      if (response.ok && data.token) {
        localStorage.setItem('authToken', data.token);
        localStorage.setItem('email', email);
        navigate('/dashboard');
      } else {
        alert(data.message || 'Invalid credentials');
      }
    } catch (error) {
      alert('An error occurred while signing in. Please try again later.');
    }
  };

  const checkExistingUser = (e) => {
    e.preventDefault(); // Prevent default form submission behavior
    const existingToken = localStorage.getItem('authToken');
    const existingEmail = localStorage.getItem('email');

    if (existingToken && existingEmail) {
      setShowPopup(true);
      setTimeout(() => {
        document.querySelector('.popup').classList.add('show');
      }, 100); 
    } else {
      handleSubmit(e); // Proceed with login
    }
  };

  const handleLogout = () => {
    localStorage.clear();
    setShowPopup(false);
  };

  const handleGitHubLogin = () => {
    // Redirect to GitHub OAuth authorization URL
    window.location.href = 'http://localhost:8080/oauth2/authorization/github';
  };

  const handleOAuthCallback = () => {
    const token = new URLSearchParams(window.location.search).get('token');
    
    if (token) {
      localStorage.setItem('authToken', token);
      navigate('/dashboard'); // Redirect to dashboard
    }
  };

  useEffect(() => {
    handleOAuthCallback();
    document.body.className = isDarkMode ? 'dark-mode-body' : 'light-mode-body';
  }, [isDarkMode]);

  return (
    <div className={`form-container ${isDarkMode ? 'dark-mode' : 'light-mode'}`}>
      <h2>Sign In</h2>
      <form onSubmit={checkExistingUser}>
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
          <div className="social-button github" onClick={handleGitHubLogin}>Sign up with GitHub</div>
        </div>
      </form>

      {showPopup && (
        <div className="popup-overlay">
          <div className={`popup ${isDarkMode ? 'dark-mode' : 'light-mode'} show`}>
            <h3>A User is already signed in</h3>
            <p>1- Use the already signed-in user to go to the dashboard</p>
            <p>2- Log out the old user and log in with the new user</p>
            <button
              onClick={() => {
                document.querySelector('.popup').classList.remove('show');
                setTimeout(() => {
                  setShowPopup(false);
                navigate('/dashboard'); 
                }, 300);
              }}
            >
              Option 1
            </button>

            <button
              onClick={(e) => {
                document.querySelector('.popup').classList.remove('show');
                setTimeout(() => {
                  setShowPopup(false); 
                  handleLogout(); 
                  handleSubmit(e);
                }, 300);
                
              }}
            >
              Option 2
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default SignInPage;
