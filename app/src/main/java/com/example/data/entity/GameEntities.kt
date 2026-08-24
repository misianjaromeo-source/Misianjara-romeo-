package com.example.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: String = "player_user",
    val playerName: String = "Capitaine JFLY",
    val level: Int = 12,
    val currentXp: Int = 2450,
    val targetXp: Int = 3000,
    val coins: Int = 18500,
    val diamonds: Int = 320,
    val matchesPlayed: Int = 48,
    val matchesWon: Int = 38,
    val goalsScored: Int = 114,
    val trophiesCount: Int = 7,
    val selectedKitId: String = "kit_jfly_gold",
    val selectedBallId: String = "ball_gold_crown",
    val selectedStadiumId: String = "stadium_grand_arena"
)

@Entity(tableName = "squad_players")
data class PlayerEntity(
    @PrimaryKey val id: String,
    val name: String,
    val number: Int,
    val position: String,
    val ovr: Int,
    val pace: Int,
    val shooting: Int,
    val passing: Int,
    val dribbling: Int,
    val defending: Int,
    val physical: Int,
    val isStarter: Boolean,
    val isCaptain: Boolean
)

@Entity(tableName = "match_history")
data class MatchHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mode: String,
    val opponentName: String,
    val homeScore: Int,
    val awayScore: Int,
    val result: String,
    val timestamp: Long = System.currentTimeMillis(),
    val rewardCoins: Int,
    val rewardXp: Int
)

@Entity(tableName = "career_progress")
data class CareerEntity(
    @PrimaryKey val id: String = "career_main",
    val playerName: String = "JFLY KING",
    val playerOvr: Int = 86,
    val currentClub: String = "JFLY FC",
    val seasonNumber: Int = 1,
    val transferValue: Long = 45_000_000L,
    val weeklyWage: Int = 12_500,
    val managerTrust: Int = 92,
    val skillPoints: Int = 3,
    val matchesPlayed: Int = 24,
    val goalsScored: Int = 18,
    val assists: Int = 9,
    val trophiesWon: Int = 2
)
