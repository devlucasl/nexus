# NEXUS — Gestão Inteligente de Estoque

Sistema web desenvolvido em **Java com Spring Boot** para gestão inteligente de estoque, produtos, categorias, fornecedores, vendas, promoções, relatórios e controle de acesso por perfil de usuário.

O projeto foi desenvolvido como aplicação acadêmica para a Universidade Católica de Brasília — UCB, com base nos documentos de requisitos e arquitetura do Sistema NEXUS.

---

## 1. Visão geral

O NEXUS tem como objetivo auxiliar pequenos e médios estabelecimentos no controle de estoque e no registro de vendas, permitindo:

* autenticação de usuários;
* cadastro e consulta de produtos;
* controle de disponibilidade;
* entrada, saída e ajuste manual de estoque;
* auditoria de ajustes;
* criação e finalização de pedidos de venda;
* cancelamento de vendas com estorno de estoque;
* cadastro de categorias;
* cadastro de fornecedores;
* cadastro de promoções;
* aplicação automática de promoções ativas;
* geração de relatórios de estoque;
* exportação de relatório em CSV;
* controle de permissões por perfil.

---

## 2. Tecnologias utilizadas

### Backend

* Java 17+
* Spring Boot
* Spring MVC
* Spring Data JPA
* Spring Security
* Thymeleaf
* Bean Validation
* Maven

### Testes

* JUnit 5
* Mockito
* MockMvc
* Spring Security Test

### Frontend

* Thymeleaf
* HTML5
* CSS3
* Layout organizado em fragments e páginas por módulo

---

## 3. Perfis de usuário

O sistema possui dois perfis principais:

### ADMINISTRADOR

Possui acesso completo ao sistema, incluindo:

* produtos;
* estoque;
* categorias;
* fornecedores;
* vendas;
* promoções;
* relatórios;
* usuários;
* alteração de perfil de usuários;
* cancelamento de vendas;
* exportação de relatório.

### FUNCIONARIO

Possui acesso operacional restrito, incluindo:

* dashboard;
* consulta de produtos;
* visualização de disponibilidade;
* criação de pedidos de venda;
* adição de itens ao pedido;
* finalização de venda;
* emissão de comprovante.

Por segurança, novos cadastros realizados pela tela pública são criados automaticamente como `FUNCIONARIO`. Apenas um usuário administrador pode promover outro usuário para `ADMINISTRADOR`.

---

## 4. Funcionalidades implementadas

### Autenticação e usuários

* Login com Spring Security.
* Cadastro público de usuário.
* Senhas criptografadas com BCrypt.
* Controle de sessão.
* Perfil padrão de cadastro: `FUNCIONARIO`.
* Tela administrativa de usuários.
* Alteração de perfil de usuário por administrador.

### Produtos

* Cadastro de produtos.
* Edição de produtos.
* Exclusão/inativação de produtos.
* Consulta por nome.
* Consulta por código.
* Controle de disponibilidade.
* Exibição de alerta de estoque baixo.
* Aplicação visual de preço promocional quando houver promoção ativa.

### Estoque

* Registro de entrada de estoque.
* Registro de saída de estoque.
* Bloqueio de saída com estoque insuficiente.
* Ajuste manual de quantidade.
* Justificativa obrigatória para ajuste manual.
* Histórico de movimentações.
* Histórico de ajustes manuais.
* Atualização automática de disponibilidade:

  * estoque igual a zero torna produto indisponível;
  * nova entrada de estoque torna produto disponível.

### Categorias

* Cadastro de categorias.
* Listagem de categorias ativas.
* Associação de categoria ao produto.

### Fornecedores

* Cadastro de fornecedores.
* Edição de fornecedores.
* Validação de nome obrigatório.
* Validação de ao menos um contato: telefone ou e-mail.
* Associação de fornecedor ao produto.

### Vendas

* Criação de pedido de venda.
* Geração automática de número único do pedido.
* Adição de itens ao pedido.
* Bloqueio de item com quantidade inválida.
* Bloqueio de produto inativo/indisponível.
* Finalização de venda.
* Baixa automática de estoque.
* Registro do valor total.
* Cancelamento de venda por administrador.
* Estorno automático de estoque.
* Emissão de comprovante.

### Promoções

* Cadastro de promoção por produto.
* Tipo de desconto:

  * percentual;
  * valor fixo.
* Validação de período.
* Validação de desconto.
* Exibição de status:

  * ativa;
  * programada;
  * encerrada;
  * inativa.
* Aplicação automática de promoção vigente em consultas e vendas.

### Relatórios

* Tela de relatório de estoque.
* Exibição de:

  * código;
  * descrição;
  * categoria;
  * fornecedor;
  * preço;
  * quantidade atual;
  * estoque mínimo;
  * disponibilidade;
  * alerta de estoque baixo.
* Exportação de relatório em CSV.
* Acesso restrito ao perfil `ADMINISTRADOR`.

---

## 5. Estrutura do projeto

```text
src
├── main
│   ├── java
│   │   └── br
│   │       └── nexus
│   │           ├── config
│   │           ├── controller
│   │           │   └── web
│   │           ├── dto
│   │           │   ├── request
│   │           │   └── response
│   │           ├── exception
│   │           ├── model
│   │           ├── repository
│   │           ├── security
│   │           ├── service
│   │           └── validation
│   │
│   └── resources
│       ├── static
│       │   └── css
│       ├── templates
│       │   ├── auth
│       │   ├── fragments
│       │   └── pages
│       ├── application.properties
│       ├── application-dev.properties
│       └── application-postgres.properties
│
└── test
    └── java
        └── br
            └── nexus
                ├── controller
                └── service
```

