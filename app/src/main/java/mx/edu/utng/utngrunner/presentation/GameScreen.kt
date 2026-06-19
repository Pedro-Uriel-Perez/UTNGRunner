package mx.edu.utng.utngrunner.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    var screenSize by remember { mutableStateOf(Pair(0f, 0f)) }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val sw = with(density) { maxWidth.toPx() }
        val sh = with(density) { maxHeight.toPx() }

        LaunchedEffect(sw, sh) {
            if (sw > 0f && sh > 0f) screenSize = Pair(sw, sh)
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .focusRequester(focusRequester)
                .focusable()
                .onRotaryScrollEvent { _ ->
                    val (w, h) = screenSize
                    when (state.status) {
                        GameStatus.IDLE -> if (w > 0f) viewModel.startGame(w, h)
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
