CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  nome_completo VARCHAR(255) NOT NULL,
  email VARCHAR(255) UNIQUE NOT NULL,
  senha VARCHAR(255) NOT NULL,
  tipo VARCHAR(50) NOT NULL,
  cidade VARCHAR(100),
  bairro VARCHAR(100),
  rua VARCHAR(100),
  numero VARCHAR(50),
  telefone VARCHAR(50),
  cnpj VARCHAR(20),
  avatar_url VARCHAR(255)
);
