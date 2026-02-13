-- ============================================================
-- SDPMS - Optional Seed Data
-- ============================================================
-- Run schema.sql first. Then run this to add sample data.
-- Admin user is created automatically by the Spring Boot app on first run (admin / admin123).
-- This script adds one department and optional sample faculty/student for testing.
-- ============================================================

USE sdpms;

-- Ensure we have at least one department (if not already created by app)
INSERT IGNORE INTO departments (id, name, code) VALUES (1, 'Computer Science', 'CS');
INSERT IGNORE INTO departments (id, name, code) VALUES (2, 'Information Technology', 'IT');
INSERT IGNORE INTO departments (id, name, code) VALUES (3, 'Electronics', 'ECE');

-- Sample subjects for department 1 (Computer Science), semester 1
INSERT IGNORE INTO subjects (name, code, semester, credits, department_id) VALUES
('Data Structures', 'CS101', 1, 4, 1),
('Programming in C', 'CS102', 1, 3, 1),
('Discrete Mathematics', 'CS103', 1, 3, 1);

-- No admin/faculty/student inserted here: passwords must be BCrypt-hashed.
-- Use the application (Admin login) to create Faculty and Students, or run
-- the Spring Boot app once to get default admin (admin / admin123).
