# Production Environment

## Run the app

*Move to **/production-env** and execute the **docker-compose-wallet-app-prod.yaml** file*

```
$ cd production-env
$ docker-compose -f docker-compose-wallet-app-prod.yaml up --build
```


## Accesses

This section allows us to access the application, the Adminer service UI conected to our MySQL database, and Kafka UI so you can check messages received on the topic.

- Fintech Application: http://localhost:8088/home

- Adminer: http://localhost:8085
    - System: MySQL/MariaDB
    - Server: mysql
    - Username: root
    - Password: root
    - Database: fintech-db

- Kafka UI: http://localhost:8083


## Production Environment Setup

The following section explains all the steps followed in order to deliver our application in to Production environment.

- Created **application-prod.properties** file in order to config all the services needed to run our backend application;
- Generate backend **.jar** backend file in order to deploy backend application execution **mvn clean package -DskipTests**;
- Created backend **Dockerfile**
- Build our frontend application running **npm run build**; 
- Created frontend **Dockerfile**
- Created and built **docker-compose-wallet-app-prod.yaml**;

This were all the steps followed to allow our application to be deployed in production environment.


## Notes


#### 1. Account Confirmation

You need to confirm your account in order for the system create your Wallet.
To confirm your account, follow these steps:

- Register your account
- Access endpoint using, for example POSTMAN, with a GET request http://localhost:8080/api/fintech/auth/check-code/{email} change '{email}' with the email you registered with
- Check the code associated to your account
- Access endpoint using, for example POSTMAN, with a POST request http://localhost:8080/api/fintech/auth/confirm-code/{email}?code={code} change '{email}' with the email you registered with and {code} with the code returned in the previous GET request

After this, your account will be confirmed and it will have a Wallet associated with it, allowing you to perform deposits or transactions in the system.

This mechanism simulates the confirmation system usually used accross other websites. However, since we wanted to avoid the email system setup, we created an easy way to mock it.

---

#### 2. New Changes on Frontend or Backend

**Frontend Changes**

If you need to perform changes in the frontend service, follow these steps to see your changes deployed in this production environment:

- cd Simple-API-and-Wallet_Frontend
- npm run build
- docker-compose -f docker-compose-wallet-app-prod.yaml up --build

You will then see your frontend changes reflected in the production environment.

**Backend Changes**

If you need to perform changes in the backend service, follow these steps to see your changes deployed in this production environment:

- cd Simple-API-and-Wallet
- mvn clean package -DskipTests
- docker-compose -f docker-compose-wallet-app-prod.yaml up --build

You will then see your backend changes reflected in the production environment.

---

