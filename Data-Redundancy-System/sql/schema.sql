-- ================================================================
-- DATA REDUNDANCY REMOVAL AND VALIDATION SYSTEM
-- MySQL Database Schema
-- ================================================================

CREATE DATABASE IF NOT EXISTS data_redundancy_db;
USE data_redundancy_db;

CREATE TABLE IF NOT EXISTS admin (
    admin_id    INT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO admin (username, password)
VALUES ('admin', 'admin123')
ON DUPLICATE KEY UPDATE username = username;

CREATE TABLE IF NOT EXISTS records (
    record_id    VARCHAR(20)  NOT NULL,
    name         VARCHAR(100) NOT NULL,
    email        VARCHAR(150) NOT NULL,
    phone        VARCHAR(15)  NOT NULL,
    department   VARCHAR(100),
    address      TEXT,
    status       ENUM('UNIQUE','DUPLICATE','POTENTIAL_DUPLICATE','FALSE_POSITIVE') DEFAULT 'UNIQUE',
    date_created DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (record_id),
    UNIQUE KEY uk_email (email),
    UNIQUE KEY uk_phone (phone),
    INDEX idx_name (name),
    INDEX idx_status (status)
);

CREATE TABLE IF NOT EXISTS validation_log (
    log_id            INT AUTO_INCREMENT PRIMARY KEY,
    record_id         VARCHAR(20),
    field_name        VARCHAR(50),
    validation_result ENUM('PASS','FAIL') NOT NULL,
    message           VARCHAR(255),
    checked_at        DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS duplicate_log (
    duplicate_id     INT AUTO_INCREMENT PRIMARY KEY,
    record_id        VARCHAR(20),
    matched_with     VARCHAR(20),
    reason           VARCHAR(255),
    similarity_score DECIMAL(5,2),
    detected_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (matched_with) REFERENCES records(record_id) ON DELETE SET NULL
);
