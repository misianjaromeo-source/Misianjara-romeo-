package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.model.Ball
import com.example.game.model.FootballPlayer
import com.example.game.model.MatchState
import com.example.game.model.TeamSide
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentRed
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.JflyGold
import com.example.ui.theme.NeonCyan

@Composable
fun MatchScoreboardHUD(
    matchState: MatchState,
    ball: Ball,
    homePlayers: List<FootballPlayer>,
    awayPlayers: List<FootballPlayer>,
    onPauseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        // 1. TOP BROADCAST SCOREBOARD
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 10.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xE60A0D14))
                .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Home Team (JFLY FC)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(JflyGold)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = matchState.homeTeamName,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp
                )
            }

            // Scoreboard (2 - 1)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF161B26))
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "${matchState.homeScore} - ${matchState.awayScore}",
                    color = JflyGold,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp
                )
            }

            // Away Team
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = matchState.awayTeamName,
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Color(matchState.awayColor))
                )
            }

            // Match Clock
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF00E676).copy(alpha = 0.2f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "${matchState.displayMinutes}' ${matchState.displayExtraTime}",
                    color = AccentGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }

            // Pause Button
            IconButton(
                onClick = onPauseClick,
                modifier = Modifier.size(28.dp).testTag("match_pause_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Pause,
                    contentDescription = "Pause",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // 2. RADAR / MINI-MAP (Top-Right)
        PitchRadar(
            ball = ball,
            homePlayers = homePlayers,
            awayPlayers = awayPlayers,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 10.dp, end = 12.dp)
        )

        // 3. LIVE COMMENTARY TICKER (Bottom Center)
        if (matchState.commentaryText.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xCC000000))
                    .border(1.dp, Color(0x40FFD700), RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = matchState.commentaryText,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp
                )
            }
        }

        // 4. GOAL CELEBRATION BANNER
        AnimatedVisibility(
            visible = matchState.goalScorerText != null,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFFFFD700), Color(0xFFFFA000))
                        )
                    )
                    .border(3.dp, Color.White, RoundedCornerShape(16.dp))
                    .padding(horizontal = 28.dp, vertical = 14.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "⚽ GOOOAAAL !",
                        color = Color.Black,
                        fontWeight = FontWeight.Black,
                        fontSize = 28.sp
                    )
                    Text(
                        text = matchState.goalScorerText ?: "",
                        color = Color(0xFF111111),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun PitchRadar(
    ball: Ball,
    homePlayers: List<FootballPlayer>,
    awayPlayers: List<FootballPlayer>,
    modifier: Modifier = Modifier
) {
    val radarWidth = 100.dp
    val radarHeight = 65.dp

    Box(
        modifier = modifier
            .size(radarWidth, radarHeight)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xAA0A1A0F))
            .border(1.dp, Color(0x66FFFFFF), RoundedCornerShape(8.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Center line
            drawLine(Color(0x44FFFFFF), Offset(w / 2f, 0f), Offset(w / 2f, h), strokeWidth = 1f)
            // Center circle
            drawCircle(Color(0x44FFFFFF), radius = h * 0.22f, center = Offset(w / 2f, h / 2f), style = Stroke(1f))

            // Home Players (Gold dots)
            for (p in homePlayers) {
                val px = (p.position.x / 1000f) * w
                val py = (p.position.y / 650f) * h
                drawCircle(
                    color = if (p.isControlled) Color(0xFFFFEB3B) else Color(0xFFFFD700),
                    radius = if (p.isControlled) 3.5f else 2.5f,
                    center = Offset(px, py)
                )
            }

            // Away Players (Cyan/White dots)
            for (p in awayPlayers) {
                val px = (p.position.x / 1000f) * w
                val py = (p.position.y / 650f) * h
                drawCircle(
                    color = Color(0xFF00E5FF),
                    radius = 2.5f,
                    center = Offset(px, py)
                )
            }

            // Ball (Bright White / Red pulsing dot)
            val bx = (ball.position.x / 1000f) * w
            val by = (ball.position.y / 650f) * h
            drawCircle(Color.White, radius = 3.5f, center = Offset(bx, by))
        }
    }
}
