## 📦 StockFlow - Inventory Management System

A **microservices-based inventory management system** built with Java 17 and Spring Boot 3.x, featuring **JWT authentication**, **RESTful APIs**, and a modern web interface. Perfect for learning Java development patterns and industry best practices.

-----

## ✨ Key Features

  * 🔐 **JWT Authentication**: Secure user registration and login system with Spring Security and BCrypt.
  * 🏗️ **Microservices Architecture**: Separate **AuthService** (Port 8081) and **ProductService** (Port 8080).
  * 📦 **Product Management**: Complete **CRUD** operations for inventory items via RESTful APIs.
  * 🗄️ **JPA/Hibernate**: Object-relational mapping with **MySQL** database.
  * 📊 **Real-time Analytics**: Inventory value calculations and statistics.

-----

## 🏗️ System Architecture

The system uses a two-microservice design:

1.  **AuthService**: Handles user authentication, registration, and **JWT token** generation.
2.  **ProductService**: Manages all **product inventory** data and statistics.

### Microservices Design

```
┌─────────────────┐      ┌─────────────────┐
│   AuthService   │      │ ProductService  │
│   (Port 8081)   │      │   (Port 8080)   │
└─────────┬───────┘      └─────────┬───────┘
          └──────────┬─────────────┘
                     │
           ┌───────▼────────┐
           │  MySQL Database │
           └────────────────┘
```

-----

## 🚀 Quick Start

### Prerequisites

  - **Java 17+**
  - **MySQL 8.0+**
  - **Maven 3.6+**

### Setup & Installation

1.  Clone the repository: `git clone <repository-url>`
2.  Create a MySQL database named **`stockflow`**.
3.  Update the database connection details in **`application.properties`** for both `authService/` and `productService/`.
4.  Start **AuthService** on port **8081** (e.g., `./mvnw spring-boot:run` in `authService/`).
5.  Start **ProductService** on port **8080** (e.g., `./mvnw spring-boot:run` in `productService/`).
6.  Access the application's login page at: **`http://localhost:8081`**

-----

## 📚 API Documentation

| Service | Method | Endpoint | Description | Security |
|---------|--------|----------|-------------|----------|
| **Auth** | `POST` | `/auth/register` | Register new user | Public |
| **Auth** | `POST` | `/auth/login` | User authentication, returns **JWT token** | Public |
| **Product**| `GET` | `/api/products` | Get all inventory products | Requires **JWT** |
| **Product**| `POST` | `/api/products` | Create a new product | Requires **JWT** |
| **Product**| `GET` | `/api/products/{id}` | Get product by ID | Requires **JWT** |
| **Product**| `GET` | `/api/products/stats/total-value` | Get inventory value | Requires **JWT** |

-----

## 📁 Project Structure

  * **`authService/`**: Authentication microservice (User models, Security configs, JWT utils).
  * **`productService/`**: Product management microservice (Product models, Controllers, Services).

-----

[![Java](https://img.shields.io/badge/Java-17+-orange?style=flat&logo=openjdk)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.12-green?style=flat&logo=springboot)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue?style=flat&logo=mysql)](https://www.mysql.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

-----
