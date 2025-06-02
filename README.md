<h1 align="center">
  <img src="assets/vetdatalogo.jpg" alt="VetData Logo" width="246"/>
</h1>

# 🐾 VETDATA APP

VetData é uma aplicação desenvolvida em Java 21 com Spring Boot para gerenciar dados veterinários — incluindo raças, diagnósticos, internações, prontuários e registros pós-operatórios. Tudo isso com uma API REST documentada com Swagger e um banco de dados MySQL rodando em Docker.

---

## 🚀 Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 3.4.1**
- **Spring Data JPA**
- **SpringDoc OpenAPI (Swagger UI)**
- **MySQL 8**
- **Docker + Docker Compose**
- **Testcontainers + JUnit 5**
- **Jacoco**
- **RestAssured** e **WireMock**
- **Password4j**

---

## 📦 How to Run

```bash
docker compose up --build --force-recreate
```

---

# 💻 API disponível em:
```bash
http://localhost:8085
```

---

## 🧪 Executando os Testes
```bash
./mvn clean install
```

---

## 📜 Documentação da API
```bash
http://localhost:8085/swagger-ui.html
```

---

## 🌐 Integração com API Externa
A aplicação consome dados da seguinte API pública de raças caninas:
```bash
https://dogapi.dog/api/v2/breeds
```

---

## 📈 Relatório de Cobertura (Jacoco)
O relatório HTML estará em:
```bash
target/site/jacoco/index.html
```

---

## 👨‍💻 Autor e Colaboradores

**Felipe** — Mentor & Frontend Developer 💻  
Idealizador do projeto, responsável pelo planejamento técnico e acompanhamento do desenvolvimento backend. Também desenvolveu a aplicação frontend integrada à API VetData.

**Cleyton** — Backend Developer Java 💻  
Responsável pelo desenvolvimento completo do backend, testes automatizados, integração com banco de dados, documentação via Swagger e configuração com Docker.