-- evita duplicação acidental de e-mail e acelera busca
CREATE UNIQUE INDEX ux_users_email ON users(email);

-- busca de itens por título (case-insensitive)
CREATE INDEX ix_itens_solicitados_titulo
  ON itens_solicitados (LOWER(titulo));

-- índice em data de criação para relatórios
CREATE INDEX ix_itens_data_criacao
  ON itens_solicitados (data_criacao);