---

## 6. Principais pacotes

### `config`

Configurações gerais da aplicação, segurança, autenticação, autorização, sessão e criptografia de senha.

### `controller`

Controladores responsáveis por receber requisições HTTP e direcionar para os services.

### `dto`

Objetos de transferência de dados.

* `dto.request`: dados recebidos da tela/API.
* `dto.response`: dados devolvidos para tela/API.

### `model`

Entidades de domínio persistidas no banco de dados.

Principais entidades:

* `Usuario`
* `Produto`
* `Categoria`
* `Fornecedor`
* `MovimentacaoEstoque`
* `HistoricoAjusteEstoque`
* `PedidoVenda`
* `ItemPedido`
* `Promocao`

### `repository`

Interfaces de acesso ao banco de dados usando Spring Data JPA.

### `service`

Camada de regras de negócio e transações.

Principais services:

* `UsuarioService`
* `ProdutoService`
* `EstoqueService`
* `CategoriaService`
* `FornecedorService`
* `VendaService`
* `PromocaoService`
* `RelatorioService`

### `templates`

Telas HTML com Thymeleaf.

### `static/css`

Arquivos CSS separados por responsabilidade:

* `nexus.css`
* `base.css`
* `layout.css`
* `components.css`
* `pages.css`

---

## 7. Requisitos atendidos

| Código | Requisito                                          | Situação     |
| ------ | -------------------------------------------------- | ------------ |
| US01   | Autenticar usuário                                 | Implementado |
| US02   | Cadastrar produto                                  | Implementado |
| US03   | Editar produto                                     | Implementado |
| US04   | Excluir produto                                    | Implementado |
| US05   | Alterar disponibilidade do produto                 | Implementado |
| US06   | Consultar produto                                  | Implementado |
| US07   | Pesquisar produto por código                       | Implementado |
| US08   | Registrar entrada de estoque                       | Implementado |
| US09   | Registrar saída de estoque                         | Implementado |
| US10   | Ajustar quantidade manualmente                     | Implementado |
| US11   | Criar pedido de venda                              | Implementado |
| US12   | Adicionar item ao pedido                           | Implementado |
| US13   | Finalizar venda                                    | Implementado |
| US14   | Cancelar venda                                     | Implementado |
| US15   | Imprimir comprovante                               | Implementado |
| US16   | Cadastrar fornecedor                               | Implementado |
| US17   | Editar fornecedor                                  | Implementado |
| US18   | Cadastrar categoria                                | Implementado |
| US19   | Criar promoção                                     | Implementado |
| US20   | Consultar disponibilidade                          | Implementado |
| US21   | Visualizar alerta de estoque baixo                 | Implementado |
| US22   | Visualizar alteração automática de disponibilidade | Implementado |
| US23   | Visualizar promoção ativa automaticamente          | Implementado |
| US24   | Consultar histórico de ajuste de estoque           | Implementado |

---

Novos usuários cadastrados pela tela `/cadastro` entram como:

```text
FUNCIONARIO
```

Para promover um usuário para administrador:

1. Faça login como `admin`.
2. Acesse a tela `Usuários`.
3. Altere o perfil do usuário desejado para `ADMINISTRADOR`.
4. Salve.
5. O usuário promovido deve sair e entrar novamente no sistema.

---

## 12. Rotas principais

| Tela         | Rota            |
| ------------ | --------------- |
| Login        | `/login`        |
| Cadastro     | `/cadastro`     |
| Dashboard    | `/dashboard`    |
| Produtos     | `/produtos`     |
| Vendas       | `/vendas`       |
| Estoque      | `/estoque`      |
| Promoções    | `/promocoes`    |
| Relatórios   | `/relatorios`   |
| Categorias   | `/categorias`   |
| Fornecedores | `/fornecedores` |
| Usuários     | `/usuarios`     |
| H2 Console   | `/h2-console`   |

---

## 13. Segurança e permissões

As permissões são controladas pelo Spring Security.

### Rotas públicas

* `/login`
* `/cadastro`
* `/css/**`
* `/h2-console/**`

### Rotas autenticadas

* `/dashboard`
* `/produtos`
* `/vendas`

### Rotas exclusivas de administrador

* `/estoque`
* `/promocoes`
* `/relatorios`
* `/categorias`
* `/fornecedores`
* `/usuarios`
* ações de criação, edição, exclusão e alteração de produtos;
* cancelamento de vendas;
* exportação de relatórios.

---

## 14. Testes

O projeto possui testes automatizados em:

### Rodar todos os testes

```bash
mvn test
```

### Rodar teste específico

```bash
mvn test -Dtest=ProdutoServiceTest
```

### Rodar teste de segurança

```bash
mvn test -Dtest=SecurityAccessTest
```

### Ferramentas de teste utilizadas

* JUnit 5;
* Mockito;
* MockMvc;
* Spring Security Test.

### Exemplos de cenários testados

* funcionário acessa tela de produtos;
* funcionário não acessa relatórios;
* funcionário não acessa estoque;
* administrador acessa relatórios;
* produto duplicado é bloqueado;
* estoque insuficiente é bloqueado;
* venda baixa estoque;
* cancelamento estorna estoque;
* promoção inválida é bloqueada.

---



## 19. Repositório

```text
https://github.com/devlucasl/nexus
```

---

## 20. Autores

Grupo: Gestão Inteligente de Estoque
Projeto acadêmico — Universidade Católica de Brasília — UCB

---

## 21. Licença

Projeto desenvolvido para fins acadêmicos.
