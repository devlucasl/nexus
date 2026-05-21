CREATE TABLE usuario (
    id_usuario BIGSERIAL PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    login VARCHAR(80) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    perfil VARCHAR(30) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    ultimo_acesso TIMESTAMP
);

CREATE TABLE categoria (
    id_categoria BIGSERIAL PRIMARY KEY,
    nome VARCHAR(120) NOT NULL UNIQUE,
    descricao VARCHAR(255),
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE fornecedor (
    id_fornecedor BIGSERIAL PRIMARY KEY,
    nome VARCHAR(120) NOT NULL,
    telefone VARCHAR(30),
    email VARCHAR(120),
    ativo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE produto (
    id_produto BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(80) NOT NULL UNIQUE,
    descricao VARCHAR(255) NOT NULL,
    preco_venda NUMERIC(15,2) NOT NULL,
    quantidade_atual INTEGER NOT NULL DEFAULT 0,
    estoque_minimo INTEGER NOT NULL DEFAULT 0,
    disponibilidade BOOLEAN NOT NULL DEFAULT TRUE,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    id_categoria BIGINT NOT NULL,
    id_fornecedor BIGINT,

    CONSTRAINT fk_produto_categoria
        FOREIGN KEY (id_categoria)
        REFERENCES categoria(id_categoria),

    CONSTRAINT fk_produto_fornecedor
        FOREIGN KEY (id_fornecedor)
        REFERENCES fornecedor(id_fornecedor),

    CONSTRAINT chk_produto_preco
        CHECK (preco_venda > 0),

    CONSTRAINT chk_produto_quantidade
        CHECK (quantidade_atual >= 0),

    CONSTRAINT chk_produto_estoque_minimo
        CHECK (estoque_minimo >= 0)
);

CREATE TABLE movimentacao_estoque (
    id_movimentacao BIGSERIAL PRIMARY KEY,
    id_produto BIGINT NOT NULL,
    id_usuario BIGINT NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    quantidade INTEGER NOT NULL,
    data_hora TIMESTAMP NOT NULL,
    observacao VARCHAR(255),

    CONSTRAINT fk_movimentacao_produto
        FOREIGN KEY (id_produto)
        REFERENCES produto(id_produto),

    CONSTRAINT fk_movimentacao_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario),

    CONSTRAINT chk_movimentacao_quantidade
        CHECK (quantidade > 0)
);

CREATE TABLE historico_ajuste_estoque (
    id_ajuste BIGSERIAL PRIMARY KEY,
    id_produto BIGINT NOT NULL,
    id_usuario BIGINT NOT NULL,
    quantidade_anterior INTEGER NOT NULL,
    quantidade_nova INTEGER NOT NULL,
    justificativa VARCHAR(255) NOT NULL,
    data_hora TIMESTAMP NOT NULL,

    CONSTRAINT fk_ajuste_produto
        FOREIGN KEY (id_produto)
        REFERENCES produto(id_produto),

    CONSTRAINT fk_ajuste_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario)
);

CREATE TABLE pedido_venda (
    id_pedido BIGSERIAL PRIMARY KEY,
    numero VARCHAR(80) NOT NULL UNIQUE,
    id_usuario BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL,
    data_abertura TIMESTAMP NOT NULL,
    data_finalizacao TIMESTAMP,
    valor_total NUMERIC(15,2) NOT NULL DEFAULT 0,

    CONSTRAINT fk_pedido_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario)
);

CREATE TABLE item_pedido (
    id_item BIGSERIAL PRIMARY KEY,
    id_pedido BIGINT NOT NULL,
    id_produto BIGINT NOT NULL,
    quantidade INTEGER NOT NULL,
    preco_unitario NUMERIC(15,2) NOT NULL,
    desconto_aplicado NUMERIC(15,2) NOT NULL DEFAULT 0,
    subtotal NUMERIC(15,2) NOT NULL,

    CONSTRAINT fk_item_pedido
        FOREIGN KEY (id_pedido)
        REFERENCES pedido_venda(id_pedido),

    CONSTRAINT fk_item_produto
        FOREIGN KEY (id_produto)
        REFERENCES produto(id_produto),

    CONSTRAINT chk_item_quantidade
        CHECK (quantidade > 0)
);

CREATE TABLE promocao (
    id_promocao BIGSERIAL PRIMARY KEY,
    id_produto BIGINT NOT NULL,
    tipo_desconto VARCHAR(30) NOT NULL,
    valor_desconto NUMERIC(15,2) NOT NULL,
    data_inicio DATE NOT NULL,
    data_fim DATE NOT NULL,
    ativa BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_promocao_produto
        FOREIGN KEY (id_produto)
        REFERENCES produto(id_produto),

    CONSTRAINT chk_promocao_valor
        CHECK (valor_desconto > 0),

    CONSTRAINT chk_promocao_periodo
        CHECK (data_inicio <= data_fim)
);

CREATE INDEX idx_produto_codigo ON produto(codigo);
CREATE INDEX idx_produto_descricao ON produto(descricao);
CREATE INDEX idx_produto_disponibilidade ON produto(disponibilidade);
CREATE INDEX idx_movimentacao_produto ON movimentacao_estoque(id_produto);
CREATE INDEX idx_pedido_numero ON pedido_venda(numero);