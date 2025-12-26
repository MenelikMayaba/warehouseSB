# Warehouse Management System (WMS)

A comprehensive warehouse management system built with Spring Boot, designed to streamline warehouse operations including inventory management, order processing, picking, packing, and dispatch. This system is tailored for efficient warehouse operations with real-time tracking and management capabilities.

## 🚀 Key Features

### 📦 Inventory Management
- **Real-time Stock Tracking**: Monitor current stock levels across all warehouse locations
- **Batch & Serial Number Tracking**: Track items by batch numbers and serial numbers for better traceability
- **Stock Movement History**: Detailed audit trail of all stock movements and transactions
- **Low Stock Alerts**: Automatic notifications for items below minimum stock levels
- **Multi-location Support**: Manage inventory across multiple warehouse zones and bins

### 📝 Order Management
- **Order Processing**: End-to-end order processing from creation to fulfillment
- **Order Prioritization**: Intelligent order prioritization based on shipping deadlines
- **Backorder Management**: Automatic handling of out-of-stock items with backorder creation
- **Order Status Tracking**: Real-time order status updates throughout the fulfillment process

### 👷 Picking Operations
- **Wave Picking**: Group orders for efficient picking
- **Pick Path Optimization**: Optimized pick paths to minimize travel time
- **Barcode Scanning**: Mobile-friendly interface with barcode scanning support
- **Pick Confirmation**: Digital confirmation of picked items with quantity validation

### 📦 Packing & Shipping
- **Packing Station**: Streamlined packing interface with order verification
- **Packing Slip Generation**: Automatic generation of packing slips
- **Shipping Label Integration**: Support for major shipping carriers
- **Weight Verification**: Automated weight verification for packed orders

### 📊 Reporting & Analytics
- **Inventory Reports**: Stock levels, turnover rates, and valuation
- **Order Fulfillment Metrics**: Picking accuracy, order cycle times
- **Performance Analytics**: Employee productivity and process efficiency
- **Custom Report Builder**: Create custom reports based on business needs

### 👥 User Management & Security
- **Role-based Access Control**:
  - **Admin**: Full system access, user management, configuration
  - **Warehouse Manager**: Oversee operations, manage inventory, view reports
  - **Picker**: View and complete picking tasks
  - **Packer**: Verify and pack orders
  - **Auditor**: View reports and audit trails
- **Activity Logging**: Comprehensive audit trail of all system activities
- **Secure Authentication**: Industry-standard security with Spring Security

## 🛠️ Tech Stack

- **Backend**: 
  - Java 21
  - Spring Boot 3.2.2
  - Spring Security
  - Spring Data JPA
  - Hibernate
  - Flyway (for database migrations)

- **Database**: 
  - PostgreSQL

- **Frontend**: 
  - Thymeleaf (server-side templates)
  - HTML5, CSS3, JavaScript

- **Build Tool**: 
  - Maven

- **Development Tools**:
  - Lombok
  - Spring Boot DevTools

## 📋 Prerequisites

- Java 21 or later
- Maven 3.6.0 or later
- PostgreSQL 12 or later
- Git (for version control)

## 🚀 Installation & Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/MenelikMayaba/warehouseSB.git
   cd warehouseSB
   ```

2. **Database Setup**
   - Create a new PostgreSQL database named `warehouse_db`
   - Update the database configuration in `src/main/resources/application.properties` with your database credentials

3. **Build the application**
   ```bash
   mvn clean install
   ```

4. **Run database migrations**
   ```bash
   mvn flyway:migrate
   ```

## 🏃 Running the Application

```bash
mvn spring-boot:run
```

The application will be available at: [http://localhost:8080](http://localhost:8080)

## 📂 Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/aCompany/warehouseSB/
│   │       ├── config/          # Configuration classes
│   │       ├── exception/       # Exception handling
│   │       ├── inventory/       # Inventory management
│   │       ├── invoice/         # Invoice processing
│   │       ├── order/           # Order management
│   │       ├── packing/         # Packing operations
│   │       └── picking/         # Picking operations
│   └── resources/
│       ├── static/              # Static resources
│       ├── templates/           # Thymeleaf templates
│       └── application.properties
└── test/                        # Test files
```

## 🔒 Default Users

- **Admin User**
  - Username: admin
  - Password: (Set in application properties)
 
## 🔄 Workflow

1. **Receiving**
   - Scan incoming items
   - Verify against purchase orders
   - Update inventory in real-time

2. **Storage**
   - Assign optimal storage locations
   - Update bin locations
   - Generate location labels

3. **Order Processing**
   - Orders are received and prioritized
   - Picking tasks are automatically generated
   - Pickers receive tasks on their dashboard

4. **Picking**
   - Pickers scan items and confirm quantities
   - System validates picks against orders
   - Items are moved to packing area

5. **Packing**
   - Packer verifies order contents
   - System suggests appropriate packaging
   - Shipping labels are generated

6. **Shipping**
   - Orders are marked as ready for dispatch
   - Shipping manifests are generated
   - Inventory is automatically updated

7. **Reporting**
   - Daily activity reports
   - Performance metrics
   - Inventory valuation

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📬 Contact

For any questions or feedback, please contact the project maintainers.
