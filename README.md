# Huza Backend

Spring Boot (Java 21) + PostgreSQL backend.

## Getting started

### 1. Clone

```bash
git clone <repository-url>
cd Huza-BE
```

### 2. Configure environment

Copy the example env file and adjust the values:

```bash
cp .env.example .env
```

### 3. Run with Docker Compose

```bash
docker compose up --build
```

The application starts on `http://localhost:${APP_PORT}` (default `8080`) and connects to the PostgreSQL container.
