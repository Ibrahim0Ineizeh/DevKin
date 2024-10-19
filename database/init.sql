-- Create the database
CREATE DATABASE IF NOT EXISTS devkin_db;

-- Use the created database
USE devkin_db;

CREATE TABLE User (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fullname VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

-- Create the Project table
CREATE TABLE Project (
    project_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    size INT DEFAULT 0,
    url VARCHAR(500) NOT NULL,
    owner_id INT NOT NULL,
    last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_owner FOREIGN KEY (owner_id) REFERENCES Users(id) ON DELETE CASCADE
);

-- Create the Project_Developers table (many-to-many relationship)
CREATE TABLE Project_Developers (
    project_id INT NOT NULL,
    developer_id INT NOT NULL,
    role VARCHAR(50), 
    PRIMARY KEY (project_id, developer_id),
    CONSTRAINT fk_project FOREIGN KEY (project_id) REFERENCES Project(project_id) ON DELETE CASCADE,
    CONSTRAINT fk_developer FOREIGN KEY (developer_id) REFERENCES Users(id) ON DELETE CASCADE
);

-- Create the Project_History table to track changes/versions
CREATE TABLE Project_History (
    history_id INT AUTO_INCREMENT PRIMARY KEY,
    project_id INT NOT NULL,
    version INT NOT NULL AUTO_INCREMENT,
    changed_by INT NOT NULL,
    change_description TEXT,
    commit_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    snapshot_url VARCHAR(500) NOT NULL,
    CONSTRAINT fk_project_history FOREIGN KEY (project_id) REFERENCES Project(project_id) ON DELETE CASCADE,
    CONSTRAINT fk_changed_by FOREIGN KEY (changed_by) REFERENCES Users(id) ON DELETE CASCADE
);