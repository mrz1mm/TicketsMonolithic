# Convenzioni di Codice

Questo documento definisce le convenzioni di codice da seguire in tutto il progetto.

## Formattazione

- Utilizzare 4 spazi per l'indentazione (non tab)
- Limite di 120 caratteri per riga
- Utilizzare la codifica UTF-8 per tutti i file sorgente
- Terminare ogni file con una nuova riga
- Evitare spazi bianchi alla fine delle righe

## Naming

### Classi

- Utilizzare PascalCase (es. `UserService`, `TicketController`)
- Nomi descrittivi che riflettano lo scopo della classe
- Evitare abbreviazioni ambigue

### Metodi

- Utilizzare camelCase (es. `getUserById`, `createTicket`)
- Verbi che descrivono l'azione (get, create, update, delete, find)
- Nomi descrittivi che spieghino cosa fa il metodo

### Variabili

- Utilizzare camelCase (es. `userId`, `ticketList`)
- Nomi significativi che descrivano il contenuto
- Evitare nomi di una sola lettera eccetto per variabili di loop o lambda temporanee

### Costanti

- Utilizzare UPPER_SNAKE_CASE (es. `MAX_RETRY_COUNT`, `DEFAULT_PAGE_SIZE`)

## Organizzazione del Codice

### Package

- Organizzare le classi in package logici basati sulla funzionalità o sul pattern architettonico
- Utilizzare nomi in minuscolo per i package (es. `com.ticketing.service`)

### Classi

- Una classe per file
- Organizzare i membri della classe in un ordine logico:
  1. Campi statici
  2. Campi di istanza
  3. Costruttori
  4. Metodi pubblici
  5. Metodi protetti
  6. Metodi privati
  7. Classi interne

## Documentazione

### Javadoc

- Tutti i metodi pubblici e protected devono avere Javadoc
- Includere una breve descrizione di ciò che fa il metodo
- Documentare tutti i parametri con `@param`
- Documentare il valore di ritorno con `@return`
- Documentare le eccezioni lanciate con `@throws`
- Utilizzare `{@link}` per riferimenti a altre classi o metodi

### Commenti Inline

- Utilizzare commenti inline per spiegare logica complessa o non ovvia
- Evitare commenti che ripetono ciò che il codice già esprime chiaramente
- Utilizzare `//` per commenti su una singola riga
- Utilizzare `/* */` per commenti multi-riga

## Pattern e Pratiche

### DTO

- Utilizzare sempre DTO per trasferire dati tra controller e view/service
- Non esporre mai direttamente le entità JPA nelle viste o nelle API
- I DTO dovrebbero contenere solo i dati necessari per il caso d'uso specifico

### Validazioni

- Utilizzare Bean Validation per validare i dati di input
- Implementare validazioni personalizzate quando necessario

### Gestione Eccezioni

- Utilizzare eccezioni specifiche per diversi tipi di errori
- Gestire le eccezioni al livello appropriato
- Utilizzare il GlobalExceptionHandler per gestire centralmente le eccezioni

### Logging

- Utilizzare SLF4J per il logging
- Utilizzare livelli di logging appropriati:
  - ERROR: errori che impediscono il funzionamento
  - WARN: situazioni anomale ma gestibili
  - INFO: eventi significativi durante l'esecuzione normale
  - DEBUG: informazioni dettagliate per il debug
  - TRACE: informazioni estremamente dettagliate
