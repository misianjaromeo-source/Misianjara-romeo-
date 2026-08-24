package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.GameRepository
import com.example.data.entity.CareerEntity
import com.example.data.model.ChallengeData
import com.example.data.model.FormationType
import com.example.data.model.LeagueClubData
import com.example.data.model.PlayerData
import com.example.data.model.ShopItemData
import com.example.data.model.TeamData
import com.example.data.model.UserProfileData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(private val repository: GameRepository) : ViewModel() {

    val userProfile: StateFlow<UserProfileData> = repository.userProfile
    val squad: StateFlow<List<PlayerData>> = repository.squad
    val standings: StateFlow<List<LeagueClubData>> = repository.standings
    val challenges: StateFlow<List<ChallengeData>> = repository.challenges
    val shopItems: StateFlow<List<ShopItemData>> = repository.shopItems
    val career: StateFlow<CareerEntity?> = repository.career

    private val _selectedFormation = MutableStateFlow(FormationType.F_4_3_3)
    val selectedFormation: StateFlow<FormationType> = _selectedFormation.asStateFlow()

    private val _currentOpponent = MutableStateFlow(repository.defaultTeams.first { it.id == "madrid" })
    val currentOpponent: StateFlow<TeamData> = _currentOpponent.asStateFlow()

    private val _currentDifficulty = MutableStateFlow("Semi-Pro")
    val currentDifficulty: StateFlow<String> = _currentDifficulty.asStateFlow()

    private val _stadiumPattern = MutableStateFlow("STRIPES")
    val stadiumPattern: StateFlow<String> = _stadiumPattern.asStateFlow()

    val teams: List<TeamData> = repository.defaultTeams

    fun setFormation(formation: FormationType) {
        _selectedFormation.value = formation
    }

    fun setStadiumPattern(pattern: String) {
        _stadiumPattern.value = pattern
    }

    fun prepareMatch(opponent: TeamData, difficulty: String = "Semi-Pro") {
        _currentOpponent.value = opponent
        _currentDifficulty.value = difficulty
    }

    fun recordMatchResult(homeScore: Int, awayScore: Int) {
        viewModelScope.launch {
            repository.recordMatchFinished(
                homeScore = homeScore,
                awayScore = awayScore,
                opponentName = _currentOpponent.value.name
            )
        }
    }

    fun upgradePlayer(playerId: String, stat: String) {
        viewModelScope.launch {
            repository.upgradePlayerStat(playerId, stat)
        }
    }

    fun swapPlayers(starterId: String, subId: String) {
        viewModelScope.launch {
            repository.swapStarterAndSub(starterId, subId)
        }
    }

    fun buyShopItem(item: ShopItemData) {
        viewModelScope.launch {
            repository.buyShopItem(item)
        }
    }

    fun claimChallenge(challengeId: String) {
        viewModelScope.launch {
            repository.claimChallenge(challengeId)
        }
    }

    fun upgradeCareerSkill(skill: String) {
        viewModelScope.launch {
            repository.upgradeCareerSkill(skill)
        }
    }
}
