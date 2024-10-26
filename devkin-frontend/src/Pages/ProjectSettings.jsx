import React, { useEffect, useState, useCallback } from 'react';
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
    const [language, setLanguage] = useState('Python');
    const [allUsers, setAllUsers] = useState([]);
    const [projectDevelopers, setProjectDevelopers] = useState([]);
    const [role, setRole] = useState('editor');
    const [availableUsers, setAvailableUsers] = useState([]);
    
    useEffect(() => {
        const loadData = async () => {
            await fetchProjectDetails();
            await fetchAvailableUsers();
            await fetchCurrentDevelopers();
        };
        loadData();
        
        return () => {
            localStorage.removeItem('projectInfo');
        };
    }, [slug]);

    const fetchProjectDetails = useCallback(async () => {
        try {
            const response = await fetch(`http://localhost:8080/dashboard/project/get?projectSlug=${slug}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('authToken')}`,
                },
            });
            const data = await response.json();
            setProject(data);
            setDescription(data.description);
            setLanguage(data.language || 'Python');
            setProjectDevelopers(data.developers || []);
            localStorage.setItem('projectInfo', JSON.stringify(data));
        } catch (error) {
            console.error('Failed to fetch project details', error);
        }
    }, [slug]);

    const fetchAvailableUsers = useCallback(async () => {
        try {
            const response = await fetch(`http://localhost:8080/users/notInProject?projectSlug=${slug}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('authToken')}`,
                },
            });
            
            const data = await response.json();
            console.log("Available Users:", data); // Log the data to check its structure
            
            if (data.message !== "No results found") {
                setAvailableUsers(data); // Ensure data is an array
            }
            else{
                setAvailableUsers([]);
            }
        } catch (error) {
            console.error('Failed to fetch available users', error);
        }
    }, [slug]);

    const fetchCurrentDevelopers = useCallback(async () => {
        try {
            const response = await fetch(`http://localhost:8080/dashboard/project/role/inProject?projectSlug=${slug}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('authToken')}`,
                },
            });
         
                const data = await response.json();
            if(data.message !== "No results found"){
                setProjectDevelopers(data);
            }
            else{
                setProjectDevelopers([]);
            }
           
        } catch (error) {
            console.error('Failed to fetch current developers', error);
        }
    }, [slug]);

    const handleBackClick = () => navigate(`/projects/${slug}`);

    const handleDeleteProject = async () => {
        if (window.confirm('Are you sure you want to delete this project? This action cannot be undone.')) {
            try {
                await fetch(`http://localhost:8080/dashboard/project/delete?projectSlug=${slug}`, {
                    method: 'DELETE',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${localStorage.getItem('authToken')}`,
                    },
                });
                navigate('/dashboard');
            } catch (error) {
                console.error('Failed to delete project', error);
            }
        }
    };

    const handleUpdateProject = async (e) => {
        e.preventDefault();
        try {
            await fetch(`http://localhost:8080/dashboard/project/update`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('authToken')}`,
                },
                body: JSON.stringify({ projectSlug: slug, description, language }),
            });
            alert('Project updated successfully!');
        } catch (error) {
            console.error('Failed to update project', error);
        }
    };

    const handleAddDeveloper = async (userEmail) => {
        try {
            const response = await fetch(`http://localhost:8080/dashboard/project/role/assign`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('authToken')}`,
                },
                body: JSON.stringify({ projectSlug: slug, userEmail, role }),
            });
    
            // Check if the response is okay (status in the range 200-299)
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
    
            // Fetch available users and current developers after the request completes
            await fetchAvailableUsers();
            await fetchCurrentDevelopers();
            alert('Developer added successfully!');
        } catch (error) {
            console.error('Failed to add developer', error);
        }
    };
    
    const handleRemoveDeveloper = async (developerEmail) => {
        try {
            const response = await fetch(`http://localhost:8080/dashboard/project/role/remove`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('authToken')}`,
                },
                body: JSON.stringify({ projectSlug: slug, userEmail: developerEmail }),
            });
    
            // Check if the response is okay (status in the range 200-299)
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
    
            // Fetch available users after the request completes
            await fetchAvailableUsers();
            await fetchCurrentDevelopers();
            alert('Developer removed successfully!');
        } catch (error) {
            console.error('Failed to remove developer', error);
        }
    };
    
    const handleRoleChange = async (developerEmail, newRole) => {
        try {
            const response = await fetch(`http://localhost:8080/dashboard/project/role/update`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${localStorage.getItem('authToken')}`,
                },
                body: JSON.stringify({ projectSlug: slug, userEmail: developerEmail, role: newRole }),
            });
    
            // Check if the response is okay (status in the range 200-299)
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
    
            // Fetch current developers after the request completes
            await fetchAvailableUsers();
            await fetchCurrentDevelopers();
        } catch (error) {
            console.error('Failed to update developer role', error);
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
                <button onClick={() => setActiveTab('description')} className={activeTab === 'description' ? 'active' : ''}>Update Project</button>
                <button onClick={() => setActiveTab('users')} className={activeTab === 'users' ? 'active' : ''}>Add Developers</button>
            </div>
            <div className="tab-content">
                {activeTab === 'description' && (
                    <div className='updateContainer'>
                        <h2>Update Project</h2>
                        <form onSubmit={handleUpdateProject}>
                            <label className='project-language-label'>
                                Language:
                                <select className='project-language-select' value={language} onChange={(e) => setLanguage(e.target.value)}>
                                    <option value="Python">Python</option>
                                    <option value="JavaScript">JavaScript</option>
                                </select>
                            </label>
                            <label className='project-description-label'>
                                Description:
                                <textarea className='project-description-textarea' value={description} onChange={(e) => setDescription(e.target.value)} rows="4" placeholder="Enter project description" />
                            </label>
                            <button className='project-update-button' type="submit">Save Changes</button>
                        </form>
                    </div>
                )}
                {activeTab === 'users' && (
                    <div className="developers-container">
                        <div className="add-developer-section">
                            <h2>Add Developers</h2>
                            <table className='developers-table'>
                                <thead>
                                    <tr><th>Name</th><th>Email</th><th>Action</th></tr>
                                </thead>
                                <tbody>
                                    {availableUsers.map(user => (
                                        <tr key={user.email}>
                                            <td>{user.name}</td>
                                            <td>{user.email}</td>
                                            <td><button onClick={() => handleAddDeveloper(user.email)}>Add</button></td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                            <label>Role:
                                <select className="custom-select" value={role} onChange={e => setRole(e.target.value)}>
                                    <option value="editor">Editor</option>
                                    <option value="viewer">Viewer</option>
                                </select>
                            </label>
                        </div>
                        <div className="current-developer-section">
                            <h2>Current Developers</h2>
                            <table className='developers-table'>
                                <thead><tr><th>Name</th><th>Email</th><th>Role</th><th>Action</th></tr></thead>
                                <tbody>
                                    {projectDevelopers.map(developer => (
                                        <tr key={developer.email}>
                                            <td>{developer.name}</td>
                                            <td>{developer.email}</td>
                                            <td>
                                                <select value={developer.role} onChange={e => handleRoleChange(developer.email, e.target.value)}>
                                                    <option value="editor">Editor</option>
                                                    <option value="viewer">Viewer</option>
                                                </select>
                                            </td>
                                            <td><button onClick={() => handleRemoveDeveloper(developer.email)}>Remove</button></td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
};

export default ProjectSettings;
