package mx.edu.utng.utngrunner.domain.model

data class Player(
    val x: Float = 50f,
    val y: Float = 200f,
    val width: Float = 40f,
    val height: Float = 40f,
    val velocityY: Float = 0f,
    val isOnGround: Boolean = true
)
