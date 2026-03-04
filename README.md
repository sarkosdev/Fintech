# Simple API & Wallter - Fintech

# SET UP

- install java 11
- install intellij IDE
- install docker and docker-compose 
- run 'docker-compose-wallet-app.yaml' file so we can start our MYSQL db service and kafka service
- implementado a cena do liquibase, para poder defenir scripts que façam alterações á bd



# entiades 

- user (id, nome, email, senha, confirmedAccount)
- account (id, user_id, balance)
- transaction (id, sender_account_id, receiver_account_id, balance, timestamp)


# Funcionalidades

- Registar
- Confirmar email
- Login
- carregar saldo
- transferir dinheiro entre contas
- consultar historico transacoes



# notas
- DATABASECHANGELOG (table liquibase, tracking the scripts ja executados)
- DATABASECHANGELOGLOCK (table liquibase, tracking)

- Registration confirmation code funcionality is implemented to mock a type of confirmation account that is usually used around systems. Since configuring an email provider to send emails with the code will make this application a bit confused to use it by third-party users that have access to git repo, we will change it creating an GET endpoint that authenticated Users can access and checks its own code, and with this code, access a second POST endpoint where they can send it and validates the user registration


- Account creation triggers when confirming user account (right after the user is confirmed successfully it creates a message that is sent from Producer to Consumer in order to create the User account aswell - this implements message driven design concept in our application aswell)

