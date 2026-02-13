-- ============================================================
-- SDPMS - Student Digital Process Monitoring System
-- Complete MySQL Database Schema
-- ============================================================
-- Run this script to create the database and all tables.
-- Alternatively, Spring Boot can create tables automatically (ddl-auto=update).
-- ============================================================

CREATE DATABASE IF NOT EXISTS sdpms
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE sdpms;

-- ------------------------------------------------------------
-- Table: users (base for Admin, Faculty, Student)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(255) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  role VARCHAR(50) NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  last_login_at DATETIME(6) NULL,
  created_at DATETIME(6) NULL,
  CONSTRAINT chk_role CHECK (role IN ('ADMIN', 'FACULTY', 'STUDENT'))
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- Table: departments
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS departments (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  code VARCHAR(10) NULL
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- Table: faculty (extends users)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS faculty (
  user_id BIGINT NOT NULL PRIMARY KEY,
  full_name VARCHAR(255) NULL,
  email VARCHAR(255) NULL,
  phone VARCHAR(50) NULL,
  employee_id VARCHAR(20) NULL,
  department_id BIGINT NULL,
  CONSTRAINT fk_faculty_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_faculty_department FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- Table: students (extends users)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS students (
  user_id BIGINT NOT NULL PRIMARY KEY,
  full_name VARCHAR(255) NULL,
  email VARCHAR(255) NULL,
  phone VARCHAR(50) NULL,
  roll_number VARCHAR(50) NULL,
  current_semester INT NULL DEFAULT 1,
  academic_year VARCHAR(20) NULL,
  department_id BIGINT NULL,
  CONSTRAINT fk_student_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_student_department FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- Table: subjects
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS subjects (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  code VARCHAR(20) NULL,
  semester INT NULL,
  credits INT NULL,
  department_id BIGINT NULL,
  CONSTRAINT fk_subject_department FOREIGN KEY (department_id) REFERENCES departments(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- Table: attendance
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS attendance (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  student_id BIGINT NOT NULL,
  subject_id BIGINT NOT NULL,
  attendance_date DATE NOT NULL,
  present BOOLEAN NOT NULL DEFAULT FALSE,
  remarks VARCHAR(500) NULL,
  CONSTRAINT fk_attendance_student FOREIGN KEY (student_id) REFERENCES students(user_id) ON DELETE CASCADE,
  CONSTRAINT fk_attendance_subject FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE,
  UNIQUE KEY uq_attendance (student_id, subject_id, attendance_date)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- Table: student_marks
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS student_marks (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  student_id BIGINT NOT NULL,
  subject_id BIGINT NOT NULL,
  semester INT NULL,
  exam_type VARCHAR(50) NULL,
  internal_marks DECIMAL(5,2) NULL,
  semester_marks DECIMAL(5,2) NULL,
  total_marks DECIMAL(5,2) NULL,
  grade VARCHAR(10) NULL,
  grade_point DECIMAL(3,2) NULL,
  CONSTRAINT fk_marks_student FOREIGN KEY (student_id) REFERENCES students(user_id) ON DELETE CASCADE,
  CONSTRAINT fk_marks_subject FOREIGN KEY (subject_id) REFERENCES subjects(id) ON DELETE CASCADE,
  UNIQUE KEY uq_marks (student_id, subject_id, semester, exam_type)
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- Table: student_alerts
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS student_alerts (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  student_id BIGINT NOT NULL,
  type VARCHAR(50) NULL,
  title VARCHAR(255) NULL,
  message VARCHAR(1000) NULL,
  read_status BOOLEAN NOT NULL DEFAULT FALSE,
  created_at DATETIME(6) NULL,
  CONSTRAINT fk_alert_student FOREIGN KEY (student_id) REFERENCES students(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ------------------------------------------------------------
-- Indexes for common queries
-- ------------------------------------------------------------
CREATE INDEX idx_attendance_student_date ON attendance(student_id, attendance_date);
CREATE INDEX idx_attendance_subject_date ON attendance(subject_id, attendance_date);
CREATE INDEX idx_marks_student_semester ON student_marks(student_id, semester);
CREATE INDEX idx_marks_subject_semester ON student_marks(subject_id, semester);
CREATE INDEX idx_alerts_student ON student_alerts(student_id);
