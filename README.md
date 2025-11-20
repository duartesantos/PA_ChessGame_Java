# ♟️ PA_ChessGame_Java: Jogo de Xadrez em JavaFX

Este projeto foi desenvolvido como Trabalho Prático da unidade curricular de **Programação Avançada (PA)** da Licenciatura em **Engenharia Informática** do ISEC, Universidade de Coimbra, no ano letivo 2024/2025.

O objetivo foi criar uma implementação completa e robusta de um jogo de xadrez, focando na aplicação rigorosa de padrões de design e na separação clara de responsabilidades.

## ✨ Funcionalidades Implementadas

O jogo de xadrez possui um conjunto robusto de funcionalidades, incluindo a lógica completa do jogo e recursos de acessibilidade e aprendizagem:

* **Regras de Xadrez Completas:** Implementação dos movimentos básicos (*mover/capturar*) e de todos os movimentos especiais: **Roque**, **En Passant** e **Promoção de Peões**.
* **Gestão de Estado de Jogo:** Verificação de **Xeque**, **Xeque-Mate** e **Empate** (*Stalemate*).
* **Modo de Aprendizagem:** Funcionalidades de **Undo/Redo** e visualização dos **movimentos possíveis** de cada peça.
* **Persistência de Dados:** Capacidade de **Exportar** e **Importar** o estado completo do jogo (`.dat` files) através de **Serialização**.
* **Acessibilidade/Feedback:** **Feedback sonoro** para movimentos e capturas, aumentando a acessibilidade.
* **Interface Gráfica (GUI):** Implementação completa da interface usando **JavaFX**.

## 💻 Destaques Técnicos e Padrões de Design

O valor deste projeto para o seu portfólio reside na forte aplicação de **Padrões de Design**, demonstrando competência em arquitetura de *software* modular e escalável.

| Padrão de Design | Classes Envolvidas | Descrição e Aplicação |
| :--- | :--- | :--- |
| **Model-View-Controller (MVC)** | `ChessGame` (Model), `BoardView` (View), `RootPane` (Controller) | Separação estrita entre a lógica do jogo (`Model`) e a interface gráfica (`View`/`Controller`), garantindo um código limpo e de fácil manutenção. |
| **Facade** | `ChessGameManager` | Atua como uma *interface* simples e segura para a complexidade interna da lógica do jogo (`ChessGame`), protegendo os componentes externos de aceitar ou retornar objetos internos. |
| **Observer** | `PropertyChangeSupport`, `BoardView`, `RootPane` | Utilizado para notificar automaticamente os componentes da interface (UI) sobre alterações no modelo, garantindo que a *view* se atualize em tempo real após cada jogada. |
| **Command** | `MoveCommand`, `CommandManager` | Essencial para implementar as funcionalidades de **Undo/Redo**, encapsulando cada jogada numa classe (`MoveCommand`) e gerindo o histórico através de uma *stack* (`CommandManager`). |
| **Factory Method** | `PieceFactory` | Utilizado para criar peças de forma simples e flexível, suportando a criação por tipo de peça (ex: promoção de peões) e por representação textual (*string*) para importação de jogos. |
| **Singleton** | `ModelLog`, `SoundManager`, `ImageManager` | Garante que apenas existe uma única instância de classes de gestão (Logs, Sons e Imagens), controlando o acesso global a recursos críticos. |

## 🛠️ Tecnologias Utilizadas

* **Linguagem:** Java
* **Interface Gráfica:** JavaFX
* **Padrões de Projeto:** MVC, Facade, Observer, Command, Factory Method, Singleton.

## ⚙️ Como Correr o Projeto

*(Aqui você deve adicionar instruções simples, como por exemplo:*

1.  *Fazer o clone do repositório (`git clone ...`).*
2.  *Abrir o projeto no **IntelliJ IDEA** (ou IDE equivalente).*
3.  *Garantir que o SDK **Java (versão X)** está configurado.*
4.  *Executar a classe principal **`ChessMain`**.*

*)
