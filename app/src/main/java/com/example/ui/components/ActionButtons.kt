package com.example.ui.components

import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentGreen
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.AccentRed
import com.example.ui.theme.JflyGold
import com.example.ui.theme.JflyGoldDark
import com.example.ui.theme.NeonCyan

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ActionButtons(
    hasBall: Boolean,
    onPassClick: () -> Unit,
    onShootDown: () -> Unit,
    onShootRelease: () -> Unit,
    onSprintToggle: (Boolean) -> Unit,
    onTackleClick: () -> Unit,
    onSwitchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isSprintPressed by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.size(190.dp),
        contentAlignment = Alignment.Center
    ) {
        // TOP BUTTON: PASSE / SWITCH
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(62.dp)
                .testTag("btn_action_pass")
        ) {
            GameActionButton(
                label = if (hasBall) "PASSE" else "SWITCH",
                brush = Brush.verticalGradient(listOf(NeonCyan, Color(0xFF0288D1))),
                borderColor = NeonCyan,
                onClick = { if (hasBall) onPassClick() else onSwitchClick() },
                size = 62.dp
            )
        }

        // RIGHT BUTTON: TIR / TACLE (Power Button)
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(66.dp)
                .testTag("btn_action_shoot")
        ) {
            GameHoldActionButton(
                label = if (hasBall) "TIR" else "TACLE",
                brush = Brush.verticalGradient(
                    if (hasBall) listOf(Color(0xFFFF5252), AccentRed) else listOf(AccentOrange, Color(0xFFE65100))
                ),
                borderColor = if (hasBall) Color(0xFFFF8A80) else AccentOrange,
                onDown = { if (hasBall) onShootDown() else onTackleClick() },
                onUp = { if (hasBall) onShootRelease() },
                size = 66.dp
            )
        }

        // BOTTOM BUTTON: SPRINT (Turbo Boost)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .size(60.dp)
                .testTag("btn_action_sprint")
        ) {
            GameHoldActionButton(
                label = "SPRINT",
                brush = Brush.verticalGradient(listOf(Color(0xFF69F0AE), AccentGreen)),
                borderColor = Color(0xFFB9F6CA),
                onDown = {
                    isSprintPressed = true
                    onSprintToggle(true)
                },
                onUp = {
                    isSprintPressed = false
                    onSprintToggle(false)
                },
                size = 60.dp
            )
        }

        // LEFT BUTTON: DRIBBLE / PRESSING
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(56.dp)
                .testTag("btn_action_skill")
        ) {
            GameActionButton(
                label = if (hasBall) "FEINTE" else "PRESS",
                brush = Brush.verticalGradient(listOf(JflyGold, JflyGoldDark)),
                borderColor = JflyGold,
                onClick = {
                    if (hasBall) onTackleClick() else onSwitchClick()
                },
                size = 56.dp
            )
        }
    }
}

@Composable
fun GameActionButton(
    label: String,
    brush: Brush,
    borderColor: Color,
    onClick: () -> Unit,
    size: Dp
) {
    var isPressed by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .size(size)
            .scale(if (isPressed) 0.92f else 1.0f)
            .clip(CircleShape)
            .background(brush)
            .border(2.dp, borderColor, CircleShape)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { onClick() }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = Color.Black,
            fontWeight = FontWeight.Black,
            fontSize = if (size > 60.dp) 11.sp else 10.sp,
            letterSpacing = 0.5.sp
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GameHoldActionButton(
    label: String,
    brush: Brush,
    borderColor: Color,
    onDown: () -> Unit,
    onUp: () -> Unit,
    size: Dp
) {
    var isPressed by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .size(size)
            .scale(if (isPressed) 0.92f else 1.0f)
            .clip(CircleShape)
            .background(brush)
            .border(2.dp, borderColor, CircleShape)
            .pointerInteropFilter { event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        isPressed = true
                        onDown()
                        true
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        isPressed = false
                        onUp()
                        true
                    }
                    else -> false
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = Color.Black,
            fontWeight = FontWeight.Black,
            fontSize = if (size > 60.dp) 12.sp else 10.sp,
            letterSpacing = 0.5.sp
        )
    }
}
