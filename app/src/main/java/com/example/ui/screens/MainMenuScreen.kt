package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Sports
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Stadium
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.UserProfileData
import com.example.sound.SoundManager
import com.example.ui.components.TopHeaderBar
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkCard
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.JflyGold
import com.example.ui.theme.JflyGoldDark
import com.example.ui.theme.NeonCyan

@Composable
fun MainMenuScreen(
    userProfile: UserProfileData,
    onQuickMatchClick: () -> Unit,
    onSeasonLeagueClick: () -> Unit,
    onCupTournamentClick: () -> Unit,
    onTeamManagementClick: () -> Unit,
    onShopClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onDailyEventsClick: () -> Unit,
    onCareerModeClick: () -> Unit,
    onStadiumClick: () -> Unit,
    onProfileClick: () -> Unit,
    onMessagesClick: () -> Unit,
    onFriendsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // 1. NIGHT FOOTBALL STADIUM & HERO PLAYER BACKGROUND
        Image(
            painter = painterResource(id = R.drawable.img_jfly_hero),
            contentDescription = "JFLY Hero Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Gradient Dark Overlays for high readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xEE0A0C12),
                            Color(0x440A0C12),
                            Color(0xD90A0C12),
                            Color(0xFF0A0C12)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 8.dp)
        ) {
            // 2. TOP HEADER BAR
            TopHeaderBar(
                userProfile = userProfile,
                onProfileClick = onProfileClick,
                onMessagesClick = onMessagesClick,
                onFriendsClick = onFriendsClick,
                onSettingsClick = onSettingsClick
            )

            // 3. JFLY GRAND LOGO WITH CROWN & FOOTBALL
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xCC0D111A))
                        .border(1.5.dp, JflyGold.copy(alpha = 0.7f), RoundedCornerShape(18.dp))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_app_icon),
                        contentDescription = "JFLY Logo",
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "JFLY FOOTBALL",
                            color = JflyGold,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "11 VS 11 • REAL 3D ENGINE",
                            color = Color(0xFFB0B8C8),
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            letterSpacing = 1.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 4. MAIN MENU BUTTONS (Left and Right Navigation Clusters)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // LEFT COLUMN: Core Game Modes & Management
                Column(
                    modifier = Modifier.weight(1.1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // MAIN ACTION: JOUER - MATCH RAPIDE
                    MainMenuCardButton(
                        title = "JOUER",
                        subtitle = "MATCH RAPIDE 11 vs 11",
                        icon = Icons.Default.PlayCircle,
                        accentColor = JflyGold,
                        isProminent = true,
                        onClick = {
                            SoundManager.play(SoundManager.SfxType.UI_CLICK)
                            onQuickMatchClick()
                        },
                        tag = "btn_menu_play_quick"
                    )

                    // CHAMPIONNAT - MODE SAISON
                    MainMenuCardButton(
                        title = "CHAMPIONNAT",
                        subtitle = "MODE SAISON 16 CLUBS",
                        icon = Icons.Default.EmojiEvents,
                        accentColor = AccentGreen,
                        onClick = {
                            SoundManager.play(SoundManager.SfxType.UI_CLICK)
                            onSeasonLeagueClick()
                        },
                        tag = "btn_menu_league"
                    )

                    // COUPE - TOURNOI
                    MainMenuCardButton(
                        title = "COUPE",
                        subtitle = "TOURNOI À ÉLIMINATION",
                        icon = Icons.Default.MilitaryTech,
                        accentColor = NeonCyan,
                        onClick = {
                            SoundManager.play(SoundManager.SfxType.UI_CLICK)
                            onCupTournamentClick()
                        },
                        tag = "btn_menu_cup"
                    )

                    // MON ÉQUIPE - JOUEURS & MAILLOTS
                    MainMenuCardButton(
                        title = "MON ÉQUIPE",
                        subtitle = "TACTIQUE & EFFECTIF",
                        icon = Icons.Default.Group,
                        accentColor = Color(0xFFB388FF),
                        onClick = {
                            SoundManager.play(SoundManager.SfxType.UI_CLICK)
                            onTeamManagementClick()
                        },
                        tag = "btn_menu_team"
                    )

                    // BOUTIQUE - ACHETER DES OBJETS
                    MainMenuCardButton(
                        title = "BOUTIQUE",
                        subtitle = "PACKS & MAILLOTS",
                        icon = Icons.Default.ShoppingBag,
                        accentColor = JflyGold,
                        onClick = {
                            SoundManager.play(SoundManager.SfxType.UI_CLICK)
                            onShopClick()
                        },
                        tag = "btn_menu_shop"
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // RIGHT COLUMN: Events, Career, Stadium & Settings
                Column(
                    modifier = Modifier.weight(0.9f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // ÉVÉNEMENTS - DÉFIS QUOTIDIENS
                    MainMenuCardButton(
                        title = "ÉVÉNEMENTS",
                        subtitle = "DÉFIS DU JOUR",
                        icon = Icons.Default.Star,
                        accentColor = AccentOrange,
                        onClick = {
                            SoundManager.play(SoundManager.SfxType.UI_CLICK)
                            onDailyEventsClick()
                        },
                        tag = "btn_menu_events"
                    )

                    // CARRIÈRE - DEVENIR UNE LÉGENDE
                    MainMenuCardButton(
                        title = "CARRIÈRE",
                        subtitle = "DEVENIR LÉGENDE",
                        icon = Icons.Default.Sports,
                        accentColor = JflyGold,
                        onClick = {
                            SoundManager.play(SoundManager.SfxType.UI_CLICK)
                            onCareerModeClick()
                        },
                        tag = "btn_menu_career"
                    )

                    // STADE - PERSONNALISER
                    MainMenuCardButton(
                        title = "STADE",
                        subtitle = "PERSONNALISER",
                        icon = Icons.Default.Stadium,
                        accentColor = NeonCyan,
                        onClick = {
                            SoundManager.play(SoundManager.SfxType.UI_CLICK)
                            onStadiumClick()
                        },
                        tag = "btn_menu_stadium"
                    )

                    // PARAMÈTRES - OPTIONS
                    MainMenuCardButton(
                        title = "PARAMÈTRES",
                        subtitle = "OPTIONS & AUDIO",
                        icon = Icons.Default.Settings,
                        accentColor = Color.White,
                        onClick = {
                            SoundManager.play(SoundManager.SfxType.UI_CLICK)
                            onSettingsClick()
                        },
                        tag = "btn_menu_settings"
                    )
                }
            }
        }
    }
}

@Composable
fun MainMenuCardButton(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    isProminent: Boolean = false,
    tag: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (isProminent) {
                    Brush.horizontalGradient(listOf(Color(0xFF241D08), Color(0xFF382B0A)))
                } else {
                    Brush.horizontalGradient(listOf(Color(0xE6131824), Color(0xCC1A2130)))
                }
            )
            .border(
                width = if (isProminent) 2.dp else 1.dp,
                color = if (isProminent) JflyGold else DarkCardBorder,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = if (isProminent) 10.dp else 8.dp)
            .testTag(tag)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(if (isProminent) 36.dp else 30.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.2f))
                    .border(1.dp, accentColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(if (isProminent) 22.dp else 18.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    color = if (isProminent) JflyGold else Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = if (isProminent) 15.sp else 13.sp,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = subtitle,
                    color = Color(0xFF909BB0),
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp
                )
            }
        }
    }
}
