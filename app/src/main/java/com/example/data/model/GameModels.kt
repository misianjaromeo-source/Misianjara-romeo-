package com.example.data.model

enum class PositionType {
    GK, CB, LB, RB, CDM, CM, CAM, LW, RW, ST
}

enum class FormationType(val displayName: String, val positions: List<PositionType>) {
    F_4_3_3("4-3-3 Attaque", listOf(
        PositionType.GK,
        PositionType.LB, PositionType.CB, PositionType.CB, PositionType.RB,
        PositionType.CDM, PositionType.CM, PositionType.CAM,
        PositionType.LW, PositionType.ST, PositionType.RW
    )),
    F_4_4_2("4-4-2 Équilibré", listOf(
        PositionType.GK,
        PositionType.LB, PositionType.CB, PositionType.CB, PositionType.RB,
        PositionType.LW, PositionType.CM, PositionType.CM, PositionType.RW,
        PositionType.ST, PositionType.ST
    )),
    F_3_5_2("3-5-2 Offensif", listOf(
        PositionType.GK,
        PositionType.CB, PositionType.CB, PositionType.CB,
        PositionType.LW, PositionType.CDM, PositionType.CM, PositionType.CAM, PositionType.RW,
        PositionType.ST, PositionType.ST
    )),
    F_4_2_3_1("4-2-3-1 Contrôle", listOf(
        PositionType.GK,
        PositionType.LB, PositionType.CB, PositionType.CB, PositionType.RB,
        PositionType.CDM, PositionType.CDM,
        PositionType.CAM, PositionType.LW, PositionType.RW,
        PositionType.ST
    ))
}

data class PlayerData(
    val id: String,
    val name: String,
    val number: Int,
    val position: PositionType,
    val ovr: Int,
    val pace: Int,
    val shooting: Int,
    val passing: Int,
    val dribbling: Int,
    val defending: Int,
    val physical: Int,
    val isStarter: Boolean = true,
    val isCaptain: Boolean = false,
    val faceStyle: Int = 1,
    val skinToneHex: Long = 0xFFD89B72,
    val hairColorHex: Long = 0xFF1C140E
)

data class TeamData(
    val id: String,
    val name: String,
    val shortName: String,
    val primaryColor: Long,
    val secondaryColor: Long,
    val rating: Int,
    val league: String = "Ligue Élite",
    val formation: FormationType = FormationType.F_4_3_3,
    val players: List<PlayerData> = emptyList()
)

data class UserProfileData(
    val id: String = "player_user",
    val playerName: String = "Capitaine JFLY",
    val level: Int = 12,
    val currentXp: Int = 2450,
    val targetXp: Int = 3000,
    val xp: Int = 2450,
    val coins: Int = 18500,
    val diamonds: Int = 320,
    val matchesPlayed: Int = 48,
    val matchesWon: Int = 38,
    val goalsScored: Int = 114,
    val trophiesCount: Int = 7,
    val trophies: Int = 7,
    val selectedKitId: String = "kit_jfly_gold",
    val selectedBallId: String = "ball_gold_crown",
    val selectedStadiumId: String = "stadium_grand_arena"
)

data class LeagueClubData(
    val clubId: String,
    val clubName: String,
    val played: Int,
    val won: Int,
    val drawn: Int,
    val lost: Int,
    val goalsFor: Int,
    val goalsAgainst: Int,
    val goalDiff: Int,
    val points: Int
)

data class ChallengeData(
    val id: String,
    val title: String,
    val description: String,
    val rewardCoins: Int,
    val rewardDiamonds: Int,
    val progress: Int,
    val target: Int,
    val isCompleted: Boolean = false,
    val isClaimed: Boolean = false
)

data class ShopItemData(
    val id: String,
    val name: String,
    val description: String,
    val priceCoins: Int = 0,
    val priceDiamonds: Int = 0,
    val currency: String = "COINS", // "COINS" or "DIAMONDS"
    val isOwned: Boolean = false,
    val category: String = "PACKS"
)
