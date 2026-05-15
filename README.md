# Brewly

A responsive full-stack web application designed for home baristas and specialty coffee enthusiasts to log brews, track inventory, share recipes, and improve brewing techniques through analytics and AI-powered assistance.

## 📊 Project Status

**Current Version**: Beta (v0.0.1-SNAPSHOT)  
**Last Updated**: March 2026  
**Build Status**: ✅ Backend compiles successfully, Frontend dependencies installable  
**Code Quality**: Good - Clean architecture, proper separation of concerns  
**Production Readiness**: Medium - Requires environment configuration optimization

### Key Assessment Points
- ✅ **Core Features Implemented**: Brew logging, recipe management, inventory tracking, AI assistant
- ✅ **Authentication Working**: JWT-based auth with proper security measures
- ✅ **Database Schema Complete**: All entities properly defined with relationships
- ✅ **API Documentation**: Comprehensive REST API endpoints documented
- ⚠️ **Configuration**: Currently uses hardcoded values in properties file (needs environment variables)
- ⚠️ **Testing**: Limited test coverage (needs improvement)
- 🔄 **Frontend State Management**: Basic Vue Composition API (could benefit from Pinia)

## Project Structure

```
brewly/
├── backend/          # Spring Boot REST API (Java 21)
│   ├── src/main/java/xyz/elwoodwjz/brewlybackend/
│   │   ├── controller/    # 7 REST controllers
│   │   ├── service/       # 5 business services
│   │   ├── repository/    # 6 data repositories
│   │   ├── entity/        # 8 JPA entities
│   │   ├── dto/           # Data transfer objects (6 packages)
│   │   ├── config/        # Configuration classes
│   │   ├── security/      # JWT authentication
│   │   └── exception/     # Custom exception handling
│   └── src/main/resources/application.properties
├── frontend/         # Vue.js 3 Single Page Application
│   ├── src/
│   │   ├── views/        # 12 page components
│   │   ├── components/   # 9 reusable components
│   │   ├── router/       # Routing configuration
│   │   ├── stores/       # State management (auth store)
│   │   ├── api/          # API clients (5 API modules)
│   │   └── assets/       # Static resources
│   └── package.json
└── README.md
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
- CORS API endpoints properly configured
- Input validation and global exception handling

## Tech Stack

### Backend
- **Framework**: Spring Boot 3.5.3 (Java 21)
- **Security**: Spring Security + JWT (jjwt 0.12.6)
- **Data Layer**: Spring Data JPA + PostgreSQL + Hibernate
- **API Integration**: Anthropic Claude 3.5 Haiku API (anthropic-java 1.0.0)
- **Tools**: Lombok 1.18.32, Jackson, Jakarta Validation
- **Reactive**: Spring WebFlux (for AI API calls)
- **Build**: Maven

### Frontend
- **Framework**: Vue 3.5.18 (Composition API)
- **Build Tool**: Vite 7.0.6
- **Routing**: Vue Router 4.5.1
- **UI Framework**: Bootstrap 5.3.7 + Bootstrap Icons 1.13.1
- **HTTP Client**: Axios 1.11.0
- **Development Tools**: Vue DevTools

### Database
- **Primary Database**: PostgreSQL
- **ORM**: Hibernate with automatic schema management
- **Data Types**: Support for JSONB fields for recipe step storage
- **Timezone**: UTC timezone configuration

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

## 🛠️ Build & Test

### Backend Build
```bash
cd backend
# Compile the project
./mvnw compile

# Run tests
./mvnw test

# Package the application
./mvnw package
```

### Frontend Build
```bash
cd frontend
# Install dependencies
npm install

# Development mode
npm run dev

# Production build
npm run build

