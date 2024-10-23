import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import '../styles/ProjectPage.css';
import { useTheme } from '../Components/Theme';
import MonacoEditor from '@monaco-editor/react';

const FileExplorer = ({ files, onFileSelect }) => (
    <div>
        {files.map((file) => {
            // Create a Date object from the last modified string
            const lastModifiedDate = new Date(file.lastModefied);
            // Format the date to a more readable format
            const formattedDate = lastModifiedDate.toLocaleString('en-US', {
                year: 'numeric',
                month: 'long',
                day: 'numeric',
                hour: '2-digit',
                minute: '2-digit',
                second: '2-digit',
                hour12: true,
            });

            return (
                <div
                    key={file.fileId}
                    onClick={() => onFileSelect(file.fileName, file.filePath)}
                    style={{ cursor: 'pointer' }}
                >
                    {file.fileName}{"_"}<small><small><small>{formattedDate}</small></small></small>
                </div>
            );
        })}
    </div>
);

const ProjectPage = () => {
    const { slug } = useParams();
    const navigate = useNavigate();
    const { isDarkMode } = useTheme();
    const [project, setProject] = useState(null);
    const [projectStructure, setProjectStructure] = useState([]);
    const [selectedFile, setSelectedFile] = useState(null);
    const [fileContent, setFileContent] = useState('');
    const [output, setOutput] = useState('');
    const [showCreatePopUP, setShowPopup] = useState(false);
    const [newFilePath, setNewFilePath] = useState('');
    const [newFileType, setNewFileType] = useState('file');
    const [showDeletePopup, setShowDeletePopup] = useState(false);
    const [fileToDelete, setFileToDelete] = useState('');
    const [showRenamePopup, setShowRenamePopup] = useState(false);
    const [fileToRename, setFileToRename] = useState('');
    const [newFileName, setNewFileName] = useState('');

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
                return fetch(`http://localhost:8080/dashboard/project/structure?projectSlug=${slug}`, {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${localStorage.getItem('authToken')}`,
                    },
                });
            })
            .then((response) => {
                if (!response.ok) throw new Error('Failed to fetch project structure');
                return response.json();
            })
            .then((structure) => setProjectStructure(structure))
            .catch((err) => console.error(err.message));

        return () => {
            localStorage.removeItem('projectInfo');
        };
    }, [slug]);

    const handleSettings = () => {
        navigate(`/projects/${slug}/settings`);
    };

    const handleFileCreate = () => {
        setShowPopup(true);
    };

    const handleRenameFile = () => {
        setShowRenamePopup(true);
    };

    const handleDeleteFile = () => {
        setShowDeletePopup(true);
    };

    const handleRenameSubmit = async () => {
    try {
        const requestBody = {
            fileName: fileToRename,
            filePath: "", 
            newFileName: newFileName,
            projectSlug: slug,
        };

        const response = await fetch(`http://localhost:8080/dashboard/project/files/rename`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('authToken')}`,
            },
            body: JSON.stringify(requestBody),
        });

        if (!response.ok) {
            const errorMessage = await response.text();
            throw new Error(`Failed to rename file: ${errorMessage}`);
        }


        setFileToRename(''); // Clear current file name
        setNewFileName(''); // Clear new file name
        setShowRenamePopup(false); // Hide popup
        fetchProjectStructure();
    } catch (error) {
        console.error(error.message);
        alert(error.message);
    }
};

