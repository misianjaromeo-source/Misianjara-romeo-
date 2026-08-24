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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TeamData
import com.example.data.model.UserProfileData
import com.example.sound.SoundManager
import com.example.ui.components.TopHeaderBar
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.JflyGold
import com.example.ui.theme.JflyGoldDark
import com.example.ui.theme.NeonCyan

@Composable
fun QuickMatchSelectScreen(
    userProfile: UserProfileData,
    teams: List<TeamData>,
    onStartMatch: (opponent: TeamData, difficulty: String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTeam by remember { mutableStateOf(teams.firstOrNull { it.id == "madrid" } ?: teams.first()) }
    var selectedDifficulty by remember { mutableStateOf("Semi-Pro") }

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
            IconButton(onClick = onBackClick, modifier = Modifier.testTag("btn_back_quick_select")) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text("MATCH RAPIDE 11 VS 11", color = JflyGold, fontWeight = FontWeight.Black, fontSize = 16.sp)
                Text("Sélectionnez votre adversaire et le niveau de difficulté", color = Color(0xFFA0ABC0), fontSize = 11.sp)
            }
        }

        // MATCHUP PREVIEW CARD
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF241D08), Color(0xFF10192A))
                    )
                )
                .border(1.5.dp, JflyGold.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Home: JFLY FC
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(JflyGold, JflyGoldDark))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("89", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("JFLY FC", color = JflyGold, fontWeight = FontWeight.Black, fontSize = 14.sp)
                    Text("Domicile", color = Color(0xFFA0ABC0), fontSize = 10.sp)
                }

                Text("VS", color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)

                // Away: Opponent Team
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(selectedTeam.primaryColor)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${selectedTeam.rating}", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(selectedTeam.name, color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
                    Text(selectedTeam.formation.displayName, color = Color(0xFFA0ABC0), fontSize = 10.sp)
                }
            }
        }

        // DIFFICULTY SELECTOR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("Amateur", "Semi-Pro", "Professionnel", "Légende").forEach { diff ->
                val isSelected = selectedDifficulty == diff
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) JflyGold else DarkCard)
                        .border(1.dp, if (isSelected) JflyGoldDark else DarkCardBorder, RoundedCornerShape(10.dp))
                        .clickable {
                            SoundManager.play(SoundManager.SfxType.UI_CLICK)
                            selectedDifficulty = diff
                        }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = diff,
                        color = if (isSelected) Color.Black else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // OPPONENT SELECTION LIST
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurface)
                .border(1.dp, DarkCardBorder, RoundedCornerShape(16.dp))
                .padding(12.dp)
        ) {
            Text("CHOISIR L'ADVERSAIRE", color = Color(0xFFA0ABC0), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(6.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(teams) { team ->
                    val isSelected = selectedTeam.id == team.id
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) Color(0xFF1E2838) else DarkCard)
                            .border(1.dp, if (isSelected) NeonCyan else DarkCardBorder, RoundedCornerShape(10.dp))
                            .clickable {
                                SoundManager.play(SoundManager.SfxType.UI_CLICK)
                                selectedTeam = team
                            }
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(Color(team.primaryColor))
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(team.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Ligue : ${team.league} • Tactique : ${team.formation.displayName}", color = Color(0xFFA0ABC0), fontSize = 10.sp)
                                }
                            }
                            Text("Note : ${team.rating}", color = JflyGold, fontWeight = FontWeight.Black, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // KICK OFF BUTTON
        Button(
            onClick = {
                SoundManager.play(SoundManager.SfxType.UI_CLICK)
                onStartMatch(selectedTeam, selectedDifficulty)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .testTag("btn_start_match_now"),
            colors = ButtonDefaults.buttonColors(containerColor = JflyGold),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
            Spacer(modifier = Modifier.width(6.dp))
            Text("LANCER LE MATCH RAPIDE", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 15.sp)
        }
    }
}
