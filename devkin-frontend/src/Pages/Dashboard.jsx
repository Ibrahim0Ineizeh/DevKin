import React, { useEffect, useState } from 'react';
import '../styles/Dashboard.css'; // Ensure the CSS is properly imported
import { useTheme } from '../Components/Theme'; // Custom hook for dark/light mode
import { useNavigate } from 'react-router-dom'; // For navigation

const Dashboard = () => {
  const { isDarkMode } = useTheme(); // Get dark mode info from theme
  const navigate = useNavigate(); // Use useNavigate from react-router-dom version 6
  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    // Get user email from localStorage
    const userEmail = localStorage.getItem('email');
    const authToken = localStorage.getItem('authToken');
    if (!userEmail) {
      setError('User email not found.');
      setLoading(false);
      return;
    }

    // Fetch projects from the backend with the user email as a query parameter
    fetch(`http://localhost:8080/dashboard/project/user-projects?email=${userEmail}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${authToken}`,
      },
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error('Failed to fetch projects');
        }
        return response.json();
      })
      .then((data) => {
        setProjects(data); // Set the project data from the server
        setLoading(false);
      })
      .catch((err) => {
        setError(err.message);
        setLoading(false);
      });
  }, []);

  if (loading) {
    return <p>Loading...</p>;
  }

  if (error) {
    return <p>Error: {error}</p>;
  }

  const handleCreateProject = () => {
    navigate('/dashboard/createProject'); // Navigate to the create project page
  };

  const handleProjectClick = (project) => {
    localStorage.setItem('projectInfo', JSON.stringify(project));
    navigate(`/projects/${project.slug}`); // Navigate to the project page
  };

  return (
    <div className={`dashboard-container ${isDarkMode ? 'dark-mode' : 'light-mode'}`}>
      {/* Left section: Project cards */}
      <div className="project-list">
        {projects.map((project) => (
          <div key={project.slug} className="project-card">
            {/* Project name as a link */}
            <h3>
              <span onClick={() => handleProjectClick(project)} className="project-link" style={{ cursor: 'pointer' }}>
                {project.name}
              </span>
            </h3>
            <p className="project-slug">{project.slug}</p>
            <p>{project.description}</p>
            <p><strong>Language:</strong> {project.language}</p>
            <p><strong>Size:</strong> {project.size} KB</p>
            <p><strong>Created At:</strong> {new Date(project.createdAt).toLocaleDateString()}</p>
            <p><strong>Last Modified:</strong> {new Date(project.lastModified).toLocaleDateString()}</p>
            <p><strong>URL:</strong> <a href="" target="_blank" rel="noopener noreferrer">{project.url}</a></p>
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
