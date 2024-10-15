import React from 'react';
import '../styles/About.css';
import { useTheme } from '../Components/Theme'; 
import useUser from '../Components/User';  // Import the custom hook

const Home = () => {
  const { isDarkMode } = useTheme(); 
  const { user, loading, error } = useUser();  // Fetch user info

  if (loading) {
    return <p>Loading...</p>;
  }

  if (error) {
    return <p>Error: {error}</p>;
  }

  return (
    <div className={`container ${isDarkMode ? 'dark-mode' : 'light-mode'}`}>
      <h2>Dashboard</h2>
      {user ? (
        <p>Welcome, {user.name}! This is your dashboard.</p>
      ) : (
        <p>No user information available.</p>
      )}
    </div>
  ); 
}

export default Home;
