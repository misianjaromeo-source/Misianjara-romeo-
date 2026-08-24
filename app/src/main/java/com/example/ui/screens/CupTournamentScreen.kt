package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PlayArrow
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
fun CupTournamentScreen(
    userProfile: UserProfileData,
    finalOpponent: TeamData,
    onPlayCupMatch: (TeamData) -> Unit,
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
            IconButton(onClick = onBackClick, modifier = Modifier.testTag("btn_back_cup")) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text("COUPE DES CHAMPIONS JFLY", color = JflyGold, fontWeight = FontWeight.Black, fontSize = 16.sp)
                Text("Tournoi à élimination directe • Grande Finale", color = Color(0xFFA0ABC0), fontSize = 11.sp)
            }
        }

        // TROPHY HERO BANNER
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF382A08), Color(0xFF101622))
                    )
                )
                .border(2.dp, JflyGold, RoundedCornerShape(16.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(JflyGold, JflyGoldDark))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color.Black, modifier = Modifier.size(34.dp))
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text("GRANDE FINALE DU TOURNOI", color = JflyGold, fontWeight = FontWeight.Black, fontSize = 18.sp)
                Text("JFLY FC  vs  ${finalOpponent.name}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("Récompense de victoire : +50 000 🪙 + Trophée d'Or", color = NeonCyan, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)

                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        SoundManager.play(SoundManager.SfxType.UI_CLICK)
                        onPlayCupMatch(finalOpponent)
                    },
                    modifier = Modifier.testTag("btn_play_cup_final"),
                    colors = ButtonDefaults.buttonColors(containerColor = JflyGold),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("DISPUTER LA FINALE", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 14.sp)
                }
            }
        }

        // TOURNAMENT TREE BRACKET
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurface)
                .border(1.dp, DarkCardBorder, RoundedCornerShape(16.dp))
                .padding(12.dp)
        ) {
            Text("TABLEAU DU TOURNOI", color = Color(0xFFA0ABC0), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // QUARTER FINALS
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("1/4 FINALE", color = JflyGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    BracketMatchCard("JFLY FC", "3", "Munich Stars", "1", isHomeWinner = true)
                    BracketMatchCard("Milan Rossoneri", "2", "London Red", "1", isHomeWinner = true)
                }

                Spacer(modifier = Modifier.width(8.dp))

                // SEMI FINALS
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("1/2 FINALE", color = JflyGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    BracketMatchCard("JFLY FC", "4", "Milan Rossoneri", "2", isHomeWinner = true)
                    BracketMatchCard("Madrid Royale", "3", "Paris Elite", "1", isHomeWinner = true)
                }

                Spacer(modifier = Modifier.width(8.dp))

                // GRAND FINAL
                Column(modifier = Modifier.weight(1.1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("FINALE", color = NeonCyan, fontSize = 10.sp, fontWeight = FontWeight.Black)
                    BracketMatchCard("JFLY FC", "?", finalOpponent.name, "?", isHomeWinner = false, isLive = true)
                }
            }
        }
    }
}

@Composable
fun BracketMatchCard(
    team1: String, score1: String,
    team2: String, score2: String,
    isHomeWinner: Boolean,
    isLive: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isLive) Color(0xFF241D08) else DarkCard)
            .border(1.dp, if (isLive) JflyGold else DarkCardBorder, RoundedCornerShape(8.dp))
            .padding(6.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(team1, color = if (isHomeWinner) JflyGold else Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp, maxLines = 1)
                Text(score1, color = if (isHomeWinner) JflyGold else Color.White, fontWeight = FontWeight.Black, fontSize = 10.sp)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(team2, color = if (!isHomeWinner && !isLive) JflyGold else Color(0xFFB0B8C8), fontWeight = FontWeight.SemiBold, fontSize = 10.sp, maxLines = 1)
                Text(score2, color = if (!isHomeWinner && !isLive) JflyGold else Color(0xFFB0B8C8), fontWeight = FontWeight.Black, fontSize = 10.sp)
            }
        }
    }
}
