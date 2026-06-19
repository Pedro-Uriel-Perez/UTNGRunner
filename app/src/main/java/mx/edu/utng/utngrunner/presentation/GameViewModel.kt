package mx.edu.utng.utngrunner.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.edu.utng.utngrunner.domain.model.GameState
import mx.edu.utng.utngrunner.domain.model.GameStatus
import mx.edu.utng.utngrunner.domain.usecase.GetHighScoreUseCase
import mx.edu.utng.utngrunner.domain.usecase.SaveHighScoreUseCase
import mx.edu.utng.utngrunner.engine.jumpPlayer
import mx.edu.utng.utngrunner.engine.updateGame

class GameViewModel(
    private val getHighScoreUseCase: GetHighScoreUseCase,
    private val saveHighScoreUseCase: SaveHighScoreUseCase
) : ViewModel() {

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private var frameCount = 0
    private var gameLoopJob: Job? = null

    init {
        viewModelScope.launch {
            getHighScoreUseCase().collect { highScore ->
                _gameState.value = _gameState.value.copy(highScore = highScore)
            }
        }
    }

    fun startGame(screenWidth: Float, screenHeight: Float) {
        gameLoopJob?.cancel()
        frameCount = 0
        _gameState.value = GameState(
            status = GameStatus.RUNNING,
            screenWidth = screenWidth,
            screenHeight = screenHeight,
            groundY = screenHeight * 0.72f,
            highScore = _gameState.value.highScore
        )
        gameLoopJob = viewModelScope.launch(Dispatchers.Default) {
            while (_gameState.value.status == GameStatus.RUNNING) {
                frameCount++
                _gameState.value = updateGame(_gameState.value, frameCount)
                delay(16L)
            }
            if (_gameState.value.score > _gameState.value.highScore) {
                saveHighScoreUseCase(_gameState.value.score)
            }
        }
    }

    fun jump() {
        _gameState.value = jumpPlayer(_gameState.value)
    }

    fun updateHeartRate(bpm: Int) {
        _gameState.value = _gameState.value.copy(heartRate = bpm)
    }

    fun resetGame() {
        _gameState.value = GameState(highScore = _gameState.value.highScore)
    }
}
