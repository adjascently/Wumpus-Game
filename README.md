# 🐗 Hunt the Wumpus - Java Game

This is a Java-based implementation of the classic game **Hunt the Wumpus**, developed using Java Swing. The game is played on a 5x5 grid maze filled with various hazards such as the Wumpus, bottomless pits, and superbats.

## 🎮 Game Objective

Navigate through the maze and hunt the Wumpus without falling into a pit or being teleported by bats. Use arrows wisely and sense danger using sensory clues like smell, draft, and rustling.

---

## 🧩 Features

- 5x5 grid-based maze
- Interactive GUI built with Java Swing
- Sensory clues:
  - **Smell**: indicates nearby Wumpus
  - **Draft**: indicates nearby pit
  - **Rustle**: indicates nearby bats
- Arrow shooting with direction and range
- Superbat teleportation to a random **safe** cave
- Reveals all hazards when the player loses
- Pop-up messages for win/lose events
- Keyboard controls:
  - Arrow keys for movement
  - `Space + Arrow key` for shooting

---

## 🛠️ Setup Instructions

### Prerequisites
- Java 17 or later
- IntelliJ IDEA or any Java IDE

## 🧪 Testing
- JUnit tests included for key game logic.
- Tests cover player movement, arrow logic, hazard placement, and unwinnable scenarios.

  ## Project Structure 
Wumpus-Game/
├── model/             # Game logic and classes (Maze, Player, Cave, etc.)
├── view/              # Java Swing GUI components
├── controller/        # User input and game flow management
├── Main.java          # Main class to run the game
└── test/              # JUnit test cases
