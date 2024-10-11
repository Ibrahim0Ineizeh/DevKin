// pages/Home.jsx
import React from 'react';
import '../styles/About.css';
import { useTheme } from '../Components/Theme'; 

const Home = () => {
  const { isDarkMode, toggleTheme } = useTheme(); // Keeping theme toggle

  return (
    <div  className={`container ${isDarkMode ? 'dark-mode' : 'light-mode'}`}>
      <h2>Dashboard</h2>
      <p>this is your dashboard</p>
    </div>
  );
}

export default Home;
