-- Cria o usuário específico para o perfil de PROD
CREATE USER "DonodoNegocio" WITH PASSWORD 'Negociador1234';

-- Garante privilégios no banco criado via variável de ambiente
GRANT ALL PRIVILEGES ON DATABASE "DonodoNegocio" TO "DonodoNegocio";

-- Conecta no banco para dar permissão no schema public (Necessário no PG 15+)
\c "DonodoNegocio";
GRANT ALL ON SCHEMA public TO "DonodoNegocio";