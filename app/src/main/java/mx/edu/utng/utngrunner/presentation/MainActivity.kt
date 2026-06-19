package mx.edu.utng.utngrunner.presentation

import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import mx.edu.utng.utngrunner.domain.model.GameStatus
import mx.edu.utng.utngrunner.presentation.theme.UTNGRunnerTheme

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: GameViewModel

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this, GameViewModelFactory(this))[GameViewModel::class.java]

        val vibrator = getSystemService(Vibrator::class.java)

        lifecycleScope.launch {
            viewModel.gameState
                .map { it.status }
                .distinctUntilChanged()
                .collect { status ->
                    if (status == GameStatus.GAME_OVER) {
                        vibrator?.vibrate(
                            VibrationEffect.createWaveform(
                                longArrayOf(0, 100, 100, 100), -1
                            )
                        )
                    }
                }
        }

        setContent {
            UTNGRunnerTheme {
                GameScreen(viewModel = viewModel)
            }
        }
    }
}
