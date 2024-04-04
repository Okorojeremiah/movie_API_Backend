# MovieAPI Backend App README

## Overview
This MovieAPI backend application provides CRUD (Create, Read, Update, Delete) functionality for managing movies. It integrates JWT (JSON Web Tokens) for authentication and authorization and uses Spring Security for securing endpoints. MySQL is used as the database, and JPA (Java Persistence API) is employed for ORM (Object-Relational Mapping).

## Features
- **CRUD Operations:** Allows users to Create, Read, Update, and Delete movie records.
- **JWT Authentication:** Secure authentication mechanism using JSON Web Tokens.
- **Spring Security:** Ensures endpoint security and access control.
- **MySQL Database:** Stores movie data persistently in a MySQL database.
- **JPA ORM:** Provides object-relational mapping for seamless interaction with the database.

## Technologies Used
- Java
- Spring Boot
- Spring Security
- JWT (JSON Web Tokens)
- MySQL
- JPA (Java Persistence API)

## Setup Instructions
1. Clone the repository: `git clone <repository-url>`
2. Navigate to the project directory: `cd movie-api-backend`
3. Update `application.properties` or `application.yml` with your MySQL database configurations.
4. Build the project: `./mvnw clean package` (for Maven wrapper) or `mvn clean package`
5. Run the application: `java -jar target/movie-api-backend.jar`

## Endpoints
- **POST /api/auth/signin:** Endpoint for user authentication and receiving JWT token.
- **POST /api/auth/signup:** Endpoint for user registration.
- **GET /api/movies:** Retrieve all movies.
- **GET /api/movies/{id}:** Retrieve a specific movie by ID.
- **POST /api/movies:** Create a new movie record.
- **PUT /api/movies/{id}:** Update an existing movie record.
- **DELETE /api/movies/{id}:** Delete a movie by ID.

## Security
- **JWT Token:** Authorization header with Bearer token is required for accessing protected endpoints.
- **Role-Based Access Control:** Admin and User roles are defined for different levels of access.
