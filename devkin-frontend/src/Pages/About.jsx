import React from 'react';
import '../styles/About.css';
import { useTheme } from '../Components/Theme'; 
import { useNavigate } from 'react-router-dom';

const About = () => {
  const { isDarkMode } = useTheme();
  const navigate = useNavigate();

  return (
    <div className={`about-container ${isDarkMode ? 'dark-mode' : 'light-mode'}`}>
      <div className="container">
        <h1>About DevKin</h1>
        <p className="para">DevKin is a collaborative platform for developers to work together on coding projects, share ideas, and build innovative solutions.</p>

        <section className="features">
          <div className="feature-item">
            <h3>Real-Time Collaboration</h3>
            <p>Work with your team in real-time using our collaborative coding editor.</p>
          </div>
          <div className="feature-item">
            <h3>Project Management</h3>
            <p>Easily manage multiple projects and track their progress all in one place.</p>
          </div>
          <div className="feature-item">
            <h3>Cloud Storage</h3>
            <p>Store and access your code securely with AWS S3 integration.</p>
          </div>
          <div className="feature-item">
            <h3>Seamless Experience</h3>
            <p>Enjoy a smooth and intuitive interface that makes collaboration a breeze.</p>
          </div>
        </section>

        <section className="cta">
          <button className="cta-button" onClick={() => {navigate('/signin');}}>Get Started</button>
        </section>
      </div>
    </div>
  );
};

export default About;