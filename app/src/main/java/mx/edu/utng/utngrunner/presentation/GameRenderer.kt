package mx.edu.utng.utngrunner.presentation

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import mx.edu.utng.utngrunner.domain.model.GameState
import mx.edu.utng.utngrunner.domain.model.GameStatus

class GameRenderer {

    private val bgPaint = Paint().apply { color = Color.parseColor("#1A1A2E") }
    private val groundPaint = Paint().apply { color = Color.parseColor("#16213E") }
    private val playerPaint = Paint().apply { color = Color.parseColor("#0F3460") }
    private val playerAccentPaint = Paint().apply { color = Color.parseColor("#E94560") }
    private val obstaclePaint = Paint().apply { color = Color.parseColor("#E94560") }
    private val coinPaint = Paint().apply { color = Color.parseColor("#F5A623"); isAntiAlias = true }

    private val scorePaint = Paint().apply {
        color = Color.WHITE
        textSize = 28f
        isAntiAlias = true
    }
    private val titlePaint = Paint().apply {
        color = Color.WHITE
        textSize = 32f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }
    private val subPaint = Paint().apply {
        color = Color.parseColor("#AAAAAA")
        textSize = 22f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }
    private val heartPaint = Paint().apply {
        color = Color.parseColor("#E94560")
        textSize = 22f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    fun render(canvas: Canvas, state: GameState) {
        val w = state.screenWidth
        val h = state.screenHeight

        canvas.drawRect(0f, 0f, w, h, bgPaint)
        canvas.drawRect(0f, state.groundY, w, h, groundPaint)

        when (state.status) {
            GameStatus.IDLE -> drawIdleScreen(canvas, state)
            GameStatus.RUNNING -> drawRunningScreen(canvas, state)
            GameStatus.GAME_OVER -> drawGameOverScreen(canvas, state)
        }
    }

    private fun drawIdleScreen(canvas: Canvas, state: GameState) {
        val cx = state.screenWidth / 2f
        val cy = state.screenHeight / 2f
        canvas.drawText("UTNG Runner", cx, cy - 40f, titlePaint)
        canvas.drawText("Gira la corona para", cx, cy, subPaint)
        canvas.drawText("iniciar el juego", cx, cy + 28f, subPaint)
        canvas.drawText("Record: ${state.highScore}", cx, cy + 64f, subPaint)
    }

    private fun drawRunningScreen(canvas: Canvas, state: GameState) {
        val p = state.player
        val playerRect = RectF(p.x, p.y, p.x + p.width, p.y + p.height)
        canvas.drawRoundRect(playerRect, 8f, 8f, playerPaint)
        canvas.drawRect(p.x + 6f, p.y + 6f, p.x + p.width - 6f, p.y + 14f, playerAccentPaint)

        state.obstacles.forEach { obs ->
            canvas.drawRect(obs.x, obs.y, obs.x + obs.width, obs.y + obs.height, obstaclePaint)
        }

        state.coins.forEach { coin ->
            canvas.drawCircle(coin.x, coin.y, coin.radius, coinPaint)
        }

        canvas.drawText("Pts: ${state.score}", 10f, 30f, scorePaint)
        canvas.drawText("Vidas: ${"❤".repeat(state.lives.coerceAtLeast(0))}", 10f, 58f, scorePaint)
        if (state.heartRate > 0) {
            canvas.drawText("FC: ${state.heartRate} bpm", state.screenWidth / 2f, 30f, heartPaint)
        }
    }

    private fun drawGameOverScreen(canvas: Canvas, state: GameState) {
        val cx = state.screenWidth / 2f
        val cy = state.screenHeight / 2f
        canvas.drawText("Game Over", cx, cy - 50f, titlePaint)
        canvas.drawText("Puntos: ${state.score}", cx, cy - 10f, subPaint)
        canvas.drawText("Record: ${state.highScore}", cx, cy + 24f, subPaint)
        canvas.drawText("Gira para reiniciar", cx, cy + 60f, subPaint)
    }
}
