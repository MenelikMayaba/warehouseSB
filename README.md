# Warehouse Management System

A comprehensive warehouse management system built with Spring Boot and Thymeleaf, designed to handle inventory management, stock tracking, and user administration.

## 🚀 Tech Stack

### Backend
- **Framework**: Spring Boot 3.2.2
- **Java Version**: 21
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA with Hibernate
- **Security**: Spring Security
- **Templating**: Thymeleaf
- **Build Tool**: Maven

### Frontend
- **UI Framework**: Bootstrap 5.3
- **Icons**: Bootstrap Icons
- **Client-side**: Vanilla JavaScript

## 🛠️ Features

### ✅ Implemented
- **User Authentication & Authorization**
  - Role-based access control (Admin/User)
  - Secure password hashing
  - Session management

- **Inventory Management**
  - Add/remove stock items
  - Adjust stock quantities
  - Track batch numbers
  - Manage stock locations

- **Admin Dashboard**
  - User management
  - System logs
  - Stock overview

### 🚧 In Progress
- **Logging System**
  - User activity tracking
  - System event logging
  - Log filtering and search

- **UI/UX Improvements**
  - Responsive design enhancements
  - Form validation
  - User feedback systems

## 📦 Prerequisites

- Java 21 or higher
- Maven 3.6.3 or higher
- PostgreSQL 13+
- Modern web browser (Chrome, Firefox, Edge)

## 🚀 Getting Started

### 1. Database Setup
```sql
CREATE DATABASE warehouse_db;
CREATE USER warehouse_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE warehouse_db TO warehouse_user;
