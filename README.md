# BlackERP

A modern, open-source ERP framework based on the Compiere/iDempiere Application Dictionary model, reimagined with contemporary technologies.

## Technology Stack

- Kotlin
- Spring Boot 3.2.0
- PostgreSQL
- Docker
- Gradle

## Project Structure

```
blackerp/
├── src/
│   ├── main/
│   │   ├── kotlin/
│   │   │   └── org/
│   │   │       └── blackerp/
│   │   │           ├── domain/     # Domain entities
│   │   │           ├── repository/ # Data access
│   │   │           ├── service/    # Business logic
│   │   │           └── web/        # REST controllers
│   │   └── resources/
│   └── test/
│       └── kotlin/
```

## Getting Started

### Prerequisites

- JDK 17
- Docker
- Docker Compose
- Gradle

### Development Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/red1oon/blackerp.git
   ```

2. Start the database:
   ```bash
   docker-compose up -d
   ```

3. Build the project:
   ```bash
   ./gradlew clean build
   ```

4. Run the application:
   ```bash
   ./gradlew bootRun
   ```

## License

This project is licensed under the MIT License - see the LICENSE file for details.
