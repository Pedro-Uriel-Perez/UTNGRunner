package mx.edu.utng.utngrunner.domain.repository

import kotlinx.coroutines.flow.Flow

interface ScoreRepository {
    fun getHighScore(): Flow<Int>
    suspend fun saveHighScore(score: Int)
}
