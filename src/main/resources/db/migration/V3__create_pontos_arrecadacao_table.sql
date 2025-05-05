CREATE TABLE pontos_arrecadacao (
  id         SERIAL        PRIMARY KEY,
  endereco   VARCHAR(255)  NOT NULL,
  item_id    BIGINT        NOT NULL,
  CONSTRAINT fk_pontos_arrecadacao_item
    FOREIGN KEY (item_id) REFERENCES itens_solicitados(id)
);
