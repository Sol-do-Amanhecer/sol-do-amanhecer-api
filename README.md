# 🌞 Sol do Amanhecer - API 

API desenvolvida em **Java 19** com **Spring Boot** para o sistema web da plataforma **Sol do Amanhecer**. Este backend é responsável por autenticação, controle de usuários e regras de negócio do sistema, suportando diferentes ambientes com foco em segurança, escalabilidade e praticidade no desenvolvimento.

---

## 🚀 Visão Geral

O projeto fornece:

- Autenticação via JWT
- Cadastro e gerenciamento de usuários
- CRUDs para entidades principais do sistema
- Boas práticas de segurança e modularização
- Perfis de ambiente (`dev`, `homol`, `prod`)
- Banco em memória para teste
- Banco persistente em container Docker para produção e homologação

---

## 🧰 Tecnologias e Ferramentas

- **Java 19**
- **Spring Boot 3+**
    - Spring Web
    - Spring Security
    - Spring Data JPA
    - Spring Validation
- **JWT** para autenticação
- **H2 Database** (dev)
- **PostgreSQL (Docker)** (produção e homologação)
- **Lombok**
- **OpenAPI/Swagger**
- **Maven** para build
- **Docker Compose** (em produção e homologação)

---

## 📁 Estrutura de Pastas

```bash
docker-config/                    # Configurações docker
src/
├── main/
│   ├── java/
│   │   └── br.com.soldoamanhecer/
│   │       ├── config/           # Configurações gerais (CORS, Swagger, security)
│   │       ├── controller/       # Endpoints REST
│   │       ├── dto/              # Objetos de transferência
│   │       ├── exception/        # Exception da aplicação
│   │       ├── model/            # Entidades, DTO's e Mappers
│   │       ├── repository/       # Interfaces de persistência
│   │       ├── security/         # Regra JWT
│   │       └── service/          # Camada de regra de negócio
│   │       └── shared/           # Constantes e enums
│   │       └── util/             # A onde fica os úteis do projeto
│   └── resources/
│       ├── application.yml       # Configuração padrão
│       ├── application-dev.yml   # Configuração de desenvolvimento (H2)
│       ├── application-homol.yml # Configuração de homologação (PostgreSQL via Docker)
│       └── application-prod.yml  # Configuração de produção (PostgreSQL via Docker)
├───test/
    ├── controller/               # Testes endpoints
    └── service/                  # Testes service
```

---

## 🧪 Ambientes

| Ambiente     | Banco de Dados | Observações                   |
|--------------|----------------|-------------------------------|
| `dev`        | H2 (memória)   | Rápido para desenvolvimento   |
| `homol`      | PostgreSQL     | Container Docker com volume   |
| `prod`       | PostgreSQL     | Container Docker com volume   |

### 🐳 Banco de Produção (PostgreSQL via Docker)

```bash
docker-compose up -d
```

Exemplo de `docker-compose.yml` (ajuste conforme necessário):

```yaml
version: '3.1'

services:
  db:
    image: postgres:15
    restart: always
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
      POSTGRES_DB: soldoamanhecer
    ports:
      - "5432:5432"
    volumes:
      - pgdata:/var/lib/postgresql/data

volumes:
  pgdata:
```

---

## 📑 Swagger (Documentação da API)

Após rodar localmente, acesse:

[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

## ✅ Status e Funcionalidades

- [x] Configuração de perfis de ambiente
- [x] Banco em memória para desenvolvimento
- [x] Banco persistente via Docker para produção/homologação
- [x] Autenticação JWT
- [x] Modularização por camadas
- [x] Documentação Swagger
- [x] Integração contínua com GitHub Actions

---

## 👨‍💻 Desenvolvedores

| Nome                             | Função                   |
|----------------------------------|--------------------------|
| Rafael Gabriel Ferreira da Silva | Desenvolvedor Full Stack |
| Matheus Siebeneichler Branco     | Desenvolvedor Back-End   |

---

## 📝 Licença

Este projeto está licenciado sob a [ONG Sol do Amanhecer junto com IFAL](LICENSE).

---

## 📬 Contato

Caso tenha dúvidas, sugestões ou problemas, sinta-se à vontade para entrar em contato com a ONG Sol do Amanhecer ou IFAL.