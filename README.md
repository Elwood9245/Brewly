# Brewly

A responsive full-stack web application designed for home baristas and specialty coffee enthusiasts to log brews, track inventory, share recipes, and improve brewing techniques through analytics and AI.

## Project Structure

```
brewly/
├── backend/          # Spring Boot REST API
├── frontend/         # Vue.js SPA
├── docs/            # Documentation
└── README.md        # This file
```

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

### Backend
- **Spring Boot 3.5.3** - Main framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Data access layer
- **PostgreSQL** - Database
- **JWT** - authentication
- **Jakarta Validation** - Input validation
- **Lombok** - Code simplification

### Frontend
- **Vue.js 3** - Progressive framework
- **Vite** - Build tool
- **Vue Router** - Routing
- **Pinia** - State management
- **Axios** - HTTP client
- **Tailwind CSS** - Styling

## Development

### Backend Setup
```bash
cd backend
./mvnw spring-boot:run
```

### Frontend Setup
```bash
cd frontend
npm install
npm run dev
```

## Architecture
- **RESTful API** - Backend API design
- **SPA** - Single Page Application frontend
- **JWT Authentication** - Stateless authentication
- **DTO Pattern** - Data transfer objects for API communication
- **Layered Architecture** - Entity, Repository, Service, Controller