package mx.edu.utng.utngrunner.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import mx.edu.utng.utngrunner.data.PreferencesDataSource
import mx.edu.utng.utngrunner.data.ScoreRepositoryImpl
import mx.edu.utng.utngrunner.domain.usecase.GetHighScoreUseCase
import mx.edu.utng.utngrunner.domain.usecase.SaveHighScoreUseCase

class GameViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val dataSource = PreferencesDataSource(context.applicationContext)
        val repository = ScoreRepositoryImpl(dataSource)
        val getHighScore = GetHighScoreUseCase(repository)
        val saveHighScore = SaveHighScoreUseCase(repository)
        @Suppress("UNCHECKED_CAST")
        return GameViewModel(getHighScore, saveHighScore) as T
    }
}
