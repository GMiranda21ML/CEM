# CEM — Central de Monitoria e Mentoria

Sistema de gestão de monitoria e mentoria acadêmica. Veja `AGENTS.md` para a arquitetura e os
módulos do projeto.

## Pré-requisitos

- **Java 25 ou mais novo** (o framework D5 é compilado nessa versão; não funciona com uma
  versão menor).
  - Linux: pacote `java-26-openjdk` (ou equivalente da sua distro).
  - Windows: baixe o instalador do OpenJDK (ex.: https://adoptium.net), instale e configure a
    variável de ambiente `JAVA_HOME` apontando para a pasta de instalação.
- **Maven**
- **Node.js** (para o frontend React)
- **MariaDB** ou **MySQL** rodando localmente (sem Docker)

## 1. Banco de dados (MariaDB)

### Linux (Arch)

Instale e inicie o serviço:

```
sudo pacman -S mariadb
sudo mariadb-install-db --datadir=/var/lib/mysql --user=mysql
sudo systemctl enable --now mariadb
```

Crie o banco e o usuário usados pela aplicação (as credenciais já estão em
`ui-backend/src/main/resources/application.properties`):

```sql
sudo mariadb -u root
CREATE DATABASE cem;
CREATE USER 'cem'@'localhost' IDENTIFIED BY 'cem';
GRANT ALL PRIVILEGES ON cem.* TO 'cem'@'localhost';
FLUSH PRIVILEGES;
```

### Windows

1. Baixe e instale o MariaDB Server em https://mariadb.org/download (o instalador já sobe o
   serviço automaticamente e pede uma senha para o usuário `root`).
2. Abra o **MySQL Client** (ou `mysql`/`mariadb` pelo prompt de comando, se tiver adicionado ao
   PATH na instalação) e rode:

```sql
mariadb -u root -p
CREATE DATABASE cem;
CREATE USER 'cem'@'localhost' IDENTIFIED BY 'cem';
GRANT ALL PRIVILEGES ON cem.* TO 'cem'@'localhost';
FLUSH PRIVILEGES;
```

As tabelas são criadas/atualizadas automaticamente na primeira execução
(`spring.jpa.hibernate.ddl-auto=update`), nos dois sistemas.

## 2. E-mail (envio real)

A aplicação envia e-mails de verdade (convites e código de verificação). É preciso uma conta
Gmail com **senha de app**:

1. Ative a verificação em duas etapas na conta Google.
2. Gere uma senha de app em https://myaccount.google.com/apppasswords.
3. Copie o template e preencha com usuário e senha de app:

**Linux:**

```
cp ui-backend/src/main/resources/application-local.properties.example \
   ui-backend/src/main/resources/application-local.properties
```

**Windows (PowerShell):**

```
copy ui-backend\src\main\resources\application-local.properties.example `
     ui-backend\src\main\resources\application-local.properties
```

Edite `application-local.properties` com o e-mail e a senha de app gerados. Esse arquivo é
ignorado pelo git (nunca commitar credenciais).

## 3. Rodar o backend

```
mvn spring-boot:test-run -pl ui-backend
```

Sobe em `http://localhost:8080`. Na primeira execução, uma conta de administrador é criada
automaticamente:

- **E-mail**: `administrador@cem.edu.br`
- **Senha**: `Cem@2026!`

## 4. Rodar o frontend

Em outro terminal:

```
cd ui-frontend/src/main/react
npm install
npm run start
```

Sobe em `http://localhost:4200` e encaminha as chamadas `/backend/**` para o backend na porta
8080. É necessário manter os dois processos rodando ao mesmo tempo (não existe um modo único
que suba os dois juntos nesse projeto).

Acesse `http://localhost:4200` no navegador e entre com a conta de administrador para começar
(convidar coordenador, liberar domínio de e-mail de aluno, etc.).

## Testes

```
mvn test
```

Roda os cenários Cucumber de cada módulo de domínio. Para os testes do frontend:

```
cd ui-frontend/src/main/react
npm test
```
