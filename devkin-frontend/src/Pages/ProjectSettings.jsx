import React, { useEffect, useState } from 'react';
import { useTheme } from '../Components/Theme'; 
import { useParams, useNavigate } from 'react-router-dom';
import '../styles/ProjectPage.css';

const ProjectSettings = () => {
    const { slug } = useParams();
    const { isDarkMode } = useTheme();
    const navigate = useNavigate();
    const [project, setProject] = useState(null);
    const [activeTab, setActiveTab] = useState('description');

    useEffect(() => {
        fetch(`http://localhost:8080/dashboard/project/get?projectSlug=${slug}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('authToken')}`,
            },
        })
            .then((response) => {
                if (!response.ok) throw new Error('Failed to fetch project details');
                return response.json();
            })
            .then((data) => {
                setProject(data);
                localStorage.setItem('projectInfo', JSON.stringify(data));
            })
            .catch((err) => console.error(err.message));

        return () => {
            localStorage.removeItem('projectInfo');
        };
    }, [slug]);

    const handleBackClick = () => {
        if (project) {
            navigate(`/projects/${project.slug}`); // Navigate to the project page
        }
    };

    const handleTabChange = (tab) => {
        setActiveTab(tab);
    };

    const handleDeleteProject = () => {
        if (window.confirm('Are you sure you want to delete this project? This action cannot be undone.')) {
            fetch(`http://localhost:8080/dashboard/project/delete?projectSlug=${slug}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('authToken')}`,
                },
            })
                .then((response) => {
                    if (!response.ok) throw new Error('Failed to delete the project');
                    // Optionally navigate back to the projects list or show a success message
                    navigate('/projects'); // Navigate to projects list after deletion
                })
                .catch((err) => console.error(err.message));
        }
    };

    return (
        <div className={`project-page-container ${isDarkMode ? 'dark-mode' : 'light-mode'}`}>
            <div className='settingControl'>
                <button className='backbutton' onClick={handleBackClick}>Back</button>
                <button className='delete-button' onClick={handleDeleteProject}>Delete Project</button>
            </div>
            <h1 className='projectSettings'>{project ? project.name : 'Loading...'}</h1>
            <div className="tabs">
                <button onClick={() => handleTabChange('description')} className={activeTab === 'description' ? 'active' : ''}>
                    Update Project 
                </button>
                <button onClick={() => handleTabChange('users')} className={activeTab === 'users' ? 'active' : ''}>
                    Add Developers
                </button>
            </div>
            <div className="tab-content">
                {activeTab === 'description' && (
                    <div>
                        <h2>Update Project </h2>
                        {/* Implement the form for updating description and language here */}
                    </div>
                )}
                {activeTab === 'users' && (
                    <div>
                        <h2>Add User</h2>
                        {/* This tab will be implemented later */}
                    </div>
                )}
            </div>
           
        </div>
    );
};

export default ProjectSettings;
