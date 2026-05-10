# Asset Track Management System

This project is a full-stack application containing a Spring Boot backend and a React (Vite) frontend.

## Prerequisites

- **Java 21**
- **Maven**
- **Node.js 18+** & **npm**
- **PostgreSQL** (if you want to run the database locally)

## PostgreSQL Setup & Environment Variables

By default, the backend is configured in `application.properties` to connect to a **remote Supabase PostgreSQL instance**. 

If you prefer to run the database locally or override any configurations, you can use the following environment variables:

- `SPRING_DATASOURCE_URL` (e.g., `jdbc:postgresql://localhost:5432/assettrack`)
- `SPRING_DATASOURCE_USERNAME` (e.g., `postgres`)
- `SPRING_DATASOURCE_PASSWORD` (e.g., `password`)
- `APP_JWT_SECRET` (Must be at least 32 characters for HS256)
- `SPRING_MAIL_HOST` (e.g., `localhost` for MailHog or `smtp.mailtrap.io` for Mailtrap)
- `SPRING_MAIL_PORT`
- `SPRING_MAIL_USERNAME`
- `SPRING_MAIL_PASSWORD`

*Note: Hibernate is configured with `spring.jpa.hibernate.ddl-auto=update` by default, so tables will be created automatically upon startup.*

## Running the Backend Locally

1. Navigate to the `backend` directory:
   ```bash
   cd backend
   ```
2. You can build and run the application using the Maven wrapper:
   ```bash
   ./mvnw spring-boot:run
   ```
   *(Alternatively, run `mvn spring-boot:run` if you have Maven installed globally).*

The backend API will start on `http://localhost:8080`.

## Running the Frontend Locally

1. Navigate to the frontend workspace:
   ```bash
   cd frontend/asset-track-management-system
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Start the Vite development server:
   ```bash
   npm run dev
   ```

The frontend will typically start on `http://localhost:5173`.

## Default Credentials for Testing

Since the application connects to a shared remote Supabase database by default, it may already contain populated data. 

If you are running against a **fresh local database**, no users are created by default. To test the system:

1. **Create a Developer User**:
   Use the Signup endpoint (`POST /api/v1/auth/signup`) with the following JSON:
   ```json
   {
     "email": "user@example.com",
     "password": "securepassword123",
     "firstName": "John",
     "lastName": "Doe"
   }
   ```
   *Note: New signups are assigned the `DEVELOPER` role by default.*

2. **Create an Admin User**:
   Admin privileges are required for most asset management endpoints. After signing up a user, you must manually promote them to an Admin directly in your database:
   ```sql
   UPDATE users SET role = 'ADMIN' WHERE email = 'user@example.com';
   ```

If using the remote database, you can try logging in with the example credentials from the API spec:
- **Email:** `admin@example.com`
- **Password:** `securepassword123`
