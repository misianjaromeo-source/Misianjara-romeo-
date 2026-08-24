package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FormationType
import com.example.data.model.PlayerData
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
fun TeamManagementScreen(
    userProfile: UserProfileData,
    squad: List<PlayerData>,
    selectedFormation: FormationType,
    onFormationSelected: (FormationType) -> Unit,
    onUpgradePlayerStat: (playerId: String, stat: String) -> Unit,
    onSwapPlayers: (starterId: String, subId: String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPlayer by remember { mutableStateOf<PlayerData?>(squad.find { it.isCaptain } ?: squad.firstOrNull()) }

    val starters = squad.filter { it.isStarter }
    val substitutes = squad.filter { !it.isStarter }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
            .padding(bottom = 8.dp)
    ) {
        // TOP BAR
        TopHeaderBar(userProfile = userProfile)

        // HEADER TITLE WITH BACK BUTTON
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.testTag("btn_back_team")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Retour",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "MON ÉQUIPE — EFFECTIF & TACTIQUE",
                    color = JflyGold,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp
                )
                Text(
                    text = "JFLY FC • Note d'équipe : 89 OVR",
                    color = Color(0xFFA0ABC0),
                    fontSize = 11.sp
                )
            }
        }

        // FORMATION SELECTOR CHIPS
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(FormationType.values()) { formation ->
                val isSelected = formation == selectedFormation
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) JflyGold else DarkCard)
                        .border(1.dp, if (isSelected) JflyGoldDark else DarkCardBorder, RoundedCornerShape(12.dp))
                        .clickable {
                            SoundManager.play(SoundManager.SfxType.UI_CLICK)
                            onFormationSelected(formation)
                        }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = formation.displayName,
                        color = if (isSelected) Color.Black else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // LEFT: TACTICAL PITCH BOARD
            Box(
                modifier = Modifier
                    .weight(1.1f)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF144D20))
                    .border(1.5.dp, Color(0x66FFFFFF), RoundedCornerShape(16.dp))
            ) {
                // Draw Tactical Pitch Lines
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    // Grass stripes
                    for (i in 0 until 6) {
                        if (i % 2 == 0) {
                            drawRect(Color(0x18000000), topLeft = Offset(0f, i * (h / 6f)), size = androidx.compose.ui.geometry.Size(w, h / 6f))
                        }
                    }
                    // Pitch lines
                    drawLine(Color(0x88FFFFFF), Offset(0f, h / 2f), Offset(w, h / 2f), strokeWidth = 2f)
                    drawCircle(Color(0x88FFFFFF), radius = w * 0.22f, center = Offset(w / 2f, h / 2f), style = Stroke(2f))
                    // Goal boxes
                    drawRect(Color(0x88FFFFFF), topLeft = Offset(w * 0.2f, 0f), size = androidx.compose.ui.geometry.Size(w * 0.6f, h * 0.16f), style = Stroke(2f))
                    drawRect(Color(0x88FFFFFF), topLeft = Offset(w * 0.2f, h * 0.84f), size = androidx.compose.ui.geometry.Size(w * 0.6f, h * 0.16f), style = Stroke(2f))
                }

                // Display Starting 11 Tokens on Pitch
                Box(modifier = Modifier.fillMaxSize()) {
                    val formationPositions = get2DFormationPositions(selectedFormation)
                    starters.take(11).forEachIndexed { idx, player ->
                        val pos = formationPositions.getOrElse(idx) { 0.5f to 0.5f }
                        val isSelected = selectedPlayer?.id == player.id

                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(
                                    start = (pos.first * 170).dp,
                                    top = (pos.second * 260).dp
                                )
                                .clickable {
                                    SoundManager.play(SoundManager.SfxType.UI_CLICK)
                                    selectedPlayer = player
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) JflyGold else if (player.isCaptain) Color(0xFFFF9100) else Color(0xFF111111))
                                        .border(2.dp, if (isSelected) Color.White else JflyGold, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${player.number}",
                                        color = if (isSelected) Color.Black else JflyGold,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp
                                    )
                                }
                                Text(
                                    text = player.name.split(" ").lastOrNull() ?: player.name,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            // RIGHT: SELECTED PLAYER CARD & ATTRIBUTE UPGRADES
            Column(
                modifier = Modifier
                    .weight(0.9f)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkSurface)
                    .border(1.dp, DarkCardBorder, RoundedCornerShape(16.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                selectedPlayer?.let { player ->
                    // PLAYER CARD HEADER
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Brush.linearGradient(listOf(JflyGold, JflyGoldDark))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${player.ovr}",
                                color = Color.Black,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = player.name,
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "${player.position.name} • N°${player.number} ${if (player.isCaptain) "• CAPITAINE" else ""}",
                                color = JflyGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // ATTRIBUTES BREAKDOWN
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        item {
                            StatUpgradeRow("Vitesse (PAC)", player.pace, onUpgrade = { onUpgradePlayerStat(player.id, "pace") })
                        }
                        item {
                            StatUpgradeRow("Tir (SHO)", player.shooting, onUpgrade = { onUpgradePlayerStat(player.id, "shooting") })
                        }
                        item {
                            StatUpgradeRow("Passe (PAS)", player.passing, onUpgrade = { onUpgradePlayerStat(player.id, "passing") })
                        }
                        item {
                            StatUpgradeRow("Dribble (DRI)", player.dribbling, onUpgrade = { onUpgradePlayerStat(player.id, "dribbling") })
                        }
                        item {
                            StatUpgradeRow("Défense (DEF)", player.defending, onUpgrade = { onUpgradePlayerStat(player.id, "defending") })
                        }
                        item {
                            StatUpgradeRow("Physique (PHY)", player.physical, onUpgrade = { onUpgradePlayerStat(player.id, "physical") })
                        }
                    }
                }
            }
        }

        // BOTTOM: SUBSTITUTES BENCH
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "REMPLAÇANTS (${substitutes.size})",
                color = Color(0xFFA0ABC0),
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(substitutes) { sub ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkCard)
                            .border(1.dp, DarkCardBorder, RoundedCornerShape(10.dp))
                            .clickable {
                                SoundManager.play(SoundManager.SfxType.UI_CLICK)
                                selectedPlayer?.let { currentStarter ->
                                    if (currentStarter.isStarter) {
                                        onSwapPlayers(currentStarter.id, sub.id)
                                    }
                                }
                                selectedPlayer = sub
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("${sub.ovr}", color = JflyGold, fontWeight = FontWeight.Black, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(sub.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatUpgradeRow(label: String, value: Int, onUpgrade: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(DarkCard)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(label, color = Color(0xFFB0B8C8), fontSize = 10.sp)
            Text("$value", color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp)
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFF261F0A))
                .border(1.dp, JflyGold.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                .clickable {
                    SoundManager.play(SoundManager.SfxType.GOLD_REWARD)
                    onUpgrade()
                }
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Add, contentDescription = null, tint = JflyGold, modifier = Modifier.size(12.dp))
                Text("2.5K 🪙", color = JflyGold, fontWeight = FontWeight.Bold, fontSize = 9.sp)
            }
        }
    }
}

private fun get2DFormationPositions(formation: FormationType): List<Pair<Float, Float>> {
    return when (formation) {
        FormationType.F_4_3_3 -> listOf(
            0.45f to 0.85f, // GK
            0.10f to 0.65f, 0.33f to 0.68f, 0.57f to 0.68f, 0.80f to 0.65f, // LB, CB, CB, RB
            0.45f to 0.50f, 0.25f to 0.38f, 0.65f to 0.38f, // CDM, CM, CAM
            0.15f to 0.18f, 0.45f to 0.12f, 0.75f to 0.18f  // LW, ST, RW
        )
        FormationType.F_4_4_2 -> listOf(
            0.45f to 0.85f,
            0.10f to 0.65f, 0.33f to 0.68f, 0.57f to 0.68f, 0.80f to 0.65f,
            0.12f to 0.42f, 0.35f to 0.45f, 0.55f to 0.45f, 0.78f to 0.42f,
            0.32f to 0.15f, 0.58f to 0.15f
        )
        FormationType.F_3_5_2 -> listOf(
            0.45f to 0.85f,
            0.20f to 0.68f, 0.45f to 0.70f, 0.70f to 0.68f,
            0.08f to 0.42f, 0.28f to 0.46f, 0.45f to 0.36f, 0.62f to 0.46f, 0.82f to 0.42f,
            0.32f to 0.15f, 0.58f to 0.15f
        )
        FormationType.F_4_2_3_1 -> listOf(
            0.45f to 0.85f,
            0.10f to 0.65f, 0.33f to 0.68f, 0.57f to 0.68f, 0.80f to 0.65f,
            0.32f to 0.52f, 0.58f to 0.52f,
            0.15f to 0.32f, 0.45f to 0.30f, 0.75f to 0.32f,
            0.45f to 0.12f
        )
    }
}
