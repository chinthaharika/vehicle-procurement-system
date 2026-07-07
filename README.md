# Smart Vehicle Procurement System

A secure and transparent vehicle purchasing system built with Spring Boot and Java 17. This blockchain-inspired procurement system provides a robust platform for managing vehicle purchases with a focus on security, transparency, and data integrity.

## Overview

The Smart Vehicle Procurement System is a comprehensive solution designed to streamline the vehicle procurement process. It features a user-friendly web interface, secure data management, and support for multiple database backends.

## Features

- **Secure Procurement**: Transparent and auditable vehicle purchasing workflow
- **Multi-Database Support**: Compatible with H2, PostgreSQL, and MySQL
- **Spring Boot Framework**: Built on Spring Boot 3.2.1 for rapid development and scalability
- **Web Interface**: User-friendly web UI with Thymeleaf templating
- **Data Validation**: Built-in input validation and error handling
- **RESTful APIs**: Well-designed REST endpoints for programmatic access

## Project Structure

```
vehicle-procurement-system/
├── src/                          # Source code
├── data/                         # Data files and scripts
├── apache-maven-3.9.6/          # Maven build tool
├── pom.xml                      # Maven project configuration
├── database_setup.sql           # Database initialization script
├── run.ps1                      # PowerShell startup script
├── run_instructions.md          # Detailed run instructions
├── LICENSE                      # GNU General Public License v3.0
└── .gitignore                   # Git ignore rules
```

## Technology Stack

- **Java 17**: Modern Java version with latest features
- **Spring Boot 3.2.1**: Latest Spring Boot framework
- **Spring Data JPA**: Database access and ORM
- **Spring Web**: REST API support
- **Thymeleaf**: Server-side templating engine
- **Spring Validation**: Input validation framework
- **Database Options**:
  - H2 (In-memory/File-based)
  - PostgreSQL
  - MySQL

## Prerequisites

Before running the application, ensure you have:

1. **Java Development Kit (JDK) 17 or higher**
   - Download from [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) or use OpenJDK
   - Verify installation: `java -version`

2. **Maven 3.9.6** (or higher)
   - Included in the project repository
   - Or install from [Apache Maven](https://maven.apache.org/download.cgi)

3. **Database** (Choose one):
   - H2: No installation needed (embedded)
   - PostgreSQL: Install and configure locally
   - MySQL: Install and configure locally

## Installation & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/chinthaharika/vehicle-procurement-system.git
cd vehicle-procurement-system
```

### 2. Database Setup

For H2 (default, requires no setup):
- The application will use H2 embedded database by default

For PostgreSQL or MySQL:
1. Create a new database
2. Run the database setup script:
   ```sql
   -- Open database_setup.sql and execute in your database client
   ```
3. Update `application.properties` with your database credentials

### 3. Build the Project

Using the included Maven:
```bash
# Windows
.\run.ps1

# Linux/Mac
mvn clean install
```

Or manually:
```bash
mvn clean package
```

## Running the Application

### Option 1: Using PowerShell Script (Windows)

```powershell
# Navigate to project directory
cd "path\to\vehicle-procurement-system"

# Run the startup script
.\run.ps1
```

The script will:
- Kill any existing Java processes on port 8080
- Build the project using Maven
- Start the application
- Application will be available at `http://localhost:8080`

### Option 2: Manual Startup

```bash
# Build the project
mvn clean package

# Run the JAR file
java -jar target/smart-vehicle-procurement-0.0.1-SNAPSHOT.jar
```

### Option 3: Using Maven Spring Boot Plugin

```bash
mvn spring-boot:run
```

## Accessing the Application

Once running, access the application at:

- **Web Interface**: [http://localhost:8080](http://localhost:8080)
- **API Endpoints**: [http://localhost:8080/api](http://localhost:8080/api) (if configured)

## Configuration

Configure the application via `application.properties` or `application.yml`:

### Database Configuration

```properties
# H2 (Default)
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver

# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/vehicle_procurement
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.username=your_username
spring.datasource.password=your_password

# MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/vehicle_procurement
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### JPA Configuration

```properties
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQL10Dialect
```

## Troubleshooting

### Execution Policy Error (Windows)

If you encounter an error about scripts being disabled:

```powershell
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
```

### Port 8080 Already in Use

```bash
# Find and kill the process using port 8080
# Windows PowerShell
Stop-Process -Id (Get-NetTCPConnection -LocalPort 8080).OwningProcess -Force

# Linux/Mac
lsof -ti:8080 | xargs kill -9
```

### Database Connection Issues

- Ensure your database service is running
- Verify credentials in `application.properties`
- Check database URL format
- Ensure database exists and is accessible

### Build Failures

```bash
# Clean Maven cache
mvn clean

# Rebuild project
mvn clean install

# Check Java version
java -version
```

## Project Dependencies

| Dependency | Version | Purpose |
|-----------|---------|---------|
| Spring Boot | 3.2.1 | Framework foundation |
| Spring Data JPA | Latest | Database ORM |
| Spring Web | Latest | REST APIs |
| Thymeleaf | Latest | Template engine |
| H2 Database | Latest | Embedded database |
| PostgreSQL Driver | Latest | PostgreSQL support |
| MySQL Connector | Latest | MySQL support |

## API Documentation

The application provides RESTful endpoints for vehicle procurement operations. Documentation for specific endpoints will be available at runtime or in dedicated API documentation files.

## License

This project is licensed under the **GNU General Public License v3.0**. See the [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Support

For issues, questions, or suggestions, please create an issue on the [GitHub repository](https://github.com/chinthaharika/vehicle-procurement-system/issues).

## Project Information

- **Repository**: [chinthaharika/vehicle-procurement-system](https://github.com/chinthaharika/vehicle-procurement-system)
- **Primary Language**: Java
- **Build Tool**: Maven
- **Java Version**: 17+
- **Status**: Active Development

---

**Last Updated**: 2026  
**Version**: 0.0.1-SNAPSHOT
