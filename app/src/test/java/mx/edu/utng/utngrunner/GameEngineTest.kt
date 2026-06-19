package mx.edu.utng.utngrunner

import mx.edu.utng.utngrunner.domain.model.GameState
import mx.edu.utng.utngrunner.domain.model.GameStatus
import mx.edu.utng.utngrunner.domain.model.Obstacle
import mx.edu.utng.utngrunner.engine.aabbOverlap
import mx.edu.utng.utngrunner.engine.jumpPlayer
import mx.edu.utng.utngrunner.engine.updateGame
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GameEngineTest {

    @Test
    fun `jumpPlayer applies negative velocity when on ground`() {
        val state = GameState(status = GameStatus.RUNNING)
        assertTrue(state.player.isOnGround)
        val after = jumpPlayer(state)
        assertTrue(after.player.velocityY < 0f)
        assertFalse(after.player.isOnGround)
    }

    @Test
    fun `jumpPlayer does nothing when already in air`() {
        val state = GameState(status = GameStatus.RUNNING)
        val inAir = state.copy(player = state.player.copy(isOnGround = false, velocityY = -10f))
        val after = jumpPlayer(inAir)
        assertEquals(inAir.player.velocityY, after.player.velocityY)
    }

    @Test
    fun `aabbOverlap returns true for overlapping boxes`() {
        assertTrue(aabbOverlap(0f, 0f, 40f, 40f, 20f, 20f, 40f, 40f))
    }

    @Test
    fun `aabbOverlap returns false for non-overlapping boxes`() {
        assertFalse(aabbOverlap(0f, 0f, 40f, 40f, 100f, 100f, 40f, 40f))
    }

    @Test
    fun `updateGame increments score while running`() {
        val state = GameState(status = GameStatus.RUNNING, screenWidth = 400f, screenHeight = 400f, groundY = 280f)
        val after = updateGame(state, 1)
        assertTrue(after.score > state.score)
    }

    @Test
    fun `updateGame sets GAME_OVER when lives reach zero`() {
        val obs = Obstacle(x = 50f, y = 220f)
        val state = GameState(
            status = GameStatus.RUNNING,
            lives = 1,
            obstacles = listOf(obs),
            screenWidth = 400f, screenHeight = 400f, groundY = 280f
        )
        val after = updateGame(state, 1)
        assertEquals(GameStatus.GAME_OVER, after.status)
        assertEquals(0, after.lives)
    }

    @Test
    fun `updateGame keeps IDLE state unchanged`() {
        val state = GameState(status = GameStatus.IDLE)
        val after = updateGame(state, 1)
        assertEquals(GameStatus.IDLE, after.status)
    }
}
