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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.data.model.LeagueClubData
import com.example.data.model.TeamData
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
fun SeasonLeagueScreen(
    userProfile: UserProfileData,
    standings: List<LeagueClubData>,
    currentMatchDay: Int,
    nextOpponent: TeamData,
    onPlayLeagueMatch: (TeamData) -> Unit,
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

        // HEADER TITLE
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick, modifier = Modifier.testTag("btn_back_league")) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "LIGUE ÉLITE JFLY — SAISON 1",
                    color = JflyGold,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                )
                Text(
                    text = "Journée $currentMatchDay / 30 • 16 Clubs",
                    color = Color(0xFFA0ABC0),
                    fontSize = 11.sp
                )
            }
        }

        // NEXT MATCH HERO CARD
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF261D09), Color(0xFF131A26))
                    )
                )
                .border(1.5.dp, JflyGold.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Match details
                Column {
                    Text("PROCHAIN MATCH — J$currentMatchDay", color = JflyGold, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("JFLY FC vs ${nextOpponent.name}", color = Color.White, fontWeight = FontWeight.Black, fontSize = 15.sp)
                    Text("Stade : Grand Dôme JFLY (Domicile)", color = Color(0xFFA0ABC0), fontSize = 11.sp)
                }

                // Play Button
                Button(
                    onClick = {
                        SoundManager.play(SoundManager.SfxType.UI_CLICK)
                        onPlayLeagueMatch(nextOpponent)
                    },
                    modifier = Modifier.testTag("btn_play_league_match"),
                    colors = ButtonDefaults.buttonColors(containerColor = JflyGold),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("JOUER", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 13.sp)
                }
            }
        }

        // STANDINGS TABLE
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurface)
                .border(1.dp, DarkCardBorder, RoundedCornerShape(16.dp))
                .padding(12.dp)
        ) {
            // Table Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("CLUBS", color = Color(0xFFA0ABC0), fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(2f))
                Text("J", color = Color(0xFFA0ABC0), fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(0.5f))
                Text("V", color = Color(0xFFA0ABC0), fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(0.5f))
                Text("N", color = Color(0xFFA0ABC0), fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(0.5f))
                Text("D", color = Color(0xFFA0ABC0), fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(0.5f))
                Text("DIFF", color = Color(0xFFA0ABC0), fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(0.8f))
                Text("PTS", color = JflyGold, fontWeight = FontWeight.Black, fontSize = 11.sp, modifier = Modifier.weight(0.8f))
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                itemsIndexed(standings) { idx, club ->
                    val isJfly = club.clubId == "jfly"
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isJfly) Color(0xFF261D09) else if (idx % 2 == 0) DarkCard else Color.Transparent)
                            .border(if (isJfly) 1.dp else 0.dp, if (isJfly) JflyGold else Color.Transparent, RoundedCornerShape(8.dp))
                            .padding(vertical = 6.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Position badge & Club Name
                        Row(modifier = Modifier.weight(2f), verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${idx + 1}.",
                                color = if (idx == 0) JflyGold else if (idx < 4) NeonCyan else Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = club.clubName,
                                color = if (isJfly) JflyGold else Color.White,
                                fontWeight = if (isJfly) FontWeight.Black else FontWeight.SemiBold,
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                        }

                        Text("${club.played}", color = Color(0xFFB0B8C8), fontSize = 11.sp, modifier = Modifier.weight(0.5f))
                        Text("${club.won}", color = AccentGreen, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.weight(0.5f))
                        Text("${club.drawn}", color = Color(0xFFB0B8C8), fontSize = 11.sp, modifier = Modifier.weight(0.5f))
                        Text("${club.lost}", color = Color(0xFFFF5252), fontSize = 11.sp, modifier = Modifier.weight(0.5f))
                        Text(
                            "${if (club.goalDiff > 0) "+${club.goalDiff}" else club.goalDiff}",
                            color = Color(0xFFB0B8C8),
                            fontSize = 11.sp,
                            modifier = Modifier.weight(0.8f)
                        )
                        Text(
                            "${club.points}",
                            color = if (isJfly) JflyGold else Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            modifier = Modifier.weight(0.8f)
                        )
                    }
                }
            }
        }
    }
}
