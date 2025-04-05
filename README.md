# Ticket Management System

A complete system for managing support tickets built with Spring Boot.

## Setup Instructions

1. Make sure PostgreSQL is installed and running on localhost:5432
2. Create the database:

   ```bash
   psql -U postgres -c "CREATE DATABASE ticketing;"
   ```

   Or let the application create it for you automatically.

3. If you encounter Flyway migration issues, you can:

   - Run the application with automatic repair:
     ```
     --spring.flyway.repair-on-migrate=true
     ```
   - Run the application with the repair profile:
     ```
     --spring.profiles.active=repair-flyway
     ```
   - Or manually reset the database:
     ```bash
     psql -U postgres -c "DROP DATABASE ticketing;"
     psql -U postgres -c "CREATE DATABASE ticketing;"
     ```

4. Default admin credentials:
   - Username: admin
   - Password: admin123

## Database Migrations

Spring Boot esegue automaticamente le migrazioni Flyway all'avvio dell'applicazione.

### Comandi Maven per la gestione manuale delle migrazioni

Se hai bisogno di gestire manualmente le migrazioni Flyway, puoi usare questi comandi:

- Visualizzare lo stato delle migrazioni:

  ```bash
  ./mvnw flyway:info
  ```

- Eseguire manualmente le migrazioni:

  ```bash
  ./mvnw flyway:migrate
  ```

- Riparare la metadata table di Flyway in caso di errori:

  ```bash
  ./mvnw flyway:repair
  ```

- Cancellare il database e tutti i suoi oggetti:
  ```bash
  ./mvnw flyway:clean
  ```
  ⚠️ ATTENZIONE: questo comando elimina tutti i dati!

### Opzioni di avvio dell'applicazione

Per controllare il comportamento di Flyway all'avvio:

- Disabilitare le migrazioni automatiche:

  ```
  --spring.flyway.enabled=false
  ```

- Riparare automaticamente in caso di problemi:

  ```
  --spring.flyway.repair-on-migrate=true
  ```

- Iniziare le migrazioni da una versione specifica:
  ```
  --spring.flyway.baseline-on-migrate=true
  --spring.flyway.baseline-version=1
  ```

## Technologies

- Spring Boot 3.2.0
- Spring Security
- PostgreSQL
- Flyway for database migrations
- Thymeleaf templates with Bootstrap 5
- Alpine.js for interactivity

## Features

- User authentication and role-based access control
- Ticket management with categories and departments
- Ticket assignment and status tracking
- Attachments support
- Real-time notifications using WebSockets
- Responsive design with Bootstrap 5
