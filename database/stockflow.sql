CREATE DATABASE IF NOT EXISTS stockflow
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE stockflow;


-- ======================================================
-- COMPANIES
-- ======================================================

CREATE TABLE IF NOT EXISTS companies (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    name VARCHAR(200) NOT NULL,

    email VARCHAR(200) UNIQUE NOT NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP

);


-- ======================================================
-- USERS (Company / Admin / User)
-- ======================================================

CREATE TABLE IF NOT EXISTS users (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    username VARCHAR(100) UNIQUE NOT NULL,

    email VARCHAR(200) UNIQUE NOT NULL,

    password VARCHAR(255) NOT NULL,

    role ENUM('ROLE_COMPANY','ROLE_ADMIN','ROLE_USER') NOT NULL,

    company_id BIGINT,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_users_company
    FOREIGN KEY (company_id)
    REFERENCES companies(id)
    ON DELETE CASCADE

);


-- ======================================================
-- PRODUCTS
-- ======================================================

CREATE TABLE IF NOT EXISTS products (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    name VARCHAR(200) NOT NULL,

    category VARCHAR(100),

    quantity INT NOT NULL DEFAULT 0,

    price_per_unit DOUBLE NOT NULL DEFAULT 0,

    company_id BIGINT NOT NULL,

    created_by BIGINT,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_products_company
    FOREIGN KEY (company_id)
    REFERENCES companies(id)
    ON DELETE CASCADE,

    CONSTRAINT fk_products_user
    FOREIGN KEY (created_by)
    REFERENCES users(id)
    ON DELETE SET NULL

);


-- ======================================================
-- INDEXES (performance)
-- ======================================================

CREATE INDEX idx_users_company
ON users(company_id);

CREATE INDEX idx_products_company
ON products(company_id);

use stockflow;
SELECT id, username, company_id FROM users;
SELECT id, name FROM companies;
