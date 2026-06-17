# Sistema NEXUS - Gestão Inteligente de Estoque

Projeto inicial em Java + Spring Boot para o sistema NEXUS, seguindo a documentação de requisitos e arquitetura.

## Requisitos implementados nesta primeira versão

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

Também foram incluídos endpoints auxiliares para categoria, fornecedor e consulta de disponibilidade, porque produto depende de categoria e pode estar vinculado a fornecedor.

## Tecnologias

- Java 17+
- Spring Boot 3.5.14
- Spring MVC
- Spring Data JPA
- Spring Security
- Bean Validation
- H2 em desenvolvimento
- PostgreSQL preparado por profile

## Como abrir no IntelliJ

1. Extraia o `.zip`.
2. Abra o IntelliJ IDEA.
3. Clique em **Open** e selecione a pasta `nexus`.
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

As senhas são gravadas com BCrypt no banco, não em texto puro.

## Fluxo rápido para testar no Postman/Insomnia

### 1. Login

```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "login": "admin",
  "senha": "admin123"
}
```

Depois disso, use a mesma sessão/cookie retornada ou use Basic Auth nas requisições:

```text
Username: admin
Password: admin123
```

### 2. Criar categoria

```http
POST http://localhost:8080/api/categorias
Authorization: Basic admin admin123
Content-Type: application/json

{
  "nome": "Eletrônicos",
  "descricao": "Produtos eletrônicos e acessórios"
}
```

### 3. Criar fornecedor

```http
POST http://localhost:8080/api/fornecedores
Authorization: Basic admin admin123
Content-Type: application/json

{
  "nome": "Fornecedor Nexus",
  "telefone": "61999999999",
  "email": "contato@fornecedor.com"
}
```

### 4. Criar produto

```http
POST http://localhost:8080/api/produtos
Authorization: Basic admin admin123
Content-Type: application/json

{
  "codigo": "P001",
  "descricao": "Mouse sem fio",
  "precoVenda": 89.90,
  "quantidadeAtual": 10,
  "estoqueMinimo": 3,
  "categoriaId": 1,
  "fornecedorId": 1,
  "disponivel": true
}
```

### 5. Consultar produtos

```http
GET http://localhost:8080/api/produtos?nome=mouse
Authorization: Basic funcionario func123
```

### 6. Pesquisar por código

```http
GET http://localhost:8080/api/produtos/codigo/P001
Authorization: Basic funcionario func123
```

### 7. Entrada de estoque

```http
POST http://localhost:8080/api/estoque/entrada
Authorization: Basic admin admin123
Content-Type: application/json

{
  "produtoId": 1,
  "quantidade": 5,
  "observacao": "Compra de reposição"
}
```

### 8. Saída de estoque

```http
POST http://localhost:8080/api/estoque/saida
Authorization: Basic admin admin123
Content-Type: application/json

{
  "produtoId": 1,
  "quantidade": 2,
  "observacao": "Perda operacional"
}
```

### 9. Ajuste manual

```http
POST http://localhost:8080/api/estoque/ajuste
Authorization: Basic admin admin123
Content-Type: application/json

{
  "produtoId": 1,
  "novaQuantidade": 20,
  "justificativa": "Correção após inventário físico"
}
```

### 10. Histórico de ajustes

```http
GET http://localhost:8080/api/estoque/produtos/1/historico-ajustes
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

## Próximos requisitos recomendados

- US11 - Criar pedido de venda.
- US12 - Adicionar item ao pedido.
- US13 - Finalizar venda.
- US14 - Cancelar venda.
- US19/US23 - Promoções.
- Relatórios/exportação.

## Frontend web integrado

Esta versão inclui uma interface web com Thymeleaf integrada ao Spring Boot.

### Como abrir no navegador

```bash
mvn spring-boot:run
```

Acesse:

```text
http://localhost:8080
```

Usuários de teste:

```text
Administrador: admin / admin123
Funcionário: funcionario / func123
```

### Telas disponíveis

- `/login`: autenticação com formulário.
- `/dashboard`: visão geral, consulta de produtos, disponibilidade e alertas de estoque baixo.
- `/produtos`: cadastro, consulta, edição, alteração de disponibilidade e exclusão/inativação de produtos.
- `/estoque`: entrada, saída, ajuste manual, movimentações e histórico de auditoria.
- `/categorias`: cadastro e listagem de categorias.
- `/fornecedores`: cadastro, edição e listagem de fornecedores.

O frontend utiliza os serviços já implementados no backend, preservando as regras de negócio documentadas para autenticação, produtos, disponibilidade, movimentações e auditoria de estoque.
