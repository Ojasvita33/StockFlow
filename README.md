# 📦 StockFlow – Inventory Management System

StockFlow is a microservices-based Inventory Management System developed using Spring Boot. It is designed to efficiently manage product inventory and user authentication in a scalable and modular architecture.

---

## 🚀 Project Overview

StockFlow helps businesses manage their stock, products, and users in a structured way. The system is divided into independent services, making it scalable, maintainable, and easy to extend.

This project follows **Microservices Architecture**, where each service is responsible for a specific functionality.

---

## 🧩 Microservices in the Project

### 1️⃣ Auth Service (Port: 8081)
- Handles user authentication and authorization
- Manages user login and registration
- Connects with MySQL database

### 2️⃣ Product Service (Port: 8080)
- Manages product-related operations
- Add, update, delete, and fetch products
- Maintains inventory data

---

## 🛠️ Technologies Used

- Java (Spring Boot)
- Spring Data JPA (Hibernate)
- MySQL Database
- Maven
- REST APIs
- Microservices Architecture

---

## 🔑 Key Features
- Microservices architecture  
- Separate Auth & Product services  
- REST APIs with MySQL  
- Scalable and modular design  
- Hibernate auto table creation  

---

## 🔒 Data Security
- Authentication via Auth Service  
- Secure DB configuration  
- Service-level data isolation  

---

## 🔄 API Communication
- REST-based communication  
- Independent services on different ports  
- Easy scaling and deployment  

---
## ⚙️ Application Configuration

### 🔐 Auth Service (`application.properties`)
spring.application.name=authService
server.port=8081

spring.datasource.url=jdbc:mysql://127.0.0.1:3306/stockflow
spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=pass

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true


---

### 📦 Product Service (`application.properties`)
spring.application.name=productService
server.port=8080

spring.datasource.url=jdbc:mysql://127.0.0.1:3306/stockflow
spring.datasource.driverClassName=com.mysql.cj.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=pass

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

---


## ▶️ How to Run the Project

### Step 1: Clone Repository
git clone <https://github.com/Ojasvita33/StockFlow>

### Step 2: Setup Database
- Create MySQL database: CREATE DATABASE stockflow; 
- Run the 'database\stockflow.sql' commands in mySQL.

### Step 3: Run Services
Run both services separately:

👉 Auth Service:
cd authService
mvn spring-boot:run

👉 Product Service:
cd productService
mvn spring-boot:run


---

## 🌐 API Endpoints (Example)

### Auth Service
- POST `/register`
- POST `/login`

### Product Service
- GET `/products`
- POST `/products`
- PUT `/products/{id}`
- DELETE `/products/{id}`

---

## 📈 Future Enhancements

- 🔹 API Gateway integration    
- 🔹 Frontend (React / Angular)  
- 🔹 Docker Deployment  
