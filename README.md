### 📌 **Gerenciador de Tarefas**

Um sistema simples de gerenciamento de boards e tarefas via console, permitindo criar, selecionar e excluir boards, além de adicionar e gerenciar cards.

## 🚀 **Funcionalidades**

- Criar um novo board com colunas personalizadas
- Selecionar um board existente e gerenciar suas tarefas
- Criar, mover, bloquear e cancelar cards dentro dos boards
- Excluir boards

## 🛠 **Tecnologias Utilizadas**

- **Java** (JDK 17+)
- **JDBC** (para conexão com banco de dados)
- **MySQL/PostgreSQL** (dependendo da configuração)
- **Lombok** (para reduzir código boilerplate)

## 📂 **Estrutura do Projeto**

```
br.com.taskBoard
│── persistence/   # Configuração e entidades do banco de dados
│── service/       # Lógica de negócios (criação, manipulação de boards e cards)
│── ui/            # Interface via console (MainMenu, BoardMenu)
│── dto/           # Objetos de transferência de dados (DTOs)
│── config/        # Configuração da conexão com o banco de dados

```

## ▶️ **Como Executar**

### 1️⃣ **Configurar o Banco de Dados**

Antes de executar, configure seu banco de dados e crie as tabelas necessárias.

### 2️⃣ **Compilar e Executar o Projeto**

```
javac -d bin src/br/com/taskBoard/ui/MainMenu.java
java -cp bin br.com.taskBoard.ui.MainMenu

```

### 3️⃣ **Usar o Sistema**

Ao executar o programa, você verá um menu no console com opções para gerenciar boards e tarefas.

## 💡 **Exemplo de Uso**

```
Bem vindo ao gerenciador de boards, escolha a opção desejada:
1 - Criar um novo board
2 - Exibir todos os boards
3 - Selecionar um board existente
4 - Excluir um board
0 - Sair

```
