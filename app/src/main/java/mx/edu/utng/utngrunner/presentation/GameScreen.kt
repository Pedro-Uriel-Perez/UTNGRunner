package mx.edu.utng.utngrunner.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material3.Text
import mx.edu.utng.utngrunner.domain.model.GameStatus

private val BG = Color(0xFF1A1A2E)
private val GROUND = Color(0xFF16213E)
private val PLAYER = Color(0xFF0F3460)
private val ACCENT = Color(0xFFE94560)
private val COIN_COLOR = Color(0xFFF5A623)
private val TEXT_DIM = Color(0xFFAAAAAA)

@Composable
fun GameScreen(viewModel: GameViewModel) {
    val state by viewModel.gameState.collectAsState()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        try { focusRequester.requestFocus() } catch (_: Exception) {}
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .focusRequester(focusRequester)
            .focusable()
            .onRotaryScrollEvent { _ ->
                when (state.status) {
                    GameStatus.IDLE -> Unit
                    GameStatus.RUNNING -> viewModel.jump()
                    GameStatus.GAME_OVER -> viewModel.resetGame()
                }
                true
            }
            .onGloballyPositioned { coords ->
                val w = coords.size.width.toFloat()
                val h = coords.size.height.toFloat()
                if (w > 0f && state.status == GameStatus.IDLE) {
                    viewModel.startGame(w, h)
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val groundY = if (state.groundY > 0f) state.groundY else h * 0.72f

            drawRect(color = BG, size = Size(w, h))
            drawRect(color = GROUND, topLeft = Offset(0f, groundY), size = Size(w, h - groundY))

            if (state.status == GameStatus.RUNNING) {
                val p = state.player
                drawRoundRect(
                    color = PLAYER,
                    topLeft = Offset(p.x, p.y),
                    size = Size(p.width, p.height),
                    cornerRadius = CornerRadius(8f, 8f)
                )
                drawRect(
                    color = ACCENT,
                    topLeft = Offset(p.x + 6f, p.y + 6f),
                    size = Size(p.width - 12f, 8f)
                )
                state.obstacles.forEach { obs ->
                    drawRect(
                        color = ACCENT,
                        topLeft = Offset(obs.x, obs.y),
                        size = Size(obs.width, obs.height)
                    )
                }
                state.coins.forEach { coin ->
                    drawCircle(color = COIN_COLOR, radius = coin.radius, center = Offset(coin.x, coin.y))
                }
            }
        }

        when (state.status) {
            GameStatus.IDLE -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("UTNG Runner", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text("Gira la corona para", color = TEXT_DIM, fontSize = 13.sp)
                    Text("iniciar el juego", color = TEXT_DIM, fontSize = 13.sp)
                    Spacer(Modifier.height(6.dp))
                    Text("Record: ${state.highScore}", color = TEXT_DIM, fontSize = 13.sp)
                }
            }
            GameStatus.RUNNING -> {
                Text(
                    "Pts: ${state.score}",
                    color = Color.White,
                    fontSize = 13.sp,
                    modifier = Modifier.align(Alignment.TopStart).padding(8.dp)
                )
                Text(
                    "❤".repeat(state.lives.coerceAtLeast(0)),
                    color = ACCENT,
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                )
            }
            GameStatus.GAME_OVER -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Game Over", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text("Puntos: ${state.score}", color = TEXT_DIM, fontSize = 13.sp)
                    Text("Record: ${state.highScore}", color = TEXT_DIM, fontSize = 13.sp)
                    Spacer(Modifier.height(4.dp))
                    Text("Gira para reiniciar", color = TEXT_DIM, fontSize = 13.sp)
                }
            }
        }
    }
}
