# Brewly

A responsive full-stack web application designed for home baristas and specialty coffee enthusiasts to log brews, track inventory, share recipes, and improve brewing techniques through analytics and AI-powered assistance.

## Project Structure

```
brewly/
├── backend/          # Spring Boot REST API
├── frontend/         # Vue.js SPA
└── README.md        # This file
```

## Features

### Core Features
- Log brews with parameters (bean, method, grind size, ratio, time, temperature, taste notes, ratings)
- Create, edit, and share brewing recipes with step-by-step instructions stored as JSON
- Track coffee beans with origin, roast level, weight, consumption, and flavour profiles
- Claude 3.5 Haiku-powered chat interface for personalised brewing advice and coffee knowledge
- Visual analytics showing brew frequency, method preferences, ratings, and trends
- Comment on recipes, like favourites, and bookmark community recipes
- Responsive interface optimised for mobile devices

### Authentication & Security
- JWT-based stateless authentication
- Local registration and login (username/email + password)
- CORS API endpoints

## Tech Stack

### Backend
- Spring Boot 3.5.3
- Spring Security
- Spring Data JPA
- JWT
- Jakarta Validation
- Lombok
- Jackson
- Anthropic Claude API

### Frontend
- Vue 3
- Vite 7.0.6
- Vue Router 4
- Bootstrap 5.3.7
- Bootstrap Icons
- Axios - HTTP client for API communication

### Database
- PostgreSQL
- Hibernate - ORM with automatic schema management

## Development Setup

### Prerequisites
- Java 21+
- Node.js 20.19.0+ or 22.12.0+
- PostgreSQL 12+
- Maven 3.6+

### Backend Setup
1. Create PostgreSQL database:
   ```sql
   CREATE DATABASE brewly;
   CREATE USER brewly_admin WITH PASSWORD 'brewly123';
   GRANT ALL PRIVILEGES ON DATABASE brewly TO brewly_admin;
   ```

2. Configure application properties in `backend/src/main/resources/application.properties`

3. Run the backend:
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```
   Backend will be available at `http://localhost:8080`

### Frontend Setup
1. Install dependencies:
   ```bash
   cd frontend
   npm install
   ```

2. Start development server:
   ```bash
   npm run dev
   ```
   Frontend will be available at `http://localhost:5173`


## Architecture

### Backend Architecture

The backend follows a clean layered architecture pattern with RESTful API design. It implements a layered architecture consisting of `Entity, Repository, Service`, and `Controller`, ensuring separation and maintainability. 

The API uses proper HTTP methods and follows REST conventions for resource management. `Data transfer objects` (DTOs) are applied for API communication between the frontend and backend. 

Authentication is handled through `JWT tokens`, providing stateless authentication with  validation for secure access. The application includes global exception handlers that provide custom error responses for error management.

`CORS` configuration is properly set up to enable cross-origin resource sharing for seamless frontend integration.

### Frontend Architecture

The frontend is built as a `Single Page Application (SPA)` using Vue.js with client-side routing provided by Vue Router. The application follows a component-based architecture with reusable Vue components built using the Composition API

API integration is implemented using Axios with request and response interceptors for automatic authentication token handling and error management. The user interface is designed with a mobile-first approach using Bootstrap 5, ensuring responsive design across all device types and screen sizes.

### Database Schema

| Table | Description |
|-------|-------------|
| **Users** | User accounts with authentication |
| **BrewLogs** | Individual brew records with parameters and ratings |
| **Recipes** | Brewing recipes with JSONB step storage |
| **Beans** | Coffee bean inventory with detailed attributes |
| **Comments** | Recipe comments and social interaction |
| **Likes** | Recipe likes and favourites |

## API Endpoints

### Authentication

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | User registration |
| POST | `/api/auth/login` | User login |
| GET | `/api/auth/me` | Get current user |

### Brew Logs

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/brewlogs` | Get user's brew logs (with pagination) |
| POST | `/api/brewlogs` | Create new brew log |
| GET | `/api/brewlogs/{id}` | Get specific brew log |
| PUT | `/api/brewlogs/{id}` | Update brew log |
| DELETE | `/api/brewlogs/{id}` | Delete brew log |
| GET | `/api/brewlogs/statistics` | Get brew log statistics |

### Recipes

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/recipes/user` | Get user's recipes (with pagination) |
| GET | `/api/recipes/my-recipes` | Get current user's recipes (with pagination) |
| POST | `/api/recipes` | Create new recipe |
| GET | `/api/recipes/{id}` | Get specific recipe |
| PUT | `/api/recipes/{id}` | Update recipe |
| DELETE | `/api/recipes/{id}` | Delete recipe |
| GET | `/api/recipes/public` | Get public recipes (with pagination) |
| GET | `/api/recipes/public/search` | Search public recipes by keyword |
| GET | `/api/recipes/public/method/{method}` | Get public recipes by brewing method |
| GET | `/api/recipes/bookmarked` | Get user's bookmarked recipes |
| GET | `/api/recipes/{id}/bookmark-status` | Check if recipe is bookmarked |

### Recipe Social Features

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/recipes/{id}/likes` | Like a recipe |
| DELETE | `/api/recipes/{id}/likes` | Unlike a recipe |
| POST | `/api/recipes/{id}/comments` | Add comment to recipe |
| GET | `/api/recipes/{id}/comments` | Get recipe comments (with pagination) |
| DELETE | `/api/recipes/comments/{commentId}` | Delete comment |
| POST | `/api/recipes/{id}/bookmark` | Bookmark a recipe |
| DELETE | `/api/recipes/{id}/bookmark` | Unbookmark a recipe |

### Beans

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/beans` | Get all beans |
| GET | `/api/beans/user` | Get user's beans |
| POST | `/api/beans` | Add new bean |
| GET | `/api/beans/{id}` | Get specific bean |
| PUT | `/api/beans/{id}` | Update bean |
| DELETE | `/api/beans/{id}` | Delete bean |

### AI Assistant

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/ai/chat` | Chat with AI assistant |

## Configuration

### Environment Variables
- `ANTHROPIC_API_KEY` - Anthropic Claude API key
- `JWT_SECRET` - JWT signing secret
- `DATABASE_URL` - PostgreSQL connection string
