package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.AppDatabase
import com.example.data.GameRepository
import com.example.data.entity.CareerEntity
import com.example.data.model.ShopItemData
import com.example.data.model.TeamData
import com.example.ui.MainViewModel
import com.example.ui.screens.CareerModeScreen
import com.example.ui.screens.CupTournamentScreen
import com.example.ui.screens.DailyEventsScreen
import com.example.ui.screens.MainMenuScreen
import com.example.ui.screens.MatchScreen
import com.example.ui.screens.ProfileFriendsScreen
import com.example.ui.screens.QuickMatchSelectScreen
import com.example.ui.screens.SeasonLeagueScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.ShopScreen
import com.example.ui.screens.StadiumCustomizerScreen
import com.example.ui.screens.TeamManagementScreen
import com.example.ui.theme.JflyFootballTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = GameRepository(database.gameDao())
        val viewModel = MainViewModel(repository)

        setContent {
            JflyFootballTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    JflyFootballApp(viewModel = viewModel)
                }
            }
        }
    }
}

object Routes {
    const val MAIN_MENU = "main_menu"
    const val QUICK_MATCH_SELECT = "quick_match_select"
    const val MATCH = "match"
    const val TEAM_MANAGEMENT = "team_management"
    const val SEASON_LEAGUE = "season_league"
    const val CUP_TOURNAMENT = "cup_tournament"
    const val CAREER_MODE = "career_mode"
    const val DAILY_EVENTS = "daily_events"
    const val STADIUM = "stadium"
    const val SHOP = "shop"
    const val SETTINGS = "settings"
    const val PROFILE_FRIENDS = "profile_friends"
}

