-- =============================================================
-- Employee Management System - Database setup
-- Run this script in phpMyAdmin (XAMPP) or MySQL CLI.
-- =============================================================

CREATE DATABASE IF NOT EXISTS clg DEFAULT CHARACTER SET utf8mb4;
USE clg;

-- -----------------------------
-- Users table (login accounts)
-- -----------------------------
DROP TABLE IF EXISTS users;
CREATE TABLE users (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    password   VARCHAR(100) NOT NULL,
    role       ENUM('ADMIN','EMPLOYEE') NOT NULL,
    employee_id INT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- -----------------------------
-- Employees table
-- -----------------------------
DROP TABLE IF EXISTS employees;
CREATE TABLE employees (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    email      VARCHAR(100) NOT NULL UNIQUE,
    department VARCHAR(50)  NOT NULL,
    designation VARCHAR(60) NOT NULL,
    salary     DECIMAL(10,2) NOT NULL,
    phone      VARCHAR(20),
    address    VARCHAR(200),
    join_date  DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- -----------------------------
-- Seed data
-- Default admin -> username: admin   password: admin123
-- Default employee -> username: john   password: john123
-- -----------------------------
INSERT INTO employees (name, email, department, designation, salary, phone, address, join_date) VALUES
('John Doe',      'john@example.com',    'Engineering', 'Software Engineer', 65000.00, '9876543210', '12 MG Road, Pune',    '2022-04-10'),
('Priya Sharma',  'priya@example.com',   'HR',          'HR Manager',        72000.00, '9876501234', '45 Park Street, Mumbai','2021-08-15'),
('Rahul Verma',   'rahul@example.com',   'Finance',     'Accountant',        58000.00, '9123456780', 'Sector 21, Noida',    '2023-01-05'),
('Anita Kapoor',  'anita@example.com',   'Engineering', 'Senior Developer',  95000.00, '9988776655', 'Banjara Hills, Hyderabad','2020-06-20'),
('Vikram Singh',  'vikram@example.com',  'Marketing',   'Marketing Lead',    78000.00, '9000011111', 'Indiranagar, Bangalore','2022-11-11'),
('Sara Khan',     'sara@example.com',    'Engineering', 'QA Engineer',       52000.00, '9012345678', 'Salt Lake, Kolkata',  '2023-07-19'),
('Manoj Patel',   'manoj@example.com',   'Operations',  'Operations Head',   110000.00,'9090909090', 'Navrangpura, Ahmedabad','2019-03-25'),
('Neha Iyer',     'neha@example.com',    'Finance',     'Financial Analyst', 67000.00, '9876123450', 'T. Nagar, Chennai',   '2022-09-30'),
('Arjun Mehta',   'arjun@example.com',   'HR',          'HR Executive',      45000.00, '9009008007', 'Vashi, Navi Mumbai',  '2024-02-12'),
('Kavya Reddy',   'kavya@example.com',   'Marketing',   'Digital Marketer',  56000.00, '9911223344', 'Jubilee Hills, Hyderabad','2023-05-05'),
('Suresh Nair',   'suresh@example.com',  'Engineering', 'DevOps Engineer',   88000.00, '9445566778', 'Kochi, Kerala',       '2021-12-01'),
('Ritu Joshi',    'ritu@example.com',    'Operations',  'Operations Analyst',61000.00, '9776655443', 'Civil Lines, Jaipur', '2024-06-18');

INSERT INTO users (username, password, role, employee_id) VALUES
('admin', 'admin123', 'ADMIN',    NULL),
('john',  'john123',  'EMPLOYEE', 1),
('priya', 'priya123', 'EMPLOYEE', 2),
('rahul', 'rahul123', 'EMPLOYEE', 3);
