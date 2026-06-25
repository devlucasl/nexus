# Sistema NEXUS - Gestão Inteligente de Estoque

Projeto Java + Spring Boot para o sistema NEXUS, seguindo a documentação de requisitos e arquitetura.

## Requisitos implementados

- **US01** - Autenticar usuário.
- **US02** - Cadastrar produto.
- **US03** - Editar produto.
- **US04** - Excluir produto, com exclusão lógica quando houver movimentação.
- **US05** - Alterar disponibilidade do produto.
- **US06** - Consultar produto por nome/listagem.
- **US07** - Pesquisar produto por código.
- **US08** - Registrar entrada de estoque.
- **US09** - Registrar saída de estoque.
- **US10** - Ajustar quantidade manualmente com auditoria.
- **US11** - Criar pedido de venda com número único.
- **US12** - Adicionar item ao pedido.
- **US13** - Finalizar venda com baixa automática de estoque.
- **US14** - Cancelar venda com estorno de estoque.
- **US15** - Imprimir comprovante pela tela web.
- **US16** - Cadastrar fornecedor.
- **US17** - Editar fornecedor.
- **US18** - Cadastrar categoria.
- **US19** - Criar promoção.
- **US20** - Consultar disponibilidade.
- **US21** - Visualizar alerta de estoque baixo.
- **US22** - Visualizar alteração automática de disponibilidade.
- **US23** - Visualizar promoção ativa automaticamente na consulta e na venda.
- **US24** - Consultar histórico de ajuste de estoque.

Também foi incluído módulo de **relatórios/exportação de estoque em CSV**, vinculado às regras RN-016 e RN-017.

## Tecnologias

- Java 17+
- Spring Boot 3.5.14
- Spring MVC
- Spring Data JPA
- Spring Security
- Bean Validation
- Thymeleaf
- H2 em desenvolvimento
- PostgreSQL preparado por profile

## Como abrir no IntelliJ

1. Extraia o `.zip`.
2. Abra o IntelliJ IDEA.
3. Clique em **Open** e selecione a pasta `Nexus`.
4. Aguarde o IntelliJ importar o Maven pelo `pom.xml`.
5. Execute a classe `br.nexus.NexusApplication`.

## Como rodar pelo terminal

```bash
mvn spring-boot:run
```

A aplicação sobe em:

```text
http://localhost:8080
```

Console H2:

```text
http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:nexusdb
User: sa
Password: password
```

## Usuários iniciais

| Perfil | Login | Senha |
|---|---|---|
| Administrador | `admin` | `admin123` |
| Funcionário | `funcionario` | `func123` |

As senhas são gravadas com BCrypt no banco.

## Frontend web integrado

Acesse:

```text
http://localhost:8080
```

Telas principais:

- Login
- Dashboard
- Produtos
- Vendas
- Estoque
- Categorias
- Fornecedores
- Promoções
- Relatórios
- Comprovante de venda

## Fluxo web recomendado para teste

1. Entrar com `admin/admin123`.
2. Cadastrar ou usar o produto demonstrativo.
3. Criar uma promoção, se desejar testar preço promocional.
4. Ir em **Vendas** e criar pedido.
5. Adicionar item ao pedido.
6. Finalizar a venda.
7. Abrir o comprovante e imprimir.
8. Como administrador, cancelar a venda para validar o estorno de estoque.
9. Verificar o histórico em **Estoque** e exportar relatório em **Relatórios**.

## Exemplos de API

### Login

```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "login": "admin",
  "senha": "admin123"
}
```

### Criar pedido de venda

```http
POST http://localhost:8080/api/vendas
Authorization: Basic funcionario func123
```

### Adicionar item ao pedido

```http
POST http://localhost:8080/api/vendas/1/itens
Authorization: Basic funcionario func123
Content-Type: application/json

{
  "produtoId": 1,
  "quantidade": 2
}
```

### Finalizar venda

```http
POST http://localhost:8080/api/vendas/1/finalizar
Authorization: Basic funcionario func123
```

### Cancelar venda

```http
POST http://localhost:8080/api/vendas/1/cancelar
Authorization: Basic admin admin123
```

### Criar promoção

```http
POST http://localhost:8080/api/promocoes
Authorization: Basic admin admin123
Content-Type: application/json

{
  "produtoId": 1,
  "tipoDesconto": "PERCENTUAL",
  "valorDesconto": 10,
  "dataInicio": "2026-06-01",
  "dataFim": "2026-12-31",
  "ativa": true
}
```

### Exportar relatório de estoque

```http
GET http://localhost:8080/api/relatorios/estoque.csv
Authorization: Basic admin admin123
```

## Profile PostgreSQL

Para rodar usando PostgreSQL:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```

Variáveis esperadas:

```text
DB_URL=jdbc:postgresql://localhost:5432/nexus
DB_USER=postgres
DB_PASSWORD=postgres
```

## Estrutura principal

```text
src/main/java/br/nexus
├── config
├── controller
├── dto
├── exception
├── model
├── repository
├── security
├── service
└── validation
```
