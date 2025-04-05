-- Insert roles
INSERT INTO roles (name, description) VALUES
('ADMIN', 'Administrator with full access'),
('SUPPORT', 'Support staff for handling tickets'),
('USER', 'Regular user with limited access');

-- Insert departments
INSERT INTO departments (name, description) VALUES
('IT Support', 'Technical support for hardware and software issues'),
('Customer Service', 'General customer inquiries and assistance'),
('Billing', 'Payment and invoice related issues');

-- Insert categories
INSERT INTO categories (name, description) VALUES
('Hardware', 'Physical equipment issues'),
('Software', 'Application or system software problems'),
('Network', 'Connectivity and networking issues'),
('Account', 'User account related queries'),
('Payment', 'Payment processing issues');

-- DON'T insert admin user here, will be created by ApplicationStartup
-- This avoids BCrypt hash incompatibility issues
