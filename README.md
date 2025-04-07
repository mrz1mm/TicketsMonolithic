# Sistema di Gestione Ticket

![Logo del Sistema](src/main/resources/static/img/logo.png)

Un'applicazione web monolitica per la gestione efficiente dei ticket di supporto, costruita con Spring Boot. Questo sistema permette agli utenti di creare, assegnare e tracciare i ticket di supporto attraverso un'interfaccia web intuitiva.

## Caratteristiche Principali

- 🎫 Creazione e gestione dei ticket
- 👤 Gestione utenti con diversi ruoli (Admin, Support, User)
- 🏢 Organizzazione per dipartimenti
- 🔍 Tracciamento completo dello stato dei ticket
- 📊 Dashboard con statistiche e visualizzazioni
- 🔒 Sistema di autenticazione sicuro
- 📱 Interfaccia responsive

## Stack Tecnologico

- **Backend**: Java 17, Spring Boot 3.1.1
- **Frontend**: Thymeleaf, Bootstrap 5, Font Awesome
- **Database**: PostgreSQL 17
- **Persistenza**: Spring Data JPA, Hibernate
- **Sicurezza**: Spring Security
- **Migrazione Database**: Flyway
- **Build Tool**: Maven
- **Utilities**: Lombok, ModelMapper

## Prerequisiti

Per eseguire l'applicazione localmente, è necessario avere:

- JDK 17 o superiore
- Maven 3.8 o superiore
- PostgreSQL 12 o superiore
- Git

## Setup e Avvio

### 1. Clonare il repository

```bash
git clone https://github.com/tuonome/ticketsMonolithic.git
cd ticketsMonolithic
```

### 2. Configurare il database

Crea un database PostgreSQL:

```sql
CREATE DATABASE ticketing;
CREATE USER ticketuser WITH PASSWORD 'yourpassword';
GRANT ALL PRIVILEGES ON DATABASE ticketing TO ticketuser;
```

### 3. Configurare l'applicazione

Modifica il file `src/main/resources/application.properties` con le tue configurazioni:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/ticketing
spring.datasource.username=ticketuser
spring.datasource.password=yourpassword

# In alternativa, puoi utilizzare variabili d'ambiente
# DB_URL, DB_USERNAME, DB_PASSWORD
```

### 4. Eseguire l'applicazione

```bash
# Compilare l'applicazione
mvn clean package

# Avviare l'applicazione
mvn spring-boot:run

# In alternativa, puoi eseguire il JAR generato
java -jar target/ticketing-system-1.0.0.jar
```

L'applicazione sarà accessibile all'indirizzo: `http://localhost:8080`

Le migrazioni Flyway verranno eseguite automaticamente al primo avvio. Se desideri disattivare questo comportamento, modifica nel file `application.properties`:

```properties
spring.flyway.enabled=false
```

## Credenziali di Default

L'applicazione viene inizializzata con i seguenti utenti:

| Username | Password   | Ruolo   |
| -------- | ---------- | ------- |
| admin    | admin123   | ADMIN   |
| support  | support123 | SUPPORT |
| user     | user123    | USER    |

## Struttura del Progetto

src/main/java/com/ticketing/
├── config/ # Configurazioni Spring e sicurezza
├── controller/ # Controller REST e MVC
├── dto/ # Data Transfer Objects
├── exception/ # Classi di eccezione personalizzate
│ ├── AccessDeniedException.java
│ ├── BadRequestException.java
│ ├── GlobalExceptionHandler.java
│ └── ResourceNotFoundException.java
├── model/ # Entità JPA
├── repository/ # Repository Spring Data
├── service/ # Logica di business
└── TicketingApplication.java # Entry point dell'applicazione

src/main/resources/
├── application.properties # Configurazione base dell'applicazione
├── application-dev.properties # Configurazioni specifiche per sviluppo (non committare)
├── application-prod.properties # Configurazioni specifiche per produzione (non committare)
├── db/migration/ # Script di migrazione Flyway
├── static/ # Risorse statiche (CSS, JS, immagini)
│ ├── css/ # Fogli di stile
│ │ ├── components/ # Stili per componenti specifici
│ │ │ ├── tickets.css # Stili per i componenti dei ticket
│ │ │ ├── dashboard.css # Stili per componenti della dashboard
│ │ │ ├── forms.css # Stili per i form e input
│ │ │ └── tables.css # Stili per le tabelle
│ │ └── main.css # File CSS principale che importa i componenti
│ ├── js/ # Script JavaScript
│ │ ├── components/ # Componenti Alpine.js
│ │ │ ├── ticket-form.js # Logica per i form dei ticket
│ │ │ ├── ticket-filters.js # Logica per filtri dei ticket
│ │ │ ├── dashboard-stats.js # Widget statistiche dashboard
│ │ │ └── user-dropdown.js # Componente per menu utente
│ │ └── app.js # File JS principale che inizializza Alpine.js
│ └── img/ # Immagini e icone
├── templates/ # Template Thymeleaf
│ ├── error/ # Pagine di errore personalizzate
│ │ ├── 400.html # Bad Request
│ │ ├── 403.html # Access Denied
│ │ ├── 404.html # Not Found
│ │ └── 500.html # Internal Server Error

## Configurazione dell'ambiente

### Variabili d'ambiente

Per la sicurezza, le credenziali e altre configurazioni sensibili devono essere impostate come variabili d'ambiente. Ecco le principali variabili che l'applicazione utilizza:

```bash
# Database
export DB_URL=jdbc:postgresql://localhost:5432/ticketing
export DB_USERNAME=user
export DB_PASSWORD=password

# Profilo Spring
export SPRING_PROFILES_ACTIVE=dev  # o 'prod' per produzione

# Sicurezza
export REMEMBER_ME_KEY=chiaveSicuraRandomica
```

Per impostare un profilo specifico all'avvio:

```bash
# Avvio con profilo di sviluppo
java -jar -Dspring.profiles.active=dev ticketing-system-1.0.0.jar

# Avvio con profilo di produzione
java -jar -Dspring.profiles.active=prod ticketing-system-1.0.0.jar
```

## Ottimizzazione delle Performance

### Indici Database

L'applicazione crea automaticamente indici sulle colonne più utilizzate nelle query tramite le migrazioni Flyway, includendo:

- Indici sullo stato e priorità dei ticket per filtraggio rapido
- Indici sulle foreign keys per migliorare la velocità dei JOIN
- Indici sui campi di ricerca comuni come username ed email

### Query Ottimizzate

Per evitare il problema N+1 delle query, l'applicazione utilizza:

- EntityGraph per caricare le entità correlate in una singola query
- JOIN FETCH nelle query JPQL per ottimizzare il caricamento delle relazioni
- Query specifiche per ogni caso d'uso che caricano solo i dati necessari

## Autore

Simone Attanasio alias mr_z1m

## Licenza

Questo progetto è distribuito sotto licenza MIT. Vedere il file LICENSE per maggiori informazioni.
