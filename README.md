# Warehouse Management System

A comprehensive warehouse management system built with Spring Boot and Thymeleaf, designed to handle inventory management, stock tracking, messaging, and user administration.

## 🚀 Tech Stack

### Backend

* Spring Boot 3.2.2
* Java 21
* PostgreSQL
* Spring Data JPA (Hibernate)
* Spring Security
* Spring JMS (ActiveMQ)
* Thymeleaf
* Maven

### Frontend

* Bootstrap 5.3
* Bootstrap Icons
* Vanilla JavaScript

## 🛠️ Features

* User authentication and role-based authorization
* Inventory and stock management
* Admin dashboard and user management
* Messaging via ActiveMQ
* Thymeleaf-based server-rendered UI

## 📦 Prerequisites

* Java 21+
* Maven 3.6.3+
* PostgreSQL 13+
* ActiveMQ

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/MenelikMayaba/warehouseSB.git
cd warehouseSB
```

### 2. Database Setup

```sql
CREATE DATABASE warehouse_db;
CREATE USER warehouse_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE warehouse_db TO warehouse_user;
```

### 3. ActiveMQ Setup

* Download and install Apache ActiveMQ
* Start the broker
* Access the admin console at [http://localhost:8161/admin](http://localhost:8161/admin) (admin/admin)

### 4. Configure Application

Update `src/main/resources/application.properties`:

```properties
# Server Configuration
server.port=8080

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/warehouse_db
spring.datasource.username=warehouse_user
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# JMS / ActiveMQ Configuration
spring.activemq.broker-url=tcp://localhost:61616
spring.activemq.user=admin
spring.activemq.password=admin
spring.jms.pub-sub-domain=false

# Thymeleaf
spring.thymeleaf.cache=false
spring.thymeleaf.prefix=classpath:/templates/
spring.thymeleaf.suffix=.html

# Logging
logging.level.org.springframework=INFO
logging.level.com.aCompany.wms=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

### 5. Build and Run

```bash
mvn clean install
mvn spring-boot:run
```

Application available at [http://localhost:8080](http://localhost:8080)

### 6. Default Credentials

**Admin**

* [admin@example.com](mailto:admin@example.com) / admin123

**User**

* [user@example.com](mailto:user@example.com) / user123

## 🏗️ Project Structure

```text
src/
├── main/
│   ├── java/com/aCompany/wms/
│   │   ├── config/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── entity/
│   │   ├── exceptions/
│   │   ├── model/
│   │   ├── repository/
│   │   ├── service/
│   │   ├── util/
│   │   └── user/
│   └── resources/
│       ├── static/
│       ├── templates/
│       │   ├── admin/
│       │   ├── auth/
│       │   └── fragments/
│       └── application.properties
└── test/
```

## 🔧 Development

### Running Tests

```bash
mvn test
```

### Code Formatting

```bash
mvn formatter:format
```

### Database Migrations

Hibernate auto-DDL is enabled. Use Flyway or Liquibase for production.

## 🧪 Testing

* Unit tests for services and repositories
* Controller tests with `@WebMvcTest`
* Integration tests with `@SpringBootTest` and TestContainers

## 🔒 Security

* Form-based authentication
* JWT support
* CSRF protection
* BCrypt password hashing
* Role-based access control

## 📦 Dependencies

* Spring Boot Starters
* PostgreSQL JDBC Driver
* Lombok
* Hibernate Validator
* ActiveMQ
* JUnit 5, Mockito, TestContainers

## 🤝 Contributing

1. Fork the repo
2. Create a branch
3. Commit changes
4. Push and open a PR

## 📄 License

MIT License

## 🙏 Acknowledgments

Spring Boot, Thymeleaf, Bootstrap, PostgreSQL, ActiveMQ
