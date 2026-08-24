package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.data.model.ChallengeData
import com.example.data.model.UserProfileData
import com.example.sound.SoundManager
import com.example.ui.components.TopHeaderBar
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.JflyGold
import com.example.ui.theme.JflyGoldDark
import com.example.ui.theme.NeonCyan

@Composable
fun DailyEventsScreen(
    userProfile: UserProfileData,
    challenges: List<ChallengeData>,
    onClaimReward: (String) -> Unit,
    onPlayPenaltyChallenge: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(bottom = 8.dp)
    ) {
        TopHeaderBar(userProfile = userProfile)

        // HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick, modifier = Modifier.testTag("btn_back_events")) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text("ÉVÉNEMENTS & DÉFIS DU JOUR", color = JflyGold, fontWeight = FontWeight.Black, fontSize = 16.sp)
                Text("Réinitialisation quotidienne dans 14h 22m", color = Color(0xFFA0ABC0), fontSize = 11.sp)
            }
        }

        // SPECIAL EVENT HERO: PENALTY SHOOTOUT MASTER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))
                    )
                )
                .border(1.5.dp, NeonCyan.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("DÉFI SPÉCIAL : SÉANCE DE TIRS AU BUT", color = NeonCyan, fontWeight = FontWeight.Black, fontSize = 14.sp)
                    Text("Marquez 3 tirs au but consécutifs contre le gardien", color = Color.White, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Récompense : +10 000 🪙 + 50 💎", color = JflyGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        SoundManager.play(SoundManager.SfxType.UI_CLICK)
                        onPlayPenaltyChallenge()
                    },
                    modifier = Modifier.testTag("btn_play_penalty_challenge"),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("JOUER", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 13.sp)
                }
            }
        }

        // DAILY CHALLENGES LIST
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurface)
                .border(1.dp, DarkCardBorder, RoundedCornerShape(16.dp))
                .padding(12.dp)
        ) {
            Text("MISSIONS QUOTIDIENNES", color = Color(0xFFA0ABC0), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(6.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(challenges) { ch ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkCard)
                            .border(1.dp, if (ch.isCompleted) AccentGreen.copy(alpha = 0.5f) else DarkCardBorder, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(ch.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(ch.description, color = Color(0xFFA0ABC0), fontSize = 10.sp)
                                Spacer(modifier = Modifier.height(4.dp))

                                // Progress bar
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    LinearProgressIndicator(
                                        progress = { (ch.progress.toFloat() / ch.target.toFloat()).coerceIn(0f, 1f) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(5.dp)
                                            .clip(CircleShape),
                                        color = if (ch.isCompleted) AccentGreen else JflyGold,
                                        trackColor = Color(0xFF1E2638)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("${ch.progress}/${ch.target}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Claim / In Progress Button
                            if (ch.isClaimed) {
                                Text("RÉCUPÉRÉ", color = Color(0xFF6B7280), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            } else if (ch.isCompleted) {
                                Button(
                                    onClick = {
                                        SoundManager.play(SoundManager.SfxType.GOLD_REWARD)
                                        onClaimReward(ch.id)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentGreen),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("RÉCLAMER", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 11.sp)
                                }
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = JflyGold, modifier = Modifier.size(14.dp))
                                    Text("+${ch.rewardCoins}", color = JflyGold, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
