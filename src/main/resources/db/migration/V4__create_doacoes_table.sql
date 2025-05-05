CREATE TABLE doacoes (
  id                   SERIAL        PRIMARY KEY,
  titulo               VARCHAR(255)  NOT NULL,
  descricao            TEXT          NOT NULL,
  categoria            VARCHAR(50)   NOT NULL,
  status               VARCHAR(50)   NOT NULL,
  usuario_id           BIGINT        NOT NULL,
  item_solicitado_id   BIGINT        NOT NULL,
  CONSTRAINT fk_doacoes_usuario
    FOREIGN KEY (usuario_id) REFERENCES users(id),
  CONSTRAINT fk_doacoes_item
    FOREIGN KEY (item_solicitado_id) REFERENCES itens_solicitados(id)
);
