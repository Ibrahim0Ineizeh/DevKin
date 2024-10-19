import React from 'react';
import '../styles/Dashboard.css'; // Ensure the CSS is properly imported
import { useTheme } from '../Components/Theme'; // Custom hook for dark/light mode
import useUser from '../Components/User'; // Custom hook to get user data
import { useNavigate } from 'react-router-dom'; // For navigation

const Dashboard = () => {
  const { isDarkMode } = useTheme(); // Get dark mode info from theme
  const { user, loading, error } = useUser(); // Get user data
  const navigate = useNavigate(); // Use useNavigate from react-router-dom version 6

  // Example data if no user projects exist
  const fallbackProjects = Array.from({ length: 10 }, (_, index) => ({
    id: index + 1,
    name: `Project ${index + 1}`,
    description: `Description for project ${index + 1}.`,
    lastModified: new Date(
      Date.now() - Math.floor(Math.random() * 1000000000)
    ).toLocaleDateString(),
  }));

  const projects = user?.projects?.length ? user.projects : fallbackProjects;

  if (loading) {
    return <p>Loading...</p>;
  }

  if (error) {
    return <p>Error: {error}</p>;
  }

  const handleCreateProject = () => {
    navigate('/dashboard/createProject'); // Navigate to the create project page
  };

  return (
    <div className={`dashboard-container ${isDarkMode ? 'dark-mode' : 'light-mode'}`}>
      {/* Left section: Project cards */}
      <div className="project-list">
        {projects.map((project) => (
          <div key={project.id} className="project-card">
            <h3>{project.name}</h3>
            <p>{project.description}</p>
            <p>Last Modified: {project.lastModified}</p>
          </div>
        ))}
      </div>

      {/* Right section: Create project button */}
      <div className="right-column">
        <button className="create-project-btn" onClick={handleCreateProject}>
          Create Project
        </button>
      </div>
    </div>
  );
};

export default Dashboard;
