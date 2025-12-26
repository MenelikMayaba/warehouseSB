-- Clear existing data
TRUNCATE TABLE users RESTART IDENTITY CASCADE;

-- Insert admin user (password: admin123)
INSERT INTO users (username, password, role) VALUES
('admin', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'ADMIN');

-- Insert picker user (password: picker123)
INSERT INTO users (username, password, role) VALUES
('picker1', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'PICKER');

-- Insert packer user (password: packer123)
INSERT INTO users (username, password, role) VALUES
('packer1', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'PACKER');

-- Insert scanner admin user (password: scanneradmin123)
INSERT INTO users (username, password, role) VALUES
('scanneradmin1', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'SCANNER_ADMIN');

-- Insert warehouse manager user (password: warehousemanager123)
INSERT INTO users (username, password, role) VALUES
('warehousemanager1', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'WAREHOUSE_MANAGER');

-- Clear existing data
TRUNCATE TABLE users RESTART IDENTITY CASCADE;

-- Insert admin user (password: admin123)
INSERT INTO users (username, password, role) VALUES
('admin', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'ROLE_ADMIN');

-- Insert picker user (password: picker123)
INSERT INTO users (username, password, role) VALUES
('picker1', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'ROLE_PICKER');

-- Insert packer user (password: packer123)
INSERT INTO users (username, password, role) VALUES
('packer1', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'ROLE_PACKER');

-- Insert scanner admin user (password: scanneradmin123)
INSERT INTO users (username, password, role) VALUES
('scanneradmin1', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'ROLE_SCANNER_ADMIN');

-- Insert warehouse manager user (password: warehousemanager123)
INSERT INTO users (username, password, role) VALUES
('warehousemanager1', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a', 'ROLE_WAREHOUSE_MANAGER');