const handleDeleteSubmit = async () => {
    try {
        const requestBody = {
            fileName: fileToDelete,
            filePath: "", 
            newFileName: "",
            projectSlug: slug,
        };

        const response = await fetch(`http://localhost:8080/dashboard/project/files/delete`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('authToken')}`,
            },
            body: JSON.stringify(requestBody),
        });

        if (!response.ok) {
            const errorMessage = await response.text();
            throw new Error(`Failed to rename file: ${errorMessage}`);
        }
        setFileToDelete(''); // Clear new file name
        setShowDeletePopup(false); // Hide popup
        fetchProjectStructure();
    } catch (error) {
        console.error(error.message);
        alert(error.message);
    }
};

    const handleFileSelect = (fileName) => {
        fetch(`http://localhost:8080/dashboard/project/files/get-contents`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('authToken')}`,
            },
            body: JSON.stringify({ projectSlug: slug, fileName, newFileName: "", filePath: "" }),
        })
            .then((response) => {
                if (!response.ok) throw new Error('Failed to fetch file content');
                return response.text();
            })
            .then((content) => {
                setSelectedFile(fileName);
                setFileContent(content);
            })
            .catch((err) => console.error(err.message));
    };

    const handleSaveCode = () => {
        const formData = new FormData();
        const file = new File([fileContent], selectedFile, { type: "text/plain" });
        formData.append("file", file);
        formData.append("projectSlug", slug);
        formData.append("fileName", selectedFile);
        formData.append("newFileName", "");
        formData.append("filePath", "");

        fetch(`http://localhost:8080/dashboard/project/files/update-contents`, {
            method: 'PUT',
            headers: { 'Authorization': `Bearer ${localStorage.getItem('authToken')}` },
            body: formData,
        })
            .then((response) => {
                if (!response.ok) throw new Error('Failed to update file contents');
                return response.json();
            })
            .then(() => console.log('File contents updated successfully'))
            .catch((err) => console.error(err.message));
    };

    const handleExecuteCode = () => {
        setOutput('Code executed successfully.');
    };

    const handleClearOutput = () => {
        setOutput('');
    };

    const handleCreateSubmit = () => {
        const pathParts = newFilePath.split('/');
        const fileNameOrFolderName = pathParts.pop();
        const folderPath = pathParts.length ? pathParts.join('/') + '/' : '';
        const requestUrl = 'http://localhost:8080/dashboard/project/files/upload';
    
        const formData = new FormData();
        const file = new File([], fileNameOrFolderName);
        formData.append('file', file);
        formData.append('projectSlug', slug);
        formData.append('filePath', folderPath);
        formData.append('fileName', fileNameOrFolderName);
    
        fetch(requestUrl, {
            method: 'POST',
            headers: { 'Authorization': `Bearer ${localStorage.getItem('authToken')}` },
            body: formData,
        })
        .then((response) => {
            if (!response.ok) throw new Error('Failed to create file');
    
            // Check if the response is in JSON format
            const contentType = response.headers.get('content-type');
            if (contentType && contentType.includes('application/json')) {
                return response.json(); // Parse as JSON
            } else {
                return response.text(); // Handle plain text response
            }
        })
        .then((data) => {
            if (typeof data === 'string') {
                console.log(data); // Handle the plain text response
            } else {
                console.log('File created successfully'); // Handle JSON response if any
            }
            
            setNewFilePath(''); // Clear the input field
            setShowPopup(false); // Hide the popup
            fetchProjectStructure(); // Fetch the updated project structure
        })
        .catch((err) => console.error(err.message));
    };

    const fetchProjectStructure = () => {
        fetch(`http://localhost:8080/dashboard/project/structure?projectSlug=${slug}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${localStorage.getItem('authToken')}`,
            },
        })
        .then((response) => {
            if (!response.ok) throw new Error('Failed to fetch project structure');
            return response.json();
        })
        .then((structure) => {
            console.log('Project structure fetched successfully', structure);
            setProjectStructure(structure);
        })
        .catch((err) => console.error(err.message));
    };

    return (
        <div className={`project-page-container ${isDarkMode ? 'dark-mode' : 'light-mode'}`}>
            <div className="project-header">
                <h1>{project ? project.name : 'Loading project...'}</h1>
                <button className="settings-btn" onClick={handleSettings}>Settings</button>
            </div>
            <div className="columns-container">
                <div className="column file-explorer">
                    <h2>File Explorer</h2>
                    <button onClick={handleFileCreate}>New File</button>
                    <button onClick={handleRenameFile}>Rename</button>
                    <button onClick={handleDeleteFile}>Delete</button>
                    {projectStructure.length > 0 ? (
                        <FileExplorer files={projectStructure} onFileSelect={handleFileSelect} />
                    ) : (
                        <p>No Files Found</p>
                    )}
                </div>

                <div className="column code-editor">
                    <h2>Code Editor</h2>
                    {selectedFile ? (
                        <div> 
                            <div style={{ height: '70vh'}} className="codeEditor">  
                                <MonacoEditor
                                height="100%"
                                language="javascript"
                                theme={isDarkMode ? 'vs-dark' : 'light'}
                                value={fileContent}
                                onChange={(newValue) => setFileContent(newValue)}
                            /></div>
                          
                            <button onClick={handleSaveCode}>Save</button>
                            <button onClick={handleExecuteCode}>Execute</button>
                        </div>
                    ) : (
                        <p>Select a file to edit...</p>
                    )}
                </div>
                <div className="column output-console">
                    <h2>Output Console</h2>
                    <pre>{output}</pre>
                    <button onClick={handleClearOutput}>Clear Output</button>
                </div>
                {showCreatePopUP && (
                    <div className="popup-overlay">
                        <div className={`popup ${isDarkMode ? 'dark-mode' : 'light-mode'} show`}>
                            <h3>Create New File</h3>
                            <label htmlFor="filePath">Enter full path:</label>
                            <input
                                type="text"
                                id="filePath"
                                value={newFilePath}
                                onChange={(e) => setNewFilePath(e.target.value)}
                                placeholder="e.g., /src/newFile.js"
                            />
                            <button onClick={handleCreateSubmit}>Create</button>
                            <button onClick={() => setShowPopup(false)}>Cancel</button>
                        </div>
                    </div>
                )}

{showRenamePopup && (
    <div className="popup-overlay">
        <div className={`popup ${isDarkMode ? 'dark-mode' : 'light-mode'} show`}>
            <h3>Rename File</h3>
            <label htmlFor="filePath">Select current file:</label>
            <select
                value={fileToRename}
                onChange={(e) => setFileToRename(e.target.value)}
            >
                <option value="" disabled>Select a file</option>
                {projectStructure.map((file) => (
                    <option key={file.fileId} value={file.fileName}>{file.fileName}</option>
                ))}
            </select>
            <label htmlFor="newFileName">New file name:</label>
            <input
                type="text"
                placeholder="New file name..."
                value={newFileName}
                onChange={(e) => setNewFileName(e.target.value)}
            />
            <button onClick={handleRenameSubmit}>Rename</button>
            <button onClick={() => setShowRenamePopup(false)}>Cancel</button>
        </div>
    </div>
)}


{showDeletePopup && (
    <div className="popup-overlay">
        <div className={`popup ${isDarkMode ? 'dark-mode' : 'light-mode'} show`}>
            <h3>Delete File</h3>
            <label htmlFor="filePath">Select file to delete:</label>
            <select
                value={fileToDelete}
                onChange={(e) => setFileToDelete(e.target.value)}
            >
                <option value="" disabled>Select a file</option>
                {projectStructure.map((file) => (
                    <option key={file.fileId} value={file.fileName}>{file.fileName}</option> // Assuming fileId is unique
                ))}
            </select>
            <button onClick={handleDeleteSubmit}>Delete</button>
            <button onClick={() => setShowDeletePopup(false)}>Cancel</button>
        </div>
    </div>
)}

            </div>
        </div>
    );
};

export default ProjectPage;