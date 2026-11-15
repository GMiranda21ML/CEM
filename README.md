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

Execute na pasta raiz do projeto. Se o arquivo local já existir, preserve sua configuração:

```powershell
if (-not (Test-Path "ui-backend\src\main\resources\application-local.properties")) { Copy-Item "ui-backend\src\main\resources\application-local.properties.example" "ui-backend\src\main\resources\application-local.properties" }
```

Edite `application-local.properties` com o e-mail e a senha de app gerados. Esse arquivo é
ignorado pelo git (nunca commitar credenciais).

O caminho completo dentro do projeto é:

```text
ui-backend/src/main/resources/application-local.properties
```

No IntelliJ, procure o arquivo na árvore de pastas do projeto. Ele não aparece como uma
alteração a ser enviada ao Git. Para abri-lo pelo PowerShell, a partir da raiz do projeto:

```powershell
notepad .\ui-backend\src\main\resources\application-local.properties
```

## 3. Rodar o backend

Abra o terminal na raiz do projeto, onde está o `pom.xml` principal. O MySQL/MariaDB deve
estar em execução. Confira Java e Maven:

```powershell
java -version
mvn -version
```

O Maven deve informar Java 25 ou mais novo. Se `mvn` não for reconhecido ou o Java estiver
em outra versão, configure os caminhos nesse terminal antes de executar o backend.
Exemplo para o Java 25.0.2 e o Maven incluído no IntelliJ IDEA 2026.3:

```powershell
$env:JAVA_HOME = "$env:USERPROFILE\.jdks\openjdk-25.0.2"
$env:Path = "$env:JAVA_HOME\bin;C:\Program Files\JetBrains\IntelliJ IDEA 2026.3\plugins\maven-plugin\lib\maven3\bin;$env:Path"
mvn -version
```

Ajuste os caminhos se sua instalação for diferente. Essas variáveis valem para o terminal
atual; ao abrir outro terminal, repita a configuração ou use variáveis de ambiente permanentes.

Na primeira execução, ou após alterar outros módulos, prepare os artefatos locais:

```
mvn install -DskipTests
```

Depois, mantenha o terminal aberto e execute:

```
mvn spring-boot:test-run -pl ui-backend
```

Uma inicialização bem-sucedida mostra `Started BackendAplicacao` e a porta `8080`.
Se a porta já estiver ocupada por outra instância do CEM, use essa instância ou encerre-a
com `Ctrl + C` no respectivo terminal antes de iniciar outra.

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
