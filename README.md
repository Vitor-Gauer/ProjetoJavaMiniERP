# Dono do Negócio
!["Logo"](https://i.ibb.co/s9yp7PpQ/logo.png)

Sistema web com back-end em Java focado em **pequenos empreendedores**, oferecendo uma gestão simplificada e eficiente de transferências financeiras e controle de estoque, sem a complexidade de ERPs corporativos.

---

## 🚀 Tecnologias

* **Java 17+** com **Spring Boot**
  * Spring Web (MVC)
  * Spring Security (Autenticação Customizada em Duas Etapas)
  * Spring Data JPA
* **Banco de Dados:** PostgreSQL
* **Front-end:** Thymeleaf + Bootstrap (Responsivo)
* **Ferramentas:** Lombok, Spring DevTools
* **Integrações:** API ViaCEP (consulta de endereços)

---

## 🎯 Objetivo e Escopo

O projeto foi desenhado para atender negócios que precisam registrar **entradas e saídas** (dinheiro e produtos) de forma ágil, **excluindo** propositalmente módulos complexos como Fiscal (NF), RH, PCP, Qualidade e Comércio Exterior.

O foco é a **saúde financeira e operacional** do dia a dia:
* **Financeiro:** Controle de Contas (Tesouro), Receitas, Despesas, Contas a Pagar/Receber.
* **Estoque:** Movimentação de Produtos, Entradas e Saídas.
* **Auditoria:** Rastreabilidade completa de ações de usuários e administradores.

---

## 🔐 Segurança e Perfis

O sistema implementa um fluxo de login em duas etapas (Empresa -> Usuário) e controle de acesso granular:

* **ADMIN (Dono):** Acesso irrestrito, incluindo relatórios de Lucro e Auditoria.
* **OPERADOR:** Registro de operações diárias (vendas, compras).
* **CONSULTOR/AUDITOR:** Visualização de relatórios e auditorias, sem permissão de escrita crítica.

---

## 🗄️ Arquitetura de Entidades

O núcleo do sistema baseia-se em um modelo flexível de **Transações e Movimentações**:

* **Core:** `Empresa`, `Usuario`, `Auditoria`
* **Financeiro:** `Tesouro` (Caixa/Banco), `Transacao`, `TipoTransacao`
* **Operacional:** `Produto`, `Estoque`, `Movimentacao`
* **Parceiros:** `Cliente`, `Fornecedor`

---

## 📊 Relatórios Gerenciais

* **Financeiro:** Despesas, Receitas, Contas Abertas (Devendo), Contas Quitadas (Quitado), Lucro (Exclusivo Admin).
* **Operacional:** Entradas e Saídas de Estoque.
* **Segurança:** Auditoria de Acessos e Tentativas de Invasão.
