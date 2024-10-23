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
    
    const authToken = localStorage.getItem('authToken'); // Get the auth token from localStorage
    const trimmedProjectName = projectName.trim();
    // Send project data to the backend
    try {
      const response = await fetch('http://localhost:8080/dashboard/project/create', {
        method: 'POST',
        body: JSON.stringify({
          name: trimmedProjectName,
          description,
          language,
        }),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${authToken}`, // Include the auth token
        },
      });

      if (!response.ok) {
        throw new Error('Failed to create project');
      }

      // Clear inputs after successful creation
      setProjectName('');
      setDescription('');
      setLanguage('');

      // Navigate back to the dashboard
      navigate('/dashboard');
    } catch (error) {
      console.error('Error:', error);
      // Handle error (e.g., display error message)
    }
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
