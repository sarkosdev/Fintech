
# SET UP

- install java 11
- install intellij IDE
- install docker and docker-compose 
- run 'docker-compose-wallet-app.yaml' file so we can start our MYSQL db service
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