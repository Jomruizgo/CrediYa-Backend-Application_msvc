-- ================================================
-- APPLICATION MICROSERVICE - DEVELOPMENT DATA
-- ================================================
-- This file contains only test data for development
-- Assumes tables are already created by schema.sql

-- Loan types are inserted in schema.sql as production data

-- Insert loan applications with different statuses
INSERT INTO loan_applications (id, user_id, identity_document, amount, term_months, loan_type_id, status, created_at) VALUES
-- Préstamos APROBADOS
('approved-001', 1, '1234567890', 25000.00, 24, 1, 'APPROVED', '2024-01-15 10:30:00'),
('approved-002', 2, '9876543210', 15000.00, 18, 1, 'APPROVED', '2024-01-20 14:45:00'),
('approved-003', 3, '5678901234', 35000.00, 36, 1, 'APPROVED', '2024-02-01 09:15:00'),
('approved-004', 4, '1122334455', 45000.00, 48, 1, 'APPROVED', '2024-02-10 16:20:00'),
('approved-005', 5, '2233445566', 75000.00, 60, 2, 'APPROVED', '2024-02-15 11:30:00'),

-- Préstamos PENDIENTES DE REVISIÓN (PENDING_REVIEW)
('pending-001', 6, '3344556677', 20000.00, 24, 1, 'PENDING_REVIEW', '2024-03-01 08:30:00'),
('pending-002', 7, '4455667788', 30000.00, 36, 1, 'PENDING_REVIEW', '2024-03-05 13:45:00'),
('pending-003', 8, '5566778899', 18000.00, 18, 1, 'PENDING_REVIEW', '2024-03-10 10:15:00'),
('pending-004', 9, '6677889900', 60000.00, 72, 2, 'PENDING_REVIEW', '2024-03-12 15:30:00'),
('pending-005', 10, '7788990011', 28000.00, 30, 1, 'PENDING_REVIEW', '2024-03-15 09:45:00'),
('pending-006', 11, '8899001122', 95000.00, 84, 2, 'PENDING_REVIEW', '2024-03-18 14:20:00'),
('pending-007', 12, '9900112233', 22000.00, 24, 1, 'PENDING_REVIEW', '2024-03-20 11:10:00'),

-- Préstamos EN EVALUACIÓN (IN_EVALUATION)
('eval-001', 13, '0011223344', 40000.00, 42, 1, 'IN_EVALUATION', '2024-03-22 16:00:00'),

-- Préstamos DESEMBOLSADOS (DISBURSED)
('disbursed-001', 1, '1234567890', 12000.00, 12, 1, 'DISBURSED', '2023-12-01 10:00:00'),
('disbursed-002', 2, '9876543210', 8000.00, 12, 1, 'DISBURSED', '2023-11-15 14:30:00'),
-- Usuarios que tienen pendientes TAMBIÉN tienen algunos aprobados/desembolsados
('disbursed-003', 6, '3344556677', 15000.00, 18, 1, 'DISBURSED', '2023-10-20 11:30:00'),
('disbursed-004', 7, '4455667788', 22000.00, 24, 1, 'DISBURSED', '2023-09-15 14:45:00'),
('disbursed-005', 10, '7788990011', 18000.00, 12, 1, 'DISBURSED', '2024-01-10 09:20:00'),

-- Préstamos APROBADOS adicionales (para usuarios que también tienen pendientes)
('approved-006', 8, '5566778899', 12000.00, 12, 1, 'APPROVED', '2024-02-20 10:15:00'),
('approved-007', 9, '6677889900', 25000.00, 36, 1, 'APPROVED', '2024-02-25 15:30:00'),
('approved-008', 11, '8899001122', 35000.00, 48, 1, 'APPROVED', '2024-03-01 12:45:00'),

-- Préstamos PAGADOS (PAID) - Préstamos históricos completamente finalizados
('paid-001', 3, '5678901234', 15000.00, 24, 1, 'PAID', '2023-08-15 09:30:00'),
('paid-002', 1, '1234567890', 8000.00, 12, 1, 'PAID', '2023-06-20 16:45:00'),
('paid-003', 2, '9876543210', 20000.00, 18, 1, 'PAID', '2023-05-10 14:20:00'),
('paid-004', 4, '1122334455', 10000.00, 12, 1, 'PAID', '2023-04-25 11:15:00'),
-- Usuarios con pendientes también tienen historial de pagados
('paid-005', 6, '3344556677', 8000.00, 12, 1, 'PAID', '2023-03-15 16:20:00'),
('paid-006', 10, '7788990011', 10000.00, 12, 1, 'PAID', '2023-02-28 13:10:00'),
('paid-007', 12, '9900112233', 6000.00, 6, 1, 'PAID', '2023-07-12 10:30:00');