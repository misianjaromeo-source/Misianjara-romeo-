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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.MonetizationOn
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
import com.example.data.entity.CareerEntity
import com.example.data.model.UserProfileData
import com.example.sound.SoundManager
import com.example.ui.components.TopHeaderBar
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.JflyGold
import com.example.ui.theme.JflyGoldDark
import com.example.ui.theme.NeonCyan

@Composable
fun CareerModeScreen(
    userProfile: UserProfileData,
    career: CareerEntity,
    onUpgradeSkill: (String) -> Unit,
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
            IconButton(onClick = onBackClick, modifier = Modifier.testTag("btn_back_career")) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text("MODE CARRIÈRE — DEVENIR UNE LÉGENDE", color = JflyGold, fontWeight = FontWeight.Black, fontSize = 16.sp)
                Text("Club actuel : ${career.currentClub} • Saison ${career.seasonNumber}", color = Color(0xFFA0ABC0), fontSize = 11.sp)
            }
        }

        // CAREER HERO CARD
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF261E09), Color(0xFF131A26))
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(JflyGold, JflyGoldDark))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${career.playerOvr}", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(career.playerName, color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                        Text("Valeur marchande : ${(career.transferValue / 1_000_000f).toInt()}M €", color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("Salaire hebdo : ${career.weeklyWage} 🪙/sem", color = JflyGold, fontSize = 11.sp)
                    }
                }

                // Confidence gauge
                Column(horizontalAlignment = Alignment.End) {
                    Text("Confiance Coach", color = Color(0xFFA0ABC0), fontSize = 10.sp)
                    Text("${career.managerTrust}%", color = AccentGreen, fontWeight = FontWeight.Black, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Points : ${career.skillPoints} SP", color = JflyGold, fontWeight = FontWeight.Black, fontSize = 12.sp)
                }
            }
        }

        // CAREER METRICS ROW
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CareerStatBadge("Matchs", "${career.matchesPlayed}", modifier = Modifier.weight(1f))
            CareerStatBadge("Buts", "${career.goalsScored}", modifier = Modifier.weight(1f))
            CareerStatBadge("Passes D.", "${career.assists}", modifier = Modifier.weight(1f))
            CareerStatBadge("Trophées", "${career.trophiesWon}", modifier = Modifier.weight(1f))
        }

        // SKILL TALENT TREE
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurface)
                .border(1.dp, DarkCardBorder, RoundedCornerShape(16.dp))
                .padding(12.dp)
        ) {
            Text("ARBRE DE COMPÉTENCES LÉGENDE", color = JflyGold, fontWeight = FontWeight.Black, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    SkillTreeItem(
                        title = "⚡ Vitesse Supersonique",
                        desc = "+3 Accélération et vitesse de pointe en contre-attaque",
                        cost = "1 SP",
                        isUnlocked = true,
                        onUnlock = { onUpgradeSkill("speed") }
                    )
                }
                item {
                    SkillTreeItem(
                        title = "🚀 Frappe Guidée d'Or",
                        desc = "+4 Puissance de tir et précision des frappes enroulées",
                        cost = "2 SP",
                        isUnlocked = true,
                        onUnlock = { onUpgradeSkill("shot") }
                    )
                }
                item {
                    SkillTreeItem(
                        title = "🎯 Vision Maestro",
                        desc = "+3 Précision des passes en profondeur lobées",
                        cost = "2 SP",
                        isUnlocked = false,
                        onUnlock = { onUpgradeSkill("pass") }
                    )
                }
                item {
                    SkillTreeItem(
                        title = "🛡️ Tacle Glissé Parfait",
                        desc = "+4 Récupération défensive sans commettre de faute",
                        cost = "3 SP",
                        isUnlocked = false,
                        onUnlock = { onUpgradeSkill("defense") }
                    )
                }
            }
        }
    }
}

@Composable
fun CareerStatBadge(label: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(DarkCard)
            .border(1.dp, DarkCardBorder, RoundedCornerShape(10.dp))
            .padding(vertical = 6.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, color = JflyGold, fontWeight = FontWeight.Black, fontSize = 14.sp)
            Text(label, color = Color(0xFFA0ABC0), fontSize = 10.sp)
        }
    }
}

@Composable
fun SkillTreeItem(
    title: String,
    desc: String,
    cost: String,
    isUnlocked: Boolean,
    onUnlock: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isUnlocked) Color(0xFF1E2718) else DarkCard)
            .border(1.dp, if (isUnlocked) AccentGreen else DarkCardBorder, RoundedCornerShape(10.dp))
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = if (isUnlocked) AccentGreen else Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text(desc, color = Color(0xFFA0ABC0), fontSize = 10.sp)
        }
        Spacer(modifier = Modifier.width(8.dp))
        if (isUnlocked) {
            Text("DÉBLOQUÉ", color = AccentGreen, fontWeight = FontWeight.Black, fontSize = 11.sp)
        } else {
            Button(
                onClick = {
                    SoundManager.play(SoundManager.SfxType.GOLD_REWARD)
                    onUnlock()
                },
                colors = ButtonDefaults.buttonColors(containerColor = JflyGold),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(cost, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        }
    }
}
