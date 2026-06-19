package mx.edu.utng.utngrunner.domain.usecase

import kotlinx.coroutines.flow.Flow
import mx.edu.utng.utngrunner.domain.repository.ScoreRepository

class GetHighScoreUseCase(private val repository: ScoreRepository) {
    operator fun invoke(): Flow<Int> = repository.getHighScore()
}
