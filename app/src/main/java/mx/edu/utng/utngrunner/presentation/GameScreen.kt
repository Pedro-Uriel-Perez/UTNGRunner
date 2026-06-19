package mx.edu.utng.utngrunner.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.ui.platform.LocalDensity
import mx.edu.utng.utngrunner.domain.model.GameStatus

@Composable
fun GameScreen(viewModel: GameViewModel) {
    val state by viewModel.gameState.collectAsState()
    val renderer = remember { GameRenderer() }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val screenWidth = with(density) { maxWidth.toPx() }
        val screenHeight = with(density) { maxHeight.toPx() }

        LaunchedEffect(screenWidth, screenHeight) {
            if (screenWidth > 0f && screenHeight > 0f && state.status == GameStatus.IDLE) {
                viewModel.startGame(screenWidth, screenHeight)
            }
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .focusRequester(focusRequester)
                .focusable()
                .onRotaryScrollEvent { _ ->
                    when (state.status) {
                        GameStatus.IDLE -> viewModel.startGame(screenWidth, screenHeight)
                        GameStatus.RUNNING -> viewModel.jump()
                        GameStatus.GAME_OVER -> viewModel.resetGame()
                    }
                    true
                }
        ) {
            drawIntoCanvas { canvas ->
                renderer.render(canvas.nativeCanvas, state)
            }
        }
    }
}
