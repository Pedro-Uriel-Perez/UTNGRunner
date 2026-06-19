package mx.edu.utng.utngrunner.data

import kotlinx.coroutines.flow.Flow
import mx.edu.utng.utngrunner.domain.repository.ScoreRepository

class ScoreRepositoryImpl(private val dataSource: PreferencesDataSource) : ScoreRepository {

    override fun getHighScore(): Flow<Int> = dataSource.getHighScore()

    override suspend fun saveHighScore(score: Int) = dataSource.saveHighScore(score)
}