@Composable
fun JflyFootballApp(viewModel: MainViewModel) {
    val navController = rememberNavController()

    val userProfile by viewModel.userProfile.collectAsState()
    val squad by viewModel.squad.collectAsState()
    val standings by viewModel.standings.collectAsState()
    val challenges by viewModel.challenges.collectAsState()
    val shopItems by viewModel.shopItems.collectAsState()
    val career by viewModel.career.collectAsState()
    val selectedFormation by viewModel.selectedFormation.collectAsState()
    val currentOpponent by viewModel.currentOpponent.collectAsState()
    val currentDifficulty by viewModel.currentDifficulty.collectAsState()
    val stadiumPattern by viewModel.stadiumPattern.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Routes.MAIN_MENU
    ) {
        // 1. MAIN MENU
        composable(Routes.MAIN_MENU) {
            MainMenuScreen(
                userProfile = userProfile,
                onQuickMatchClick = { navController.navigate(Routes.QUICK_MATCH_SELECT) },
                onSeasonLeagueClick = { navController.navigate(Routes.SEASON_LEAGUE) },
                onCupTournamentClick = { navController.navigate(Routes.CUP_TOURNAMENT) },
                onTeamManagementClick = { navController.navigate(Routes.TEAM_MANAGEMENT) },
                onShopClick = { navController.navigate(Routes.SHOP) },
                onSettingsClick = { navController.navigate(Routes.SETTINGS) },
                onDailyEventsClick = { navController.navigate(Routes.DAILY_EVENTS) },
                onCareerModeClick = { navController.navigate(Routes.CAREER_MODE) },
                onStadiumClick = { navController.navigate(Routes.STADIUM) },
                onProfileClick = { navController.navigate(Routes.PROFILE_FRIENDS) },
                onMessagesClick = { navController.navigate(Routes.PROFILE_FRIENDS) },
                onFriendsClick = { navController.navigate(Routes.PROFILE_FRIENDS) }
            )
        }

        // 2. QUICK MATCH SELECTION
        composable(Routes.QUICK_MATCH_SELECT) {
            QuickMatchSelectScreen(
                userProfile = userProfile,
                teams = viewModel.teams,
                onStartMatch = { opponent, difficulty ->
                    viewModel.prepareMatch(opponent, difficulty)
                    navController.navigate(Routes.MATCH)
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        // 3. 11 VS 11 3D MATCH SCREEN
        composable(Routes.MATCH) {
            MatchScreen(
                awayTeam = currentOpponent,
                squad = squad,
                formation = selectedFormation,
                difficulty = currentDifficulty,
                stadiumPattern = stadiumPattern,
                onMatchFinished = { homeScore, awayScore ->
                    viewModel.recordMatchResult(homeScore, awayScore)
                    navController.popBackStack(Routes.MAIN_MENU, inclusive = false)
                },
                onExitMatch = {
                    navController.popBackStack(Routes.MAIN_MENU, inclusive = false)
                }
            )
        }

        // 4. TEAM MANAGEMENT
        composable(Routes.TEAM_MANAGEMENT) {
            TeamManagementScreen(
                userProfile = userProfile,
                squad = squad,
                selectedFormation = selectedFormation,
                onFormationSelected = { viewModel.setFormation(it) },
                onUpgradePlayerStat = { pId, stat -> viewModel.upgradePlayer(pId, stat) },
                onSwapPlayers = { stId, subId -> viewModel.swapPlayers(stId, subId) },
                onBackClick = { navController.popBackStack() }
            )
        }

        // 5. SEASON LEAGUE
        composable(Routes.SEASON_LEAGUE) {
            val nextOpponent = viewModel.teams.find { it.id == "paris" } ?: viewModel.teams.first()
            SeasonLeagueScreen(
                userProfile = userProfile,
                standings = standings,
                currentMatchDay = userProfile.matchesPlayed + 1,
                nextOpponent = nextOpponent,
                onPlayLeagueMatch = { opponent ->
                    viewModel.prepareMatch(opponent, "Professionnel")
                    navController.navigate(Routes.MATCH)
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        // 6. CUP TOURNAMENT
        composable(Routes.CUP_TOURNAMENT) {
            val finalOpponent = viewModel.teams.find { it.id == "madrid" } ?: viewModel.teams.first()
            CupTournamentScreen(
                userProfile = userProfile,
                finalOpponent = finalOpponent,
                onPlayCupMatch = { opponent ->
                    viewModel.prepareMatch(opponent, "Légende")
                    navController.navigate(Routes.MATCH)
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        // 7. CAREER MODE
        composable(Routes.CAREER_MODE) {
            val currentCareer = career ?: CareerEntity(
                id = "main_career",
                playerName = userProfile.playerName,
                playerOvr = 86,
                currentClub = "JFLY FC",
                seasonNumber = 1,
                transferValue = 45_000_000L,
                weeklyWage = 12_500,
                managerTrust = 92,
                skillPoints = 3,
                matchesPlayed = 24,
                goalsScored = 18,
                assists = 9,
                trophiesWon = 2
            )
            CareerModeScreen(
                userProfile = userProfile,
                career = currentCareer,
                onUpgradeSkill = { viewModel.upgradeCareerSkill(it) },
                onBackClick = { navController.popBackStack() }
            )
        }

        // 8. DAILY EVENTS & PENALTY CHALLENGE
        composable(Routes.DAILY_EVENTS) {
            DailyEventsScreen(
                userProfile = userProfile,
                challenges = challenges,
                onClaimReward = { viewModel.claimChallenge(it) },
                onPlayPenaltyChallenge = {
                    val gkTeam = viewModel.teams.find { it.id == "munich" } ?: viewModel.teams.first()
                    viewModel.prepareMatch(gkTeam, "Professionnel")
                    navController.navigate(Routes.MATCH)
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        // 9. STADIUM CUSTOMIZATION
        composable(Routes.STADIUM) {
            StadiumCustomizerScreen(
                userProfile = userProfile,
                currentPattern = stadiumPattern,
                onPatternSelected = { viewModel.setStadiumPattern(it) },
                onBackClick = { navController.popBackStack() }
            )
        }

        // 10. SHOP
        composable(Routes.SHOP) {
            ShopScreen(
                userProfile = userProfile,
                shopItems = shopItems,
                onBuyItem = { viewModel.buyShopItem(it) },
                onBackClick = { navController.popBackStack() }
            )
        }

        // 11. SETTINGS
        composable(Routes.SETTINGS) {
            SettingsScreen(
                userProfile = userProfile,
                onBackClick = { navController.popBackStack() }
            )
        }

        // 12. PROFILE & FRIENDS
        composable(Routes.PROFILE_FRIENDS) {
            ProfileFriendsScreen(
                userProfile = userProfile,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
