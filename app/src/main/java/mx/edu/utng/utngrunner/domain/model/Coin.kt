package mx.edu.utng.utngrunner.domain.model

data class Coin(
    val x: Float,
    val y: Float,
    val radius: Float = 15f,
    val collected: Boolean = false
)
