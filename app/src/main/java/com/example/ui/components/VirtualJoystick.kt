package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun VirtualJoystick(
    onMove: (x: Float, y: Float) -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 150.dp
) {
    var thumbOffset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .size(size)
            .testTag("virtual_joystick_container")
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val center = Offset(size.toPx() / 2f, size.toPx() / 2f)
                        val delta = offset - center
                        val maxRadius = size.toPx() * 0.38f
                        val dist = delta.getDistance()
                        val clamped = if (dist > maxRadius) {
                            Offset(delta.x / dist * maxRadius, delta.y / dist * maxRadius)
                        } else delta
                        thumbOffset = clamped
                        onMove(clamped.x / maxRadius, clamped.y / maxRadius)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val newOffset = thumbOffset + dragAmount
                        val maxRadius = size.toPx() * 0.38f
                        val dist = newOffset.getDistance()
                        val clamped = if (dist > maxRadius) {
                            Offset(newOffset.x / dist * maxRadius, newOffset.y / dist * maxRadius)
                        } else newOffset
                        thumbOffset = clamped
                        onMove(clamped.x / maxRadius, clamped.y / maxRadius)
                    },
                    onDragEnd = {
                        thumbOffset = Offset.Zero
                        onMove(0f, 0f)
                    },
                    onDragCancel = {
                        thumbOffset = Offset.Zero
                        onMove(0f, 0f)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val outerRadius = this.size.width * 0.42f
            val innerRadius = this.size.width * 0.20f

            // Outer Base Ring
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x35000000), Color(0x60000000), Color(0x900A0E17)),
                    center = center,
                    radius = outerRadius
                ),
                radius = outerRadius,
                center = center
            )
            drawCircle(
                color = Color(0x66FFD700),
                radius = outerRadius,
                center = center,
                style = Stroke(width = 2.5f)
            )

            // Directional indicators (N, S, E, W)
            for (i in 0 until 4) {
                val angle = (i * Math.PI / 2).toFloat()
                val tickStart = Offset(center.x + cos(angle) * (outerRadius - 10f), center.y + sin(angle) * (outerRadius - 10f))
                val tickEnd = Offset(center.x + cos(angle) * outerRadius, center.y + sin(angle) * outerRadius)
                drawLine(Color(0x88FFD700), tickStart, tickEnd, strokeWidth = 2f)
            }

            // Joystick Thumb Knob
            val thumbCenter = center + thumbOffset
            drawCircle(
                color = Color(0x40000000),
                radius = innerRadius * 1.15f,
                center = thumbCenter + Offset(0f, 4f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFE57F), Color(0xFFFFD700), Color(0xFFC59B27)),
                    center = thumbCenter - Offset(4f, 4f),
                    radius = innerRadius * 1.2f
                ),
                radius = innerRadius,
                center = thumbCenter
            )
            drawCircle(
                color = Color(0xFF111111),
                radius = innerRadius * 0.35f,
                center = thumbCenter
            )
        }
    }
}
