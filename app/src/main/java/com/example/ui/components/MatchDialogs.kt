package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.game.model.CameraMode
import com.example.game.model.MatchState
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentRed
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.JflyGold
import com.example.ui.theme.JflyGoldDark
import com.example.ui.theme.NeonCyan

@Composable
fun PauseDialog(
    matchState: MatchState,
    onResume: () -> Unit,
    onCameraChange: (CameraMode) -> Unit,
    onSoundToggle: () -> Unit,
    onQuit: () -> Unit
) {
    Dialog(onDismissRequest = onResume) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(20.dp))
                .background(DarkSurface)
                .border(1.5.dp, JflyGold.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "MATCH EN PAUSE",
                    color = JflyGold,
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp
                )

                // Match Score in pause
                Text(
                    text = "${matchState.homeTeamName}  ${matchState.homeScore} - ${matchState.awayScore}  ${matchState.awayTeamName}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )

                // Camera Angle Selection
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Angle Caméra",
                        color = Color(0xFFB0B8C8),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            CameraMode.BROADCAST_3D to "3D TV",
                            CameraMode.DYNAMIC_TV to "Dynamique",
                            CameraMode.TACTICAL_TOP to "Tactique"
                        ).forEach { (mode, label) ->
                            val isSelected = matchState.cameraMode == mode
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) JflyGold else DarkCard)
                                    .border(1.dp, if (isSelected) JflyGoldDark else DarkCardBorder, RoundedCornerShape(10.dp))
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Button(
                                    onClick = { onCameraChange(mode) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                                ) {
                                    Text(
                                        text = label,
                                        color = if (isSelected) Color.Black else Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Resume button
                Button(
                    onClick = onResume,
                    modifier = Modifier.fillMaxWidth().testTag("btn_resume_match"),
                    colors = ButtonDefaults.buttonColors(containerColor = JflyGold),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("REPRENDRE LE MATCH", color = Color.Black, fontWeight = FontWeight.Black)
                }

                // Quit button
                OutlinedButton(
                    onClick = onQuit,
                    modifier = Modifier.fillMaxWidth().testTag("btn_quit_match"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentRed)
                ) {
                    Text("QUITTER LE MATCH", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun MatchSummaryDialog(
    matchState: MatchState,
    onContinue: () -> Unit
) {
    val isWin = matchState.homeScore > matchState.awayScore
    val isDraw = matchState.homeScore == matchState.awayScore
    val titleText = if (isWin) "🏆 VICTOIRE !" else if (isDraw) "🤝 MATCH NUL" else "❌ DÉFAITE"
    val titleColor = if (isWin) JflyGold else if (isDraw) NeonCyan else AccentRed

    val coinsEarned = if (isWin) 1500 + matchState.homeScore * 200 else 500 + matchState.homeScore * 150
    val xpEarned = if (isWin) 350 else 150

    Dialog(onDismissRequest = onContinue) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(20.dp))
                .background(DarkSurface)
                .border(2.dp, titleColor.copy(alpha = 0.7f), RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = titleText,
                    color = titleColor,
                    fontWeight = FontWeight.Black,
                    fontSize = 24.sp
                )

                // Scoreboard card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkCard)
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(matchState.homeTeamName, color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                        Text("${matchState.homeScore}", color = JflyGold, fontWeight = FontWeight.Black, fontSize = 28.sp)
                    }
                    Text("VS", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(matchState.awayTeamName, color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                        Text("${matchState.awayScore}", color = Color.White, fontWeight = FontWeight.Black, fontSize = 28.sp)
                    }
                }

                // Match Stats
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F131D))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    StatRow("Tirs au but", "${matchState.stats.homeShots}", "${matchState.stats.awayShots}")
                    StatRow("Tirs cadrés", "${matchState.stats.homeShotsOnTarget}", "${matchState.stats.awayShotsOnTarget}")
                    StatRow("Passes réussies", "${matchState.stats.homePassesCompleted}", "${matchState.stats.awayPassesCompleted}")
                }

                // Rewards Earned
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1B1607))
                        .border(1.dp, JflyGold.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = JflyGold, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("+$coinsEarned Pièces", color = JflyGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("+$xpEarned XP", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }

                Button(
                    onClick = onContinue,
                    modifier = Modifier.fillMaxWidth().testTag("btn_continue_postmatch"),
                    colors = ButtonDefaults.buttonColors(containerColor = JflyGold),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("CONTINUER", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 15.sp)
                }
            }
        }
    }
}

@Composable
fun StatRow(label: String, homeVal: String, awayVal: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(homeVal, color = JflyGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Text(label, color = Color(0xFFA0ABC0), fontSize = 12.sp)
        Text(awayVal, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}
