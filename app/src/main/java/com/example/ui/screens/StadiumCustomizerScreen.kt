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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Stadium
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
fun StadiumCustomizerScreen(
    userProfile: UserProfileData,
    currentPattern: String,
    onPatternSelected: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPattern by remember { mutableStateOf(currentPattern) }

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
            IconButton(onClick = onBackClick, modifier = Modifier.testTag("btn_back_stadium")) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text("STADE — GRAND DÔME JFLY", color = JflyGold, fontWeight = FontWeight.Black, fontSize = 16.sp)
                Text("Capacité : 68 000 places • Éclairage LED 3D", color = Color(0xFFA0ABC0), fontSize = 11.sp)
            }
        }

        // STADIUM PREVIEW HERO
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF0F381B), Color(0xFF141F16))
                    )
                )
                .border(1.5.dp, Color(0xFF00E676).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Stadium, contentDescription = null, tint = JflyGold, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(4.dp))
                Text("GRAND DÔME JFLY ARENA", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                Text("Pelouse Hybride Haute Densité • Éclairage UEFA Catégorie 4", color = Color(0xFFA0ABC0), fontSize = 11.sp)
            }
        }

        // MOWING PATTERNS SELECTION
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurface)
                .border(1.dp, DarkCardBorder, RoundedCornerShape(16.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("MOTIF DE TONTE DE LA PELOUSE", color = JflyGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)

            listOf(
                Triple("STRIPES", "Rayures Classiques 3D", "Alternance de bandes vert sombre et clair haute visibilité"),
                Triple("DIAMOND", "Damier & Diamant Élite", "Motif géométrique premium style grandes soirées de coupe"),
                Triple("CIRCULAR", "Cercles Concentriques", "Design moderne centré sur le rond central")
            ).forEach { (id, title, desc) ->
                val isSelected = selectedPattern == id
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) Color(0xFF16321F) else DarkCard)
                        .border(1.dp, if (isSelected) Color(0xFF00E676) else DarkCardBorder, RoundedCornerShape(10.dp))
                        .clickable {
                            SoundManager.play(SoundManager.SfxType.UI_CLICK)
                            selectedPattern = id
                            onPatternSelected(id)
                        }
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(title, color = if (isSelected) Color(0xFF00E676) else Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(desc, color = Color(0xFFA0ABC0), fontSize = 10.sp)
                        }
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF00E676)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
