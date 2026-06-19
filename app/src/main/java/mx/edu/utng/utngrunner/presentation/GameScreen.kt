package mx.edu.utng.utngrunner.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import mx.edu.utng.utngrunner.domain.model.GameStatus

@Composable
fun GameScreen(viewModel: GameViewModel) {
    val state by viewModel.gameState.collectAsState()
    val renderer = remember { GameRenderer() }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .focusRequester(focusRequester)
            .focusable()
            .onRotaryScrollEvent { event ->
                when (state.status) {
                    GameStatus.IDLE -> { /* started by startGame below */ }
                    GameStatus.RUNNING -> viewModel.jump()
                    GameStatus.GAME_OVER -> viewModel.resetGame()
                }
                true
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            if (state.status == GameStatus.IDLE && w > 0f && h > 0f) {
                viewModel.startGame(w, h)
            }

            drawIntoCanvas { canvas ->
                renderer.render(canvas.nativeCanvas, state)
            }
        }
    }
}
