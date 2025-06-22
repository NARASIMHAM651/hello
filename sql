CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('student', 'recruiter', 'admin') NOT NULL,
    is_approved BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
----------------------------------------------------------------------------------------------------------------------------------
CREATE TABLE students (
    student_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    full_name VARCHAR(100),
    enrollment_number VARCHAR(50),
    branch VARCHAR(50),
    college VARCHAR(100),
    resume_path VARCHAR(255), -- Path to uploaded resume
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
------------------------------------------------------------------------------------------------------------------------------------
CREATE TABLE recruiters (
    recruiter_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    company_name VARCHAR(100),
    contact_person VARCHAR(100),
    phone VARCHAR(15),
    address TEXT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
--------------------------------------------------------------------------------------------------------------------------------------
CREATE TABLE jobs (
    job_id INT AUTO_INCREMENT PRIMARY KEY,
    recruiter_id INT NOT NULL,
    title VARCHAR(100),
    description TEXT,
    location VARCHAR(100),
    stipend DECIMAL(10,2) DEFAULT NULL,
    deadline DATE,
    status ENUM('pending', 'approved', 'rejected') DEFAULT 'pending',
    posted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (recruiter_id) REFERENCES recruiters(recruiter_id)
);
---------------------------------------------------------------------------------------------------------------------------------------------
CREATE TABLE applications (
    application_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    job_id INT NOT NULL,
    application_status ENUM('Applied', 'Shortlisted', 'Rejected', 'Hired') DEFAULT 'Applied',
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (job_id) REFERENCES jobs(job_id)
);
------------------------------------------------------------------------------------------------------------------------------------------------
CREATE TABLE admin_logs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    admin_id INT NOT NULL,
    action_type ENUM('approve_job', 'reject_job', 'block_user', 'unblock_user'),
    target_type ENUM('job', 'user'),
    target_id INT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (admin_id) REFERENCES users(id)
);
