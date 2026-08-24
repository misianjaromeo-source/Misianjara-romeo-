package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.FormationType
import com.example.data.model.PlayerData
import com.example.data.model.TeamData
import com.example.game.engine.MatchEngine
import com.example.game.model.MatchPhase
import com.example.game.model.TeamSide
import com.example.game.renderer.BallRenderer
import com.example.game.renderer.Camera3D
import com.example.game.renderer.PitchRenderer
import com.example.game.renderer.PlayerRenderer
import com.example.sound.SoundManager
import com.example.ui.components.ActionButtons
import com.example.ui.components.MatchScoreboardHUD
import com.example.ui.components.MatchSummaryDialog
import com.example.ui.components.PauseDialog
import com.example.ui.components.VirtualJoystick
import com.example.ui.theme.DarkBg
import com.example.ui.theme.GkKitAway
import com.example.ui.theme.GkKitHome
import com.example.ui.theme.HomeKitPrimary
import com.example.ui.theme.HomeKitSecondary

@Composable
fun MatchScreen(
    awayTeam: TeamData,
    squad: List<PlayerData>,
    formation: FormationType,
    difficulty: String,
    stadiumPattern: String = "STRIPES",
    onMatchFinished: (homeScore: Int, awayScore: Int) -> Unit,
    onExitMatch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val engine = remember { MatchEngine() }
    val camera = remember { Camera3D() }
    val pitchRenderer = remember { PitchRenderer() }
    val playerRenderer = remember { PlayerRenderer() }
    val ballRenderer = remember { BallRenderer() }

    var showPauseDialog by remember { mutableStateOf(false) }
    var showSummaryDialog by remember { mutableStateOf(false) }
    var animTime by remember { mutableFloatStateOf(0f) }

    // Initialize Match
    LaunchedEffect(awayTeam) {
        engine.setupMatch(
            homeTeamName = "JFLY FC",
            awayTeam = awayTeam,
            squad = squad,
            formation = formation,
            difficulty = difficulty
        )
    }

    // 60 FPS Game Loop
    LaunchedEffect(Unit) {
        var lastTime = System.nanoTime()
        while (true) {
            withFrameNanos { now ->
                val delta = (now - lastTime) / 1_000_000_000f
                lastTime = now
                animTime += delta

                engine.update(delta)

                // Update Camera 3D follow target
                camera.update(
                    ballPos = engine.ball.position,
                    controlledPlayerPos = engine.userControlledPlayer?.position,
                    cameraMode = engine.matchState.cameraMode,
                    delta = delta
                )

                // Check for match completion
                if (engine.matchState.phase == MatchPhase.FULL_TIME && !showSummaryDialog) {
                    showSummaryDialog = true
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .testTag("match_screen_root")
    ) {
        // 1. 3D FOOTBALL RENDERING CANVAS
        Canvas(modifier = Modifier.fillMaxSize()) {
            val screenW = size.width
            val screenH = size.height

            // Render Stadium, Crowd & Pitch
            pitchRenderer.drawStadiumAndPitch(
                drawScope = this,
                camera = camera,
                screenWidth = screenW,
                screenHeight = screenH,
                cameraMode = engine.matchState.cameraMode,
                stadiumMowPattern = stadiumPattern,
                animTime = animTime
            )

            // Render 22 3D-Shaded Players
            val allPlayers = engine.homePlayers + engine.awayPlayers
            playerRenderer.drawPlayers(
                drawScope = this,
                players = allPlayers,
                camera = camera,
                screenWidth = screenW,
                screenHeight = screenH,
                cameraMode = engine.matchState.cameraMode,
                homeKitPrimary = HomeKitPrimary,
                homeKitSecondary = HomeKitSecondary,
                awayKitPrimary = Color(awayTeam.primaryColor),
                awayKitSecondary = Color(awayTeam.secondaryColor),
                gkKitHome = GkKitHome,
                gkKitAway = GkKitAway
            )

            // Render 3D Football with Shadow & Motion Trail
            ballRenderer.drawBall(
                drawScope = this,
                ball = engine.ball,
                camera = camera,
                screenWidth = screenW,
                screenHeight = screenH,
                cameraMode = engine.matchState.cameraMode,
                ballDesign = "GOLD_CROWN"
            )
        }

        // 2. BROADCAST SCOREBOARD HUD, RADAR & COMMENTARY
        MatchScoreboardHUD(
            matchState = engine.matchState,
            ball = engine.ball,
            homePlayers = engine.homePlayers,
            awayPlayers = engine.awayPlayers,
            onPauseClick = {
                engine.matchState.isPaused = true
                showPauseDialog = true
            }
        )

        // 3. VIRTUAL JOYSTICK (Bottom Left)
        VirtualJoystick(
            onMove = { x, y ->
                engine.joystickX = x
                engine.joystickY = y
            },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 16.dp)
        )

        // 4. ACTION BUTTONS (Bottom Right: Passe, Tir, Sprint, Tacle)
        val userHasBall = (engine.ball.isControlledBy?.team == TeamSide.HOME)
        ActionButtons(
            hasBall = userHasBall,
            onPassClick = { engine.onPassButtonPressed() },
            onShootDown = { engine.onShootButtonDown() },
            onShootRelease = { engine.onShootButtonRelease() },
            onSprintToggle = { engine.isSprintActive = it },
            onTackleClick = { engine.onTackleButtonPressed() },
            onSwitchClick = { engine.onSwitchButtonPressed() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp)
        )

        // 5. PAUSE DIALOG
        if (showPauseDialog) {
            PauseDialog(
                matchState = engine.matchState,
                onResume = {
                    engine.matchState.isPaused = false
                    showPauseDialog = false
                },
                onCameraChange = { newMode ->
                    engine.matchState.cameraMode = newMode
                },
                onSoundToggle = {
                    SoundManager.isMuted = !SoundManager.isMuted
                },
                onQuit = {
                    showPauseDialog = false
                    onExitMatch()
                }
            )
        }

        // 6. POST-MATCH SUMMARY DIALOG
        if (showSummaryDialog) {
            MatchSummaryDialog(
                matchState = engine.matchState,
                onContinue = {
                    showSummaryDialog = false
                    onMatchFinished(engine.matchState.homeScore, engine.matchState.awayScore)
                }
            )
        }
    }
}
