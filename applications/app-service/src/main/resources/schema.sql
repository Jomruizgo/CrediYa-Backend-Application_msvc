-- ================================================
-- DATABASE SCHEMA - PRODUCTION READY
-- ================================================
-- This file contains only DDL (Data Definition Language) statements
-- No test data should be included here for production deployments

-- Create loan_types table
CREATE TABLE IF NOT EXISTS loan_types (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    min_amount DECIMAL(15,2) NOT NULL,
    max_amount DECIMAL(15,2) NOT NULL,
    min_term_months INTEGER NOT NULL,
    max_term_months INTEGER NOT NULL,
    interest_rate DECIMAL(5,4),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create loan_applications table
CREATE TABLE IF NOT EXISTS loan_applications (
    id VARCHAR(36) PRIMARY KEY,
    user_id BIGINT NOT NULL,
    identity_document VARCHAR(50) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    term_months INTEGER NOT NULL,
    loan_type_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (loan_type_id) REFERENCES loan_types(id)
);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_loan_applications_user_id ON loan_applications(user_id);
CREATE INDEX IF NOT EXISTS idx_loan_applications_identity_document ON loan_applications(identity_document);
CREATE INDEX IF NOT EXISTS idx_loan_applications_status ON loan_applications(status);
CREATE INDEX IF NOT EXISTS idx_loan_applications_created_at ON loan_applications(created_at);

-- Insert essential loan types (production data)
INSERT INTO loan_types (name, description, min_amount, max_amount, min_term_months, max_term_months, interest_rate) 
VALUES 
('PERSONAL', 'Personal loan for various purposes', 1000.00, 50000.00, 6, 60, 0.1200),
('MORTGAGE', 'Home mortgage loan', 50000.00, 500000.00, 60, 360, 0.0650),
('AUTO', 'Vehicle financing loan', 5000.00, 100000.00, 12, 84, 0.0850),
('EDUCATION', 'Student loan for education expenses', 1000.00, 100000.00, 12, 120, 0.0750),
('BUSINESS', 'Business loan for commercial purposes', 10000.00, 1000000.00, 6, 120, 0.0950)
ON CONFLICT (name) DO NOTHING;