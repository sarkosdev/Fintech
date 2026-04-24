# Simple API & Wallet

Simple API & Wallet is a fintech application that allows users to **register, authenticate, perform transactions between wallets, and deposit money in to your own wallet within the system**.

The project is built using **Spring Boot** for backend and follows modern backend development practices, including **secure authentication, database versioning, caching, and event-driven communication** and **Angular** for frontend.

---

# Project Overview

This application simulates a simplified **digital wallet system**, where users can:

- Register an Account
- Authenticate securely
- Deposit money in to your own wallet
- Transfer money between accounts
- Retrieve transaction history

The system integrates several technologies commonly used in **real-world backend systems**, such as **mysql for persistante data**,  **Redis for caching**, **Kafka for messaging**, and **Liquibase for database versioning**.

---

# Technologies Used

## Core
- Java 11
- Spring Boot 2.7.15
- Maven

## Backend Frameworks
- Spring Web
- Spring Security
- Spring Data JPA
- Hibernate Validator

## Database
- MySQL
- Liquibase

## Messaging
- Apache Kafka

## Caching
- Redis

## Authentication
- JWT (JSON Web Tokens)

## Development Tools
- Lombok
- Jackson

## Infrastructure
- Docker
- Docker Compose

---

# Project Architecture

The project follows a **layered architecture**.

```
Controller
   ↓
Service
   ↓
Repository
   ↓
Database
```

### Components

**Controllers**
- Handle HTTP requests and responses.

**Services**
- Contain business logic.

**Repositories**
- Communicate with the MySQL database using Spring Data JPA.

**Entities**
- Represent database tables.

**DTOs**
- Used for transferring data between layers.

---

# Features

### User Management
- User registration
- Email confirmation
- Secure login
- Password encryption

### Account Management
- Automatic wallet creation for users
- Account balance management
- Deposit balance

### Transactions
- Transfer money between accounts
- Retrieve transaction history
- Timestamped transactions

### Security
- JWT authentication
- Protected endpoints
- BCrypt password hashing

### Database Versioning
- Liquibase manages schema changes

### Caching
- Redis used to cache frequently accessed data

### Messaging
- Kafka used for event-driven communication between services

---

# Prerequisites

Before running the project, install the following:

### Java Development Kit

Install **JDK 11**

https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html

---

### IDE

Recommended IDE:

**IntelliJ IDEA**

https://www.jetbrains.com/idea/

---

### Docker

Install:

- Docker Desktop
- Docker Compose

https://www.docker.com/products/docker-desktop/

---

# Installation

### 1. Clone the repository

```bash
git clone https://github.com/your-username/simple-api-wallet.git
cd Simple API & Wallet
```

---

### 2. Start Infrastructure Services

Run the Docker Compose file inside project directory:
Use *-d* flag in order to free the console windows without the logs, or delete it if you want to check all logs while running
Use *--build* in order to allways re-build our *docker-compose-wallet-app.yaml*, specially when we make changes to it


```bash
docker compose -f docker-compose-wallet-app.yaml up -d --build
```

This will start:

- MySQL database
- Redis cache
- Kafka broker
- Zookeeper
- Kafka UI
- Adminer database interface

# Infrastructure Services

The project uses Docker to run required infrastructure.

| Service | Description | Port |
|------|------|------|
| MySQL | Main application database | 3306 |
| Adminer | Web interface for MySQL | 8085 |
| Redis | Caching layer | 6379 |
| Kafka | Event messaging system | 9092 |
| Kafka UI | Kafka monitoring interface | 8083 |

---

### 3. Build the project

```bash
mvn clean install
```

Instead you chould just build and run the application in an IDEA like Intellij

---

### 4. Run the application - backend (outside IDE)

```bash
mvn spring-boot:run
```

Or run the jar:

```bash
java -jar target/fintech-0.0.1-SNAPSHOT.jar
```

The API will start at:

```
http://localhost:8080
```

---

### 5. Run the application - frontend 

```bash
cd Simple API & Wallet - Frontend
ng serve
```

Frontend application will start at:

```
http://localhost:4200
```

After you got both applications running (backend and frontend) you will be able to run the application as a whole, performing all kind of operations using UI made with angular and primeng library

# MySQL Database

The application stores data in **MySQL**.

Connection configuration:

```
Host: localhost
Port: 3306
Database: fintech-db
Username: user
Password: user
```

Root credentials:

```
Username: root
Password: root
```

---

# Adminer (Database Web Interface)

Adminer allows easy database management with an UI very friendly.

Open your browser at:

```
http://localhost:8085
```

Login configuration:

```
System: MySQL / MariaDB
Server: mysql
Username: root
Password: root
Database: fintech-db
```

You can allways connect directly to your mysql docker container where the database is located and running if you prefer to manager you database using CLI. 

Open a terminal and execute the following command and followed by the root password which is also root

```
docker exec -it mysql-db-wallet mysql -u root -p
```



---

# Redis Configuration

Redis is used to cache certain API responses to improve performance.

application.properties of redis configuration:

```
spring.redis.host=localhost
spring.redis.port=6379
```

In order to check the data present in our Redis Cache database

First we need to connect to our redis container executing the following command

```
docker exec -it redis-walltet redis-cli
```

In order to list every object saved in our redis currenclty, execute the following command inside the CLI

```
SCAN 0
```

---

# Kafka Configuration

Kafka is used for asynchronous communication, in our scenario, we are using it to create the User account once the user is confirmed. So once the user confrim his account, a message is sent to our kafka topic and our kafka consumer processes this message, creating a user account right after the confirmation is correctly done

application.properties of kafka configuration:

```
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=fintech-group
```

There is also a UI that we can access, so we can check which messages were sent and processed by our Kafka topic 

Open a browser and type

```
http://localhost:8083
```

You will have access to every message that was sent to our Kafka topic

---

# Database Migration (Liquibase)

Liquibase manages database schema changes.

Migration files are located in:

```
src/main/resources/db/changelog
```

Migrations files:

```
001-create-user-table.yaml
002-create-account-table.yaml
003-create-transaction-table.yaml
```

This .yaml files are used mainly in this case to create each table on our database

Liquibase automatically creates two tracking tables:

- `DATABASECHANGELOG`
- `DATABASECHANGELOGLOCK`

These tables track which migration scripts have already been executed.

---

# Entities

The application database contains the following main entities.

### User

```
id
name
email
password
confirmedAccount
```

---

### Account

```
id
user_id
balance
```

---

### Transaction

```
id
sender_account_id
receiver_account_id
balance
timestamp
```

---

# API Endpoints

## Authentication

### Register User

```
POST /api/fintech/auth/register
```

Example request:

```json
{
  "name": "John Doe",
  "email": "john@email.com",
  "password": "password123"
}
```

---

### Login

```
POST /api/fintech/auth/login
```

Example request:

```json
{
  "email": "john@email.com",
  "password": "password123"
}
```

Response returns a **JWT token**.

---

### Check User 'confirmation token'

```
GET /api/fintech/check-code/{userName}
```

Response returns a **String** with the code associated to this email


### Confirm User and subsequently create an Account associated to this User if confirmation happens currectly

```
GET /api/fintech//confirm-code/{userName}
```

Response returns a **String** if it happened as supposed or throw an error if any exception was throwned


# Account

### Deposit money in to our own Account

User can allways deposit money on its own account, in another to perform operations with other Accounts

```
POST /api/fintech/account/deposit
```

Response returns a **String** if it happened as supposed or throw an error if any exception was throwned 


# Transactions

### Get Transaction History for a specific Account

```
GET /api/fintech/transaction/getAllTransactions/{accountId}
```

Returns all transactions associated to this account

Example response:

```json
[
  {
    "id": 1,
    "balance": 100.00,
    "timestamp": "2026-03-04T18:10:10",
    "senderId": 1,
    "receiverId": 2,
    "operationType": "TRANSFER"
  }
]
```

---

### Withdraw Money

```
POST /api/fintech/transaction/withdraw
```

Withdraw Money from one account to another

Example request:

```json
{
  "receiverId": 2,
  "senderId": 1,
  "amount": 100
}
```

---

# Security

The application uses **Spring Security with JWT authentication**.

Endpoints that require authentication the following header is needed:

```
Authorization: Bearer <JWT_TOKEN>
```

Passwords are encrypted using **BCrypt**.

---

# Functionalities

The application supports the following main features:

- Register user
- Confirm user email
- Login authentication
- Deposit balance
- Transfer money between accounts
- View transaction history

---

# Notes

### Registration Confirmation

The system implements a **registration confirmation mechanism**.

Normally, systems send a confirmation code via email.  
To simplify testing and avoid requiring an external email provider, this project implements a different approach:

1. The user registers.
2. A confirmation code is generated.
3. The authenticated user can access a **GET endpoint** to retrieve their confirmation code.
4. The user sends the code to a **POST endpoint** to confirm the account.

---

### Event Driven Account Creation

When a user confirms their account:

1. A message is produced by the application.
2. Kafka processes the event.
3. A consumer receives the event.
4. The user account (wallet) is automatically created.

This demonstrates the implementation of **message-driven architecture** inside the application.

---

# Future Improvements

Planned improvements include:

- API documentation with Swagger/OpenAPI
- Monitoring and logging tools
- CI/CD pipeline

---

# Author

Nuno Cruz - @sarkosdev
