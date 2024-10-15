import React from 'react';
import { useTheme } from '../Components/Theme'; 
import '../styles/Home.css';
import logoLight from '../assets/devkinlight.png';
import logoDark from '../assets/devkindark.png';
import { useNavigate } from 'react-router-dom';

const Home = () => {
  const navigate = useNavigate();
  const { isDarkMode } = useTheme(); // Use theme context

  return (
    <div className={`container ${isDarkMode ? 'dark-mode' : 'light-mode'}`}>
      <img 
        src={isDarkMode ? logoDark : logoLight} 
        alt="devkin logo" 
        className="logoimage"
      />
      <h2>Your Collaborative Development Platform</h2>
      <p className='para'>
        DevKin is a collaborative web application designed for developers to work together in real time. 
        Whether you're building a project from scratch or collaborating on an existing one, DevKin provides 
        the tools you need to enhance teamwork and boost productivity.
      </p>
      
      <h2>Main Features</h2>
      <section className="features">
        <div className="feature-item">
          <h3>Real-Time Collaboration</h3>
          <p>
            Work with your team simultaneously on the same codebase. Changes are reflected instantly, ensuring 
            everyone is always on the same page.
          </p>
        </div>
        <div className="feature-item">
          <h3>Project Management</h3>
          <p>
            Organize your projects efficiently with our integrated management tools. Assign tasks, track progress, 
            and manage deadlines all in one place.
          </p>
        </div>
        <div className="feature-item">
          <h3>Code Editor</h3>
          <p>
            Our intuitive code editor supports multiple languages, providing syntax highlighting and autocompletion 
            to enhance your coding experience.
          </p>
        </div>
        <div className="feature-item">
          <h3>Secure Environment</h3>
          <p>
            Your projects are safe with us. DevKin uses top-notch security measures to ensure that your code 
            and data remain confidential.
          </p>
        </div>
      </section>

      <section className="cta">
        <h2>Ready to Get Started?</h2>
        <p>
          Join us today and experience a new way of coding together!
        </p>
        <button className="cta-button" onClick={() => {navigate('/signup');}}>Sign Up Now</button>
      </section>
    </div>
  );
}

export default Home;