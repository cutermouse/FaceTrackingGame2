package com.example.facetrackinggame

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ScoreDao {
    @Insert
    suspend fun insertScore(score: Score)

    @Query("SELECT * FROM scores ORDER BY value DESC LIMIT 100")
    suspend fun getTopScores(): List<Score>
}
