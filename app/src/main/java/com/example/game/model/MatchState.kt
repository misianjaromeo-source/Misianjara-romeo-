package com.example.game.model

enum class MatchPhase {
    KICKOFF,
    PLAYING,
    GOAL_SCORED,
    CORNER_KICK,
    THROW_IN,
    GOAL_KICK,
    HALF_TIME,
    FULL_TIME
}

enum class CameraMode {
    BROADCAST_3D,
    DYNAMIC_TV,
    TACTICAL_TOP,
    PLAYER_FOCUS
}

data class MatchStats(
    var homePossessionPercent: Int = 54,
    var homeShots: Int = 0,
    var homeShotsOnTarget: Int = 0,
    var homePassesCompleted: Int = 0,
    var homeCorners: Int = 0,
    var homeFouls: Int = 0,
    var awayShots: Int = 0,
    var awayShotsOnTarget: Int = 0,
    var awayPassesCompleted: Int = 0,
    var awayCorners: Int = 0,
    var awayFouls: Int = 0
)

data class MatchState(
    var phase: MatchPhase = MatchPhase.KICKOFF,
    var homeScore: Int = 0,
    var awayScore: Int = 0,
    var homeTeamName: String = "JFLY FC",
    var awayTeamName: String = "Paris Elite",
    var homeColor: Long = 0xFF111111,
    var awayColor: Long = 0xFF0A2240,
    var matchSeconds: Float = 0f, // 0..5400 (representing 0' to 90')
    var matchSpeedMultiplier: Float = 12.0f, // 90 min match takes ~450s (7.5 min)
    var commentaryText: String = "Coup d'envoi du grand match 11 vs 11 !",
    var commentaryTimer: Float = 4.0f,
    var goalScorerText: String? = null,
    var goalCelebrationTimer: Float = 0f,
    var isPaused: Boolean = false,
    var difficultyLevel: String = "Semi-Pro", // Amateur, Semi-Pro, Pro, Légende
    var cameraMode: CameraMode = CameraMode.BROADCAST_3D,
    var stats: MatchStats = MatchStats()
) {
    val displayMinutes: Int get() = (matchSeconds / 60).toInt().coerceAtMost(90)
    val displayExtraTime: String get() = if (matchSeconds > 5400) "+${((matchSeconds - 5400)/30).toInt() + 1}'" else ""
}
