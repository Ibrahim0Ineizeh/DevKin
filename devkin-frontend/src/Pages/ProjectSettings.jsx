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
    const [description, setDescription] = useState('');
    const [language, setLanguage] = useState('Python'); // Default to 'Python'

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
                setDescription(data.description);
                setLanguage(data.language || 'Python'); // Set language to default if null
                localStorage.setItem('projectInfo', JSON.stringify(data));
            })
            .catch((err) => console.error(err.message));

        return () => {
            localStorage.removeItem('projectInfo');
        };
    }, [slug]);

    const handleBackClick = () => {
        if (project) {
            navigate(`/projects/${project.slug}`);
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
                    navigate('/dashboard');
                })
                .catch((err) => console.error(err.message));
        }
    };

    const handleUpdateProject = (e) => {
        e.preventDefault();
        fetch(`http://localhost:8080/dashboard/project/update`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('authToken')}`,
            },
            body: JSON.stringify({ projectSlug: slug, description:description, language:language }),
        })
            .then((response) => {
                if (!response.ok) throw new Error('Failed to update project details');
                return response.json();
            })
            .then((data) => {
                setProject(data); // Update project details in the state
                alert('Project updated successfully!');
            })
            .catch((err) => console.error(err.message));
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
                    <div className='updateContainer'>
                        <h2>Update Project</h2>
                        <form onSubmit={handleUpdateProject}>
                        <label className='project-language-label'>
                                Language:
                                <select 
                                    className='project-language-select'
                                    value={language}
                                    onChange={(e) => setLanguage(e.target.value)}
                                >
                                    <option value="Python">Python</option>
                                    <option value="JavaScript">JavaScript</option>
                                </select>
                            </label>
                            <label className='project-description-label'>
                                Description:
                                <textarea 
                                    className='project-description-textarea'
                                    value={description}
                                    onChange={(e) => setDescription(e.target.value)}
                                    rows="4"
                                    placeholder="Enter project description"
                                />
                            </label>
                            <button className='project-update-button' type="submit">Save Changes</button>
                        </form>
                    </div>
                )}
                {activeTab === 'users' && (
                    <div>
                        <h2>Add Developers</h2>
                        {/* This tab will be implemented later */}
                    </div>
                )}
            </div>
        </div>
    );
};

export default ProjectSettings;
