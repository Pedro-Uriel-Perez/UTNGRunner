package mx.edu.utng.utngrunner.domain.model

enum class GameStatus { IDLE, RUNNING, GAME_OVER }

data class GameState(
    val player: Player = Player(),
    val obstacles: List<Obstacle> = emptyList(),
    val coins: List<Coin> = emptyList(),
    val score: Int = 0,
    val highScore: Int = 0,
    val lives: Int = 3,
    val heartRate: Int = 0,
    val status: GameStatus = GameStatus.IDLE,
    val screenWidth: Float = 400f,
    val screenHeight: Float = 400f,
    val groundY: Float = 280f
)
