package mx.edu.utng.utngrunner.engine

import mx.edu.utng.utngrunner.domain.model.Coin
import mx.edu.utng.utngrunner.domain.model.GameState
import mx.edu.utng.utngrunner.domain.model.GameStatus
import mx.edu.utng.utngrunner.domain.model.Obstacle
import mx.edu.utng.utngrunner.domain.model.Player
import kotlin.random.Random

private const val GRAVITY = 1.5f
private const val JUMP_VELOCITY = -18f
private const val OBSTACLE_SPEED = 6f
private const val COIN_SPEED = 5f
private const val SPAWN_INTERVAL = 60

fun updateGame(state: GameState, frameCount: Int): GameState {
    if (state.status != GameStatus.RUNNING) return state

    val player = updatePlayer(state.player, state.groundY)
    val obstacles = updateObstacles(state.obstacles, state.screenWidth, frameCount)
    val coins = updateCoins(state.coins, state.screenWidth, frameCount)

    val (coinsAfterCollect, coinsCollected) = collectCoins(player, coins)
    val newScore = state.score + coinsCollected * 10 + 1

    val hitObstacle = checkObstacleCollisions(player, obstacles)
    val newLives = if (hitObstacle) state.lives - 1 else state.lives
    val obstaclesAfterHit = if (hitObstacle) obstacles.drop(1) else obstacles

    val status = if (newLives <= 0) GameStatus.GAME_OVER else GameStatus.RUNNING
    val newHighScore = maxOf(state.highScore, newScore)

    return state.copy(
        player = player,
        obstacles = obstaclesAfterHit,
        coins = coinsAfterCollect,
        score = newScore,
        highScore = newHighScore,
        lives = newLives,
        status = status
    )
}

fun jumpPlayer(state: GameState): GameState {
    if (!state.player.isOnGround || state.status != GameStatus.RUNNING) return state
    return state.copy(player = state.player.copy(velocityY = JUMP_VELOCITY, isOnGround = false))
}

private fun updatePlayer(player: Player, groundY: Float): Player {
    val newVelocityY = player.velocityY + GRAVITY
    val newY = player.y + newVelocityY
    return if (newY >= groundY - player.height) {
        player.copy(y = groundY - player.height, velocityY = 0f, isOnGround = true)
    } else {
        player.copy(y = newY, velocityY = newVelocityY, isOnGround = false)
    }
}

private fun updateObstacles(obstacles: List<Obstacle>, screenWidth: Float, frame: Int): List<Obstacle> {
    val moved = obstacles.map { it.copy(x = it.x - OBSTACLE_SPEED) }.filter { it.x + it.width > 0 }
    return if (frame % SPAWN_INTERVAL == 0) {
        moved + Obstacle(x = screenWidth, y = 230f)
    } else moved
}

private fun updateCoins(coins: List<Coin>, screenWidth: Float, frame: Int): List<Coin> {
    val moved = coins.map { it.copy(x = it.x - COIN_SPEED) }.filter { it.x + it.radius > 0 && !it.collected }
    return if (frame % (SPAWN_INTERVAL / 2) == 0 && Random.nextFloat() > 0.4f) {
        moved + Coin(x = screenWidth, y = Random.nextFloat() * 100f + 120f)
    } else moved
}

private fun collectCoins(player: Player, coins: List<Coin>): Pair<List<Coin>, Int> {
    var collected = 0
    val updated = coins.map { coin ->
        if (!coin.collected && aabbOverlapsCircle(player, coin)) {
            collected++
            coin.copy(collected = true)
        } else coin
    }.filter { !it.collected }
    return updated to collected
}

private fun checkObstacleCollisions(player: Player, obstacles: List<Obstacle>): Boolean =
    obstacles.any { obs ->
        aabbOverlap(
            ax = player.x, ay = player.y, aw = player.width, ah = player.height,
            bx = obs.x, by = obs.y, bw = obs.width, bh = obs.height
        )
    }

fun aabbOverlap(ax: Float, ay: Float, aw: Float, ah: Float,
                bx: Float, by: Float, bw: Float, bh: Float): Boolean =
    ax < bx + bw && ax + aw > bx && ay < by + bh && ay + ah > by

private fun aabbOverlapsCircle(player: Player, coin: Coin): Boolean {
    val closestX = coin.x.coerceIn(player.x, player.x + player.width)
    val closestY = coin.y.coerceIn(player.y, player.y + player.height)
    val dx = coin.x - closestX
    val dy = coin.y - closestY
    return dx * dx + dy * dy < coin.radius * coin.radius
}
