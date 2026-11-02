-- Crea l'utente e il database usati dall'applicazione (da eseguire UNA volta, come utente "postgres").
-- Si puo' rieseguire senza danni: se esistono gia', non fa nulla.
-- Le credenziali devono coincidere con quelle di web/META-INF/context.xml.

SELECT 'CREATE ROLE ecommerce_user LOGIN PASSWORD ''ecommerce'''
 WHERE NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'ecommerce_user')
\gexec

SELECT 'CREATE DATABASE ecommerce OWNER ecommerce_user'
 WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'ecommerce')
\gexec
