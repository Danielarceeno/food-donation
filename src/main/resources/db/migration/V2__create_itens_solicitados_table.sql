CREATE TABLE itens_solicitados (
  id                SERIAL        PRIMARY KEY,
  titulo            VARCHAR(255)  NOT NULL,
  descricao         TEXT          NOT NULL,
  categoria         VARCHAR(50)   NOT NULL,
  data_criacao      TIMESTAMP     NOT NULL DEFAULT NOW(),
  instituicao_id    BIGINT        NOT NULL,
  CONSTRAINT fk_itens_solicitados_instituicao
    FOREIGN KEY (instituicao_id) REFERENCES users(id)
);
