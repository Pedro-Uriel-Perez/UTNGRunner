package mx.edu.utng.utngrunner.domain.usecase

import mx.edu.utng.utngrunner.domain.repository.ScoreRepository

class SaveHighScoreUseCase(private val repository: ScoreRepository) {
    suspend operator fun invoke(score: Int) = repository.saveHighScore(score)
}
