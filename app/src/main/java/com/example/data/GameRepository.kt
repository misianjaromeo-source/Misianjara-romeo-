package com.example.data

import com.example.data.dao.GameDao
import com.example.data.entity.CareerEntity
import com.example.data.entity.MatchHistoryEntity
import com.example.data.entity.PlayerEntity
import com.example.data.entity.UserProfileEntity
import com.example.data.model.ChallengeData
import com.example.data.model.FormationType
import com.example.data.model.LeagueClubData
import com.example.data.model.PlayerData
import com.example.data.model.PositionType
import com.example.data.model.ShopItemData
import com.example.data.model.TeamData
import com.example.data.model.UserProfileData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GameRepository(private val dao: GameDao) {
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _standings = MutableStateFlow<List<LeagueClubData>>(emptyList())
    val standings: StateFlow<List<LeagueClubData>> = _standings.asStateFlow()

    private val _challenges = MutableStateFlow<List<ChallengeData>>(emptyList())
    val challenges: StateFlow<List<ChallengeData>> = _challenges.asStateFlow()

    private val _shopItems = MutableStateFlow<List<ShopItemData>>(emptyList())
    val shopItems: StateFlow<List<ShopItemData>> = _shopItems.asStateFlow()

    val defaultTeams: List<TeamData> = listOf(
        TeamData("paris", "Paris Elite", "PAR", 0xFF0A2240, 0xFFE30613, 89, "Ligue Élite", FormationType.F_4_3_3),
        TeamData("madrid", "Madrid Royale", "RMA", 0xFFFFFFFF, 0xFF1A3B8B, 91, "Ligue Élite", FormationType.F_4_3_3),
        TeamData("manchester", "Manchester Sky", "MCY", 0xFF6CABDD, 0xFF1C2C5B, 90, "Ligue Élite", FormationType.F_4_3_3),
        TeamData("munich", "Munich Champions", "BAY", 0xFFDC052D, 0xFFFFFFFF, 88, "Ligue Élite", FormationType.F_4_2_3_1),
        TeamData("milan", "Milan Rossoneri", "MIL", 0xFFFB090B, 0xFF000000, 87, "Ligue Élite", FormationType.F_4_4_2),
        TeamData("london", "London Gunners", "ARS", 0xFFEF0107, 0xFFFFFFFF, 88, "Ligue Élite", FormationType.F_4_3_3),
        TeamData("barca", "Catalunya Blau", "FCB", 0xFF004D98, 0xFFA50044, 89, "Ligue Élite", FormationType.F_4_3_3),
        TeamData("turin", "Turin Zebras", "JUV", 0xFF000000, 0xFFFFFFFF, 86, "Ligue Élite", FormationType.F_3_5_2)
    )

    val userProfile: StateFlow<UserProfileData> = dao.getUserProfileFlow().map { entity ->
        if (entity != null) {
            UserProfileData(
                id = entity.id,
                playerName = entity.playerName,
                level = entity.level,
                currentXp = entity.currentXp,
                targetXp = entity.targetXp,
                xp = entity.currentXp,
                coins = entity.coins,
                diamonds = entity.diamonds,
                matchesPlayed = entity.matchesPlayed,
                matchesWon = entity.matchesWon,
                goalsScored = entity.goalsScored,
                trophiesCount = entity.trophiesCount,
                trophies = entity.trophiesCount,
                selectedKitId = entity.selectedKitId,
                selectedBallId = entity.selectedBallId,
                selectedStadiumId = entity.selectedStadiumId
            )
        } else {
            UserProfileData()
        }
    }.stateIn(scope, SharingStarted.Eagerly, UserProfileData())

    val squad: StateFlow<List<PlayerData>> = dao.getAllSquadPlayersFlow().map { list ->
        list.map { entity ->
            PlayerData(
                id = entity.id,
                name = entity.name,
                number = entity.number,
                position = try { PositionType.valueOf(entity.position) } catch (e: Exception) { PositionType.CM },
                ovr = entity.ovr,
                pace = entity.pace,
                shooting = entity.shooting,
                passing = entity.passing,
                dribbling = entity.dribbling,
                defending = entity.defending,
                physical = entity.physical,
                isStarter = entity.isStarter,
                isCaptain = entity.isCaptain
            )
        }
    }.stateIn(scope, SharingStarted.Eagerly, emptyList())

    val career: StateFlow<CareerEntity?> = dao.getCareerProgressFlow()
        .stateIn(scope, SharingStarted.Eagerly, null)

    init {
        scope.launch {
            initDefaultData()
            initStandings()
            initChallenges()
            initShop()
        }
    }

    private suspend fun initDefaultData() {
        val profile = dao.getUserProfileFlow().firstOrNull()
        if (profile == null) {
            dao.insertOrUpdateProfile(UserProfileEntity())
        }

        val players = dao.getAllSquadPlayersFlow().firstOrNull()
        if (players.isNullOrEmpty()) {
            val starterSquad = listOf(
                PlayerEntity("jfly_1", "L. Falcon", 1, PositionType.GK.name, 88, 70, 40, 75, 65, 90, 85, isStarter = true, isCaptain = false),
                PlayerEntity("jfly_2", "D. Silva", 2, PositionType.RB.name, 85, 87, 65, 80, 82, 84, 80, isStarter = true, isCaptain = false),
                PlayerEntity("jfly_3", "M. Ramos", 4, PositionType.CB.name, 89, 78, 68, 74, 76, 92, 90, isStarter = true, isCaptain = false),
                PlayerEntity("jfly_4", "V. Van Dijk", 5, PositionType.CB.name, 90, 80, 60, 78, 75, 93, 91, isStarter = true, isCaptain = false),
                PlayerEntity("jfly_5", "T. Hernandez", 3, PositionType.LB.name, 86, 91, 70, 81, 84, 82, 83, isStarter = true, isCaptain = false),
                PlayerEntity("jfly_6", "K. De Bruyne", 17, PositionType.CM.name, 91, 76, 88, 95, 87, 72, 78, isStarter = true, isCaptain = false),
                PlayerEntity("jfly_7", "N. Kante", 7, PositionType.CDM.name, 88, 82, 66, 82, 81, 91, 89, isStarter = true, isCaptain = false),
                PlayerEntity("jfly_8", "J. Musiala", 8, PositionType.CAM.name, 87, 89, 82, 85, 92, 65, 70, isStarter = true, isCaptain = false),
                PlayerEntity("jfly_9", "V. Junior", 11, PositionType.LW.name, 90, 95, 84, 82, 94, 45, 74, isStarter = true, isCaptain = false),
                PlayerEntity("jfly_10", "JFLY KING", 10, PositionType.ST.name, 94, 94, 96, 89, 95, 52, 86, isStarter = true, isCaptain = true),
                PlayerEntity("jfly_11", "M. Salah", 19, PositionType.RW.name, 89, 92, 89, 84, 90, 48, 76, isStarter = true, isCaptain = false),
                // Substitutes
                PlayerEntity("jfly_12", "H. Kane", 9, PositionType.ST.name, 89, 74, 93, 85, 82, 50, 84, isStarter = false, isCaptain = false),
                PlayerEntity("jfly_13", "B. Saka", 77, PositionType.RW.name, 87, 88, 83, 84, 88, 60, 75, isStarter = false, isCaptain = false),
                PlayerEntity("jfly_14", "L. Modric", 14, PositionType.CM.name, 86, 72, 78, 91, 89, 70, 68, isStarter = false, isCaptain = false),
                PlayerEntity("jfly_15", "A. Hakimi", 22, PositionType.RB.name, 86, 92, 75, 80, 83, 79, 81, isStarter = false, isCaptain = false),
                PlayerEntity("jfly_16", "E. Martinez", 23, PositionType.GK.name, 87, 65, 45, 78, 62, 88, 86, isStarter = false, isCaptain = false)
            )
            dao.insertSquadPlayers(starterSquad)
        }

        val car = dao.getCareerProgressFlow().firstOrNull()
        if (car == null) {
            dao.saveCareerProgress(CareerEntity())
        }
    }

    private fun initStandings() {
        val clubs = listOf(
            LeagueClubData("jfly", "JFLY FC (Vous)", 14, 12, 1, 1, 38, 10, 28, 37),
            LeagueClubData("madrid", "Madrid Royale", 14, 11, 2, 1, 34, 12, 22, 35),
            LeagueClubData("paris", "Paris Elite", 14, 10, 3, 1, 31, 11, 20, 33),
            LeagueClubData("mancity", "Manchester Sky", 14, 10, 2, 2, 35, 14, 21, 32),
            LeagueClubData("bayern", "Munich Champions", 14, 9, 2, 3, 30, 15, 15, 29),
            LeagueClubData("milan", "Milan Rossoneri", 14, 8, 3, 3, 26, 17, 9, 27),
            LeagueClubData("arsenal", "London Gunners", 14, 7, 4, 3, 24, 16, 8, 25),
            LeagueClubData("barca", "Catalunya Blau", 14, 7, 2, 5, 25, 20, 5, 23),
            LeagueClubData("juve", "Turin Zebras", 14, 6, 4, 4, 19, 18, 1, 22),
            LeagueClubData("dortmund", "Westfalen BVB", 14, 6, 2, 6, 22, 21, 1, 20),
            LeagueClubData("inter", "Nerazzurri FC", 14, 5, 4, 5, 18, 19, -1, 19),
            LeagueClubData("ajax", "Amsterdam Ajax", 14, 4, 3, 7, 17, 24, -7, 15),
            LeagueClubData("benfica", "Lisbon Eagles", 14, 3, 4, 7, 14, 23, -9, 13),
            LeagueClubData("porto", "Porto Dragons", 14, 3, 3, 8, 13, 26, -13, 12),
            LeagueClubData("om", "Marseille Bleu", 14, 2, 4, 8, 12, 28, -16, 10),
            LeagueClubData("celtic", "Glasgow Hoops", 14, 1, 3, 10, 10, 32, -22, 6)
        )
        _standings.value = clubs.sortedByDescending { it.points }
    }

    private fun initChallenges() {
        _challenges.value = listOf(
            ChallengeData("c1", "Maître des Tirs au But", "Remportez une séance de 5 pénaltys", 2500, 30, 3, 5, isCompleted = false, isClaimed = false),
            ChallengeData("c2", "Coup Franc Lucarne", "Marquez 3 coups francs directs", 3000, 45, 1, 3, isCompleted = false, isClaimed = false),
            ChallengeData("c3", "Slalom Dribble Élite", "Éliminez 5 défenseurs avec des gestes techniques", 2000, 25, 5, 5, isCompleted = true, isClaimed = false),
            ChallengeData("c4", "But en Or Express", "Marquez le premier but en moins de 30s", 4000, 60, 0, 1, isCompleted = false, isClaimed = false)
        )
    }

    private fun initShop() {
        _shopItems.value = listOf(
            ShopItemData("p1", "Pack Joueur Légende", "Débloquez 1 joueur superstar aléatoire OVR 90+", priceCoins = 12000, priceDiamonds = 150, currency = "COINS", isOwned = false),
            ShopItemData("p2", "Pack Élite Titulaires", "Pack de 3 joueurs or OVR 85+", priceCoins = 6000, priceDiamonds = 80, currency = "COINS", isOwned = false),
            ShopItemData("k1", "Maillot JFLY Noir & Or", "Le maillot officiel de gala JFLY 11 vs 11", priceCoins = 0, priceDiamonds = 0, currency = "COINS", isOwned = true),
            ShopItemData("k2", "Maillot Cyber Neon 2026", "Design futuriste bleu cyan électrisant", priceCoins = 5000, priceDiamonds = 60, currency = "COINS", isOwned = false),
            ShopItemData("b1", "Crampons Or Pure Vitesse", "+3 Vitesse et +2 Frappe pour le Capitaine", priceCoins = 8000, priceDiamonds = 100, currency = "COINS", isOwned = false),
            ShopItemData("d1", "Sac 500 Diamants", "Boost instantané pour débloquer des packs rares", priceCoins = 0, priceDiamonds = 500, currency = "DIAMONDS", isOwned = false)
        )
    }

    suspend fun recordMatchFinished(homeScore: Int, awayScore: Int, opponentName: String) {
        val resultStr = if (homeScore > awayScore) "VICTOIRE" else if (homeScore == awayScore) "NUL" else "DÉFAITE"
        val coinsEarned = if (homeScore > awayScore) 1500 + homeScore * 200 else 500 + homeScore * 150
        val xpEarned = if (homeScore > awayScore) 350 else 150

        val currentProfile = dao.getUserProfileFlow().firstOrNull() ?: UserProfileEntity()
        var newXp = currentProfile.currentXp + xpEarned
        var newLevel = currentProfile.level
        var newTargetXp = currentProfile.targetXp
        if (newXp >= newTargetXp) {
            newLevel += 1
            newXp -= newTargetXp
            newTargetXp = (newTargetXp * 1.25).toInt()
        }

        val updated = currentProfile.copy(
            coins = currentProfile.coins + coinsEarned,
            currentXp = newXp,
            targetXp = newTargetXp,
            level = newLevel,
            matchesPlayed = currentProfile.matchesPlayed + 1,
            matchesWon = currentProfile.matchesWon + (if (homeScore > awayScore) 1 else 0),
            goalsScored = currentProfile.goalsScored + homeScore
        )
        dao.insertOrUpdateProfile(updated)

        dao.insertMatchHistory(
            MatchHistoryEntity(
                mode = "11 vs 11",
                opponentName = opponentName,
                homeScore = homeScore,
                awayScore = awayScore,
                result = resultStr,
                rewardCoins = coinsEarned,
                rewardXp = xpEarned
            )
        )
    }

    suspend fun upgradePlayerStat(playerId: String, statKey: String): Boolean {
        val currentProfile = dao.getUserProfileFlow().firstOrNull() ?: return false
        val cost = 2500
        if (currentProfile.coins < cost) return false

        val players = dao.getAllSquadPlayersFlow().firstOrNull() ?: return false
        val player = players.find { it.id == playerId } ?: return false

        val updatedPlayer = when (statKey) {
            "pace" -> player.copy(pace = (player.pace + 2).coerceAtMost(99), ovr = (player.ovr + 1).coerceAtMost(99))
            "shooting" -> player.copy(shooting = (player.shooting + 2).coerceAtMost(99), ovr = (player.ovr + 1).coerceAtMost(99))
            "passing" -> player.copy(passing = (player.passing + 2).coerceAtMost(99), ovr = (player.ovr + 1).coerceAtMost(99))
            "dribbling" -> player.copy(dribbling = (player.dribbling + 2).coerceAtMost(99), ovr = (player.ovr + 1).coerceAtMost(99))
            "defending" -> player.copy(defending = (player.defending + 2).coerceAtMost(99), ovr = (player.ovr + 1).coerceAtMost(99))
            "physical" -> player.copy(physical = (player.physical + 2).coerceAtMost(99), ovr = (player.ovr + 1).coerceAtMost(99))
            else -> player
        }

        dao.updatePlayer(updatedPlayer)
        dao.insertOrUpdateProfile(currentProfile.copy(coins = currentProfile.coins - cost))
        return true
    }

    suspend fun swapStarterAndSub(starterId: String, subId: String) {
        val players = dao.getAllSquadPlayersFlow().firstOrNull() ?: return
        val starter = players.find { it.id == starterId } ?: return
        val sub = players.find { it.id == subId } ?: return

        dao.updatePlayer(starter.copy(isStarter = false))
        dao.updatePlayer(sub.copy(isStarter = true))
    }

    suspend fun buyShopItem(item: ShopItemData) {
        val currentProfile = dao.getUserProfileFlow().firstOrNull() ?: return
        val updated = if (item.currency == "DIAMONDS") {
            if (currentProfile.diamonds < item.priceDiamonds) return
            currentProfile.copy(diamonds = currentProfile.diamonds - item.priceDiamonds)
        } else {
            if (currentProfile.coins < item.priceCoins) return
            currentProfile.copy(coins = currentProfile.coins - item.priceCoins)
        }
        dao.insertOrUpdateProfile(updated)

        _shopItems.value = _shopItems.value.map {
            if (it.id == item.id) it.copy(isOwned = true) else it
        }
    }

    suspend fun claimChallenge(challengeId: String) {
        val ch = _challenges.value.find { it.id == challengeId } ?: return
        val currentProfile = dao.getUserProfileFlow().firstOrNull() ?: return

        dao.insertOrUpdateProfile(
            currentProfile.copy(
                coins = currentProfile.coins + ch.rewardCoins,
                diamonds = currentProfile.diamonds + ch.rewardDiamonds
            )
        )

        _challenges.value = _challenges.value.map {
            if (it.id == challengeId) it.copy(isClaimed = true, progress = it.target) else it
        }
    }

    suspend fun upgradeCareerSkill(skill: String) {
        val car = dao.getCareerProgressFlow().firstOrNull() ?: return
        if (car.skillPoints <= 0) return
        dao.saveCareerProgress(
            car.copy(
                skillPoints = car.skillPoints - 1,
                playerOvr = (car.playerOvr + 1).coerceAtMost(99)
            )
        )
    }
}