# Preview production build
npm run preview
```

### Testing Status
- **Backend Tests**: Basic test class exists, needs expanded test coverage
- **Frontend Tests**: Currently lacks testing framework (consider adding Vitest)

## Architecture

### Backend Architecture

The backend follows a clean layered architecture pattern with RESTful API design. It implements a layered architecture consisting of `Entity, Repository, Service`, and `Controller`, ensuring separation and maintainability. 

The API uses proper HTTP methods and follows REST conventions for resource management. `Data transfer objects` (DTOs) are applied for API communication between the frontend and backend. 

Authentication is handled through `JWT tokens`, providing stateless authentication with validation for secure access. The application includes global exception handlers that provide custom error responses for error management.

`CORS` configuration is properly set up to enable cross-origin resource sharing for seamless frontend integration.

### Frontend Architecture

The frontend is built as a `Single Page Application (SPA)` using Vue.js with client-side routing provided by Vue Router. The application follows a component-based architecture with reusable Vue components built using the Composition API.

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

## ⚠️ Configuration Notes

### Current State
The project currently uses hardcoded configuration values in `backend/src/main/resources/application.properties`, including:
- JWT secret key
- Anthropic API key
- Database credentials

### Production Environment Recommendations
1. **Migrate to Environment Variables**:
   Update `application.properties` to use environment variable references:
   ```properties
   # Replace hardcoded values with environment variable references
   jwt.secret=${JWT_SECRET}
   anthropic.api.key=${ANTHROPIC_API_KEY}
   spring.datasource.password=${DATABASE_PASSWORD}
   ```

2. **Create .env File Example**:
   ```bash
   # .env.example
   JWT_SECRET=your-secure-jwt-secret-here
   ANTHROPIC_API_KEY=your-anthropic-api-key-here
   DATABASE_PASSWORD=your-database-password-here
   ```

3. **Environment Variables Required**:
   - `ANTHROPIC_API_KEY` - Anthropic Claude API key
   - `JWT_SECRET` - JWT signing secret
   - `DATABASE_URL` - PostgreSQL connection string (optional, can use separate properties)

## 🔧 Known Issues & Improvement Plan

### High Priority
1. **Hardcoded Configuration** - Migrate sensitive information to environment variables
2. **Git Status Anomaly** - package-lock.json file has conflicting staged/unstaged status
3. **Limited Test Coverage** - Need to add unit tests and integration tests

### Medium Priority
1. **Frontend State Management** - Consider introducing Pinia for more structured state management
2. **Error Handling Optimization** - Frontend error handling could be more user-friendly
3. **Performance Monitoring** - Add application performance monitoring and logging

### Low Priority
1. **Documentation Enhancement** - Add more detailed API documentation and usage examples
2. **CI/CD Pipeline** - Set up automated build and deployment pipeline
3. **Containerization** - Provide Docker configuration for easier deployment

## License & Contribution

This project is developed as a demonstration of full-stack development practices. Feel free to use it as a reference or starting point for your own projects.

### Getting Help
If you encounter issues or have questions about the project structure or implementation, please refer to the code comments and architecture documentation provided.

---

*Last Updated: March 2026*  
*Project Health: Good - Functional with some production optimizations needed*


## 📋 待实现功能Todo清单


### 第一阶段：Redis缓存层

- [ ] 添加`spring-boot-starter-data-redis`依赖到pom.xml
- [ ] 配置Redis连接参数到`application.properties`
- [ ] 创建Redis配置类
- [ ] 为热点查询接口添加缓存注解（如`@Cacheable`）
- [ ] 实现缓存更新/失效策略

### 第二阶段：定时任务 - 临期豆子预警

- [ ] 创建`ScheduledTaskService`类
- [ ] 使用`@Scheduled(cron = "0 0 9 * * *")`每天9点扫描
- [ ] 实现临期豆子检测逻辑（如烘焙日期+30天）
- [ ] 存储预警信息到数据库或Redis
- [ ] 在`HomeView.vue`首页添加预警展示模块

### 第三阶段：AI降级方案优化

- [ ] 改进`AIService`异常日志记录
- [ ] 添加详细的降级方案日志（记录API失败原因、响应码等）
- [ ] 实现异步AI处理（可选，简历提到异步）

### 第四阶段：最近使用参数模板功能

- [ ] 后端：创建API保存用户的最近冲煮参数
- [ ] 后端：创建`RecentBrewTemplate`实体和Repository
- [ ] 前端：`AddBrewView.vue`添加"使用最近参数"按钮
- [ ] 前端：实现表单自动填充逻辑
- [ ] 优化用户体验（模板选择、保存等）

### 第五阶段：性能优化指标

- [ ] 添加响应时间监控
- [ ] AI接口异步处理改造（使用`@Async`）
