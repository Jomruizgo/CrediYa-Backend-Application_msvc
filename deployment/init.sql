-- Create loan_types table
CREATE TABLE loan_types (
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
CREATE TABLE loan_applications (
    id VARCHAR(36) PRIMARY KEY,
    identity_document VARCHAR(50) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    term_months INTEGER NOT NULL,
    loan_type_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (loan_type_id) REFERENCES loan_types(id)
);

-- Insert default loan types
INSERT INTO loan_types (name, description, min_amount, max_amount, min_term_months, max_term_months, interest_rate) VALUES
('PERSONAL', 'Personal loan for various purposes', 1000.00, 50000.00, 6, 60, 0.1200),
('MORTGAGE', 'Home mortgage loan', 50000.00, 500000.00, 60, 360, 0.0650),
('AUTO', 'Vehicle financing loan', 5000.00, 100000.00, 12, 84, 0.0850),
('EDUCATION', 'Student loan for education expenses', 1000.00, 100000.00, 12, 120, 0.0750),
('BUSINESS', 'Business loan for commercial purposes', 10000.00, 1000000.00, 6, 120, 0.0950);