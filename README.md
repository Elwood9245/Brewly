# Brewly

A responsive full-stack web application designed for home baristas and specialty coffee enthusiasts to log brews, track inventory, share recipes, and improve brewing techniques through analytics and AI. This repository is used for the back-end.

## Features

### Core Features
- **Brew Journal**: Log brews with parameters (bean, method, grind size, ratio, time, temperature, taste notes)
- **Recipe Sharing**: Create and share brewing recipes with instructions
- **Inventory Management**: Track coffee beans, filters, grinder burr wear, water filters
- **Brew Timer Assistant**: Customisable step timer for different brew methods
- **Data Visualisation**: Charts showing brew frequency, bean usage, flavour correlations
- **AI Assistant**: Retrieval-Augmented Generation (RAG) for personalised brewing advice
- **Social Features**: Follow users, comment, like, and interact with the community

### Authentication
- Local registration and login (username/email + password)
- Third-party login (Google, Apple) - Coming Soon
- JWT-based authentication

## Tech Stack

- **Spring Boot 3.5.3** - Main framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Data access layer
- **PostgreSQL** - Database
- **JWT** - authentication
- **Jakarta Validation** - Input validation
- **Lombok** - Code simplification

## Architecture
- **RESTful API** - Backend API design
- **DTO Pattern** - Data transfer objects for API communication
- **Layered Architecture** - Entity, Repository, Service, Controller

## Project Structure

```
src/main/java/xyz/elwoodwjz/brewlybackend/
├── entity/
│   ├── User.java
│   ├── Bean.java
│   ├── Recipe.java
│   └── ...
├── repository/
│   └── UserRepository.java
├── service/
│   └── AuthService.java
├── controller/
│   └── AuthController.java
├── dto/
│   ├── auth/
│   │   ├── RegisterRequest.java
│   │   ├── LoginRequest.java
│   │   └── AuthResponse.java
│   └── user/
│       └── UserResponse.java
└── config/
```