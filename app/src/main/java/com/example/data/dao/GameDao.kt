package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.entity.CareerEntity
import com.example.data.entity.MatchHistoryEntity
import com.example.data.entity.PlayerEntity
import com.example.data.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Query("SELECT * FROM user_profile WHERE id = 'player_user' LIMIT 1")
    fun getUserProfileFlow(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)

    @Query("SELECT * FROM squad_players ORDER BY isStarter DESC, number ASC")
    fun getAllSquadPlayersFlow(): Flow<List<PlayerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSquadPlayers(players: List<PlayerEntity>)

    @Update
    suspend fun updatePlayer(player: PlayerEntity)

    @Query("SELECT * FROM match_history ORDER BY timestamp DESC LIMIT 20")
    fun getMatchHistoryFlow(): Flow<List<MatchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatchHistory(match: MatchHistoryEntity)

    @Query("SELECT * FROM career_progress WHERE id = 'career_main' LIMIT 1")
    fun getCareerProgressFlow(): Flow<CareerEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCareerProgress(career: CareerEntity)
}
