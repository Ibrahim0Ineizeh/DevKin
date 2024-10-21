import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/CreateProject.css'; 
import { useTheme } from '../Components/Theme';

const CreateProject = () => {
  const { isDarkMode } = useTheme(); // Get the theme mode
  const [projectName, setProjectName] = useState('');
  const [description, setDescription] = useState('');
  const [language, setLanguage] = useState('');
  const navigate = useNavigate(); // For redirecting after creation

  const handleSubmit = async (e) => {
    e.preventDefault();
    // Send project data to backend (e.g., via fetch or axios)
    // Example:
    // await fetch('/api/projects', {
    //   method: 'POST',
    //   body: JSON.stringify({ projectName, description }),
    //   headers: { 'Content-Type': 'application/json' }
    // });

    // After successful creation, navigate back to the dashboard
    navigate('/dashboard');
  };

  return (
    <div className={`create-project-container ${isDarkMode ? 'dark-mode' : 'light-mode'}`}>
      <h2>Create New Project</h2>
      <form onSubmit={handleSubmit} className="create-project-form">
        <div className="form-group">
          <label htmlFor="projectName">Project Name:</label>
          <input
            type="text"
            id="projectName"
            value={projectName}
            onChange={(e) => setProjectName(e.target.value)}
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="language">Programming Language:</label>
          <select
            id="language"
            value={language}
            onChange={(e) => setLanguage(e.target.value)}
            required
          >
            <option value="javascript">JavaScript</option>
            <option value="python">Python</option>
          </select>
        </div>

        <div className="form-group">
          <label htmlFor="description">Project Description:</label>
          <textarea
            id="description"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            required
          ></textarea>
        </div>

        <button type="submit" className="create-project-btn">
          Create Project
        </button>
      </form>
    </div>
  );

  
};

export default CreateProject;
