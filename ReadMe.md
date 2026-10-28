# RentFit Backend

Backend REST API for RentFit, a rental platform built using Spring Boot and PostgreSQL.

## Tech Stack

- Java
- Spring Boot
- Spring Data JPA
- Spring Security
- PostgreSQL
- Maven
- JWT Authentication

## Prerequisites

- Java JDK installed
- PostgreSQL installed and running
- Maven installed, or use the included Maven Wrapper

## Setup and Run

### 1. Clone the repository

```bash
git clone <your-repository-url>
cd <repository-folder>
```

### 2. Create the database

Create a PostgreSQL database named `rentfit`:

```sql
CREATE DATABASE rentfit;
```

### 3. Configure environment variables

Create a `.env` file in the project root:

```dotenv
DB_URL=jdbc:postgresql://localhost:5432/rentfit
DB_USERNAME=postgres
DB_PASSWORD=your_postgres_password

JWT_SECRET=your_secure_jwt_secret

# Email Configuration
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_gmail_app_password
```


**Email setup:**
1. Enable 2-Step Verification on your Google Account.
2. Generate an App Password in your Google Account security settings.
3. Set `MAIL_USERNAME` to your Gmail address.
4. Set `MAIL_PASSWORD` to the generated App Password, not your regular Gmail password.
Update the values according to your local environment.



**Note:** Spring Boot does not automatically load `.env` files. Configure these variables in your IDE or terminal, or use a dotenv library.

If email functionality is enabled, configure `MAIL_USERNAME` and `MAIL_PASSWORD` as required by the application.

### 4. Run the application

Using Maven:

```bash
mvn spring-boot:run
```

Or, on Windows, using the Maven Wrapper:

```bash
mvnw.cmd spring-boot:run
```

The backend runs at:

`http://localhost:8080`

## Configuration

The application uses `application.yml` for database, JPA, JWT, and server configuration.

- Database: `rentfit`
- Server port: `8080`
- Hibernate DDL mode: `update`
- JWT expiration: 1 hour

## Security

- Never commit `.env` files or credentials to GitHub.
- Use a strong, randomly generated JWT secret.
- Keep production credentials in environment variables or a secrets manager.
