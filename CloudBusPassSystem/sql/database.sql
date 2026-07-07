-- ============================================================
-- Cloud-Based Bus Pass Management System
-- Database Schema
-- ============================================================

CREATE DATABASE IF NOT EXISTS bus_pass_system;
USE bus_pass_system;

-- ------------------------------------------------------------
-- Table: admin
-- ------------------------------------------------------------
CREATE TABLE admin (
    admin_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Default admin -> username: admin | password: Admin@123
-- (password below is a real bcrypt hash of "Admin@123", verified compatible with PHP password_verify())
INSERT INTO admin (username, password) VALUES
('admin', '$2b$10$Gejv435GsynDtB7PLM2aReBJyAO8OQU.JCAR3NSzWkP1XpoXkfB82');

-- ------------------------------------------------------------
-- Table: student
-- ------------------------------------------------------------
CREATE TABLE student (
    student_id VARCHAR(20) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(15) NOT NULL UNIQUE,
    college_name VARCHAR(150) NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_phone (phone)
);

-- ------------------------------------------------------------
-- Table: application
-- ------------------------------------------------------------
CREATE TABLE application (
    application_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id VARCHAR(20) NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    boarding_point VARCHAR(100) NOT NULL,
    destination VARCHAR(100) NOT NULL,
    distance_km DECIMAL(5,2) NOT NULL,
    pass_type ENUM('Monthly','Quarterly') NOT NULL,
    fare DECIMAL(8,2) NOT NULL,
    status ENUM('Pending','Approved','Rejected') DEFAULT 'Pending',
    applied_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE CASCADE,
    INDEX idx_status (status)
);

-- ------------------------------------------------------------
-- Table: buspass
-- ------------------------------------------------------------
CREATE TABLE buspass (
    pass_id INT AUTO_INCREMENT PRIMARY KEY,
    application_id INT NOT NULL UNIQUE,
    student_id VARCHAR(20) NOT NULL,
    route VARCHAR(200) NOT NULL,
    boarding_point VARCHAR(100) NOT NULL,
    destination VARCHAR(100) NOT NULL,
    pass_type ENUM('Monthly','Quarterly') NOT NULL,
    issue_date DATE NOT NULL,
    expiry_date DATE NOT NULL,
    status ENUM('Active','Expired','Cancelled') DEFAULT 'Active',
    qr_code_data VARCHAR(255) NOT NULL,
    FOREIGN KEY (application_id) REFERENCES application(application_id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE CASCADE,
    INDEX idx_pass_status (status)
);

-- ------------------------------------------------------------
-- Table: payment
-- ------------------------------------------------------------
CREATE TABLE payment (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    application_id INT NOT NULL,
    student_id VARCHAR(20) NOT NULL,
    amount DECIMAL(8,2) NOT NULL,
    payment_status ENUM('Paid','Pending') DEFAULT 'Pending',
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (application_id) REFERENCES application(application_id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES student(student_id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- View: reports (used by admin/reports.php for quick stats)
-- ------------------------------------------------------------
CREATE OR REPLACE VIEW report_summary AS
SELECT
  (SELECT COUNT(*) FROM application) AS total_applications,
  (SELECT COUNT(*) FROM application WHERE status='Approved') AS approved_passes,
  (SELECT COUNT(*) FROM application WHERE status='Rejected') AS rejected_passes,
  (SELECT COUNT(*) FROM buspass WHERE status='Active') AS active_passes,
  (SELECT COUNT(*) FROM buspass WHERE status='Expired') AS expired_passes,
  (SELECT COALESCE(SUM(amount),0) FROM payment WHERE payment_status='Paid') AS total_revenue;
