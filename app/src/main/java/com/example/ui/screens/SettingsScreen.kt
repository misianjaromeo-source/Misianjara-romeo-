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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

@Composable
fun SettingsScreen(
    userProfile: UserProfileData,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isAudioEnabled by remember { mutableStateOf(!SoundManager.isMuted) }
    var isVibrationEnabled by remember { mutableStateOf(true) }
    var is60FpsEnabled by remember { mutableStateOf(true) }
    var isCommentaryEnabled by remember { mutableStateOf(true) }

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
            IconButton(onClick = onBackClick, modifier = Modifier.testTag("btn_back_settings")) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text("PARAMÈTRES DU JEU", color = JflyGold, fontWeight = FontWeight.Black, fontSize = 16.sp)
                Text("Audio, graphismes 3D et commandes tactiles", color = Color(0xFFA0ABC0), fontSize = 11.sp)
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurface)
                .border(1.dp, DarkCardBorder, RoundedCornerShape(16.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("OPTIONS AUDIO & VISUELLES", color = Color(0xFFA0ABC0), fontWeight = FontWeight.Bold, fontSize = 12.sp)

            SettingToggleRow(
                title = "Effets Sonores & Commentaires",
                desc = "Sifflet de l'arbitre, frappes de balle, ambiance de stade",
                checked = isAudioEnabled,
                onCheckedChange = {
                    isAudioEnabled = it
                    SoundManager.isMuted = !it
                }
            )

            SettingToggleRow(
                title = "Moteur 3D Haute Performance (60 FPS)",
                desc = "Fluidité maximale pour le rendu des 22 joueurs",
                checked = is60FpsEnabled,
                onCheckedChange = { is60FpsEnabled = it }
            )

            SettingToggleRow(
                title = "Texte des Commentaires en direct",
                desc = "Affichage en bas de l'écran des actions clés du match",
                checked = isCommentaryEnabled,
                onCheckedChange = { isCommentaryEnabled = it }
            )

            SettingToggleRow(
                title = "Retour Haptique",
                desc = "Vibrations légères lors des tirs puissants et des buts",
                checked = isVibrationEnabled,
                onCheckedChange = { isVibrationEnabled = it }
            )
        }
    }
}

@Composable
fun SettingToggleRow(
    title: String,
    desc: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(DarkCard)
            .border(1.dp, DarkCardBorder, RoundedCornerShape(10.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(desc, color = Color(0xFFA0ABC0), fontSize = 10.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = JflyGold,
                checkedTrackColor = Color(0xFF382B0A)
            )
        )
    }
}
