package com.example.game.renderer

import android.graphics.Paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import com.example.game.model.CameraMode
import com.example.game.model.FootballPlayer
import com.example.game.model.PlayerState
import com.example.game.model.TeamSide
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class PlayerRenderer {

    fun drawPlayers(
        drawScope: DrawScope,
        players: List<FootballPlayer>,
        camera: Camera3D,
        screenWidth: Float,
        screenHeight: Float,
        cameraMode: CameraMode,
        homeKitPrimary: Color,
        homeKitSecondary: Color,
        awayKitPrimary: Color,
        awayKitSecondary: Color,
        gkKitHome: Color,
        gkKitAway: Color
    ) {
        // Sort players by Y coordinate for correct depth layering (back-to-front rendering)
        val sortedPlayers = players.sortedBy { it.position.y }

        with(drawScope) {
            for (player in sortedPlayers) {
                drawSinglePlayer(
                    player = player,
                    camera = camera,
                    screenWidth = screenWidth,
                    screenHeight = screenHeight,
                    cameraMode = cameraMode,
                    homePrimary = homeKitPrimary,
                    homeSecondary = homeKitSecondary,
                    awayPrimary = awayKitPrimary,
                    awaySecondary = awayKitSecondary,
                    gkHome = gkKitHome,
                    gkAway = gkKitAway
                )
            }
        }
    }

    private fun DrawScope.drawSinglePlayer(
        player: FootballPlayer,
        camera: Camera3D,
        screenWidth: Float,
        screenHeight: Float,
        cameraMode: CameraMode,
        homePrimary: Color,
        homeSecondary: Color,
        awayPrimary: Color,
        awaySecondary: Color,
        gkHome: Color,
        gkAway: Color
    ) {
        val proj = camera.project(player.position.x, player.position.y, player.position.z, screenWidth, screenHeight, cameraMode)
        val groundProj = camera.project(player.position.x, player.position.y, 0f, screenWidth, screenHeight, cameraMode)

        val px = proj.first
        val py = proj.second
        val scale = proj.third

        val baseRadius = 13f * scale
        val playerHeight = 36f * scale

        // 1. Dynamic Drop Shadow on Turf
        val shadowRadiusX = baseRadius * 1.35f
        val shadowRadiusY = baseRadius * 0.75f
        drawOval(
            color = Color(0x60000000),
            topLeft = Offset(groundProj.first - shadowRadiusX, groundProj.second - shadowRadiusY),
            size = Size(shadowRadiusX * 2, shadowRadiusY * 2)
        )

        // Kit Colors determination
        val primaryColor = when {
            player.isGoalkeeper && player.team == TeamSide.HOME -> gkHome
            player.isGoalkeeper && player.team == TeamSide.AWAY -> gkAway
            player.team == TeamSide.HOME -> homePrimary
            else -> awayPrimary
        }
        val secondaryColor = when {
            player.isGoalkeeper -> Color.White
            player.team == TeamSide.HOME -> homeSecondary
            else -> awaySecondary
        }

        // Animation Stride Leg Offsets
        val isMoving = player.velocity.length2D() > 0.4f
        val legStride = if (isMoving) sin(player.animFrame) * 8f * scale else 0f
        val facing = player.facingAngle
        val cosFacing = cos(facing)
        val sinFacing = sin(facing)

        // 2. Draw Legs & Cleats
        val legWidth = 4.5f * scale
        val legLength = 14f * scale
        val leftLegX = px - (sinFacing * 5f * scale) + (cosFacing * legStride)
        val rightLegX = px + (sinFacing * 5f * scale) - (cosFacing * legStride)
        val legY = py - (playerHeight * 0.35f)

        // Shorts Color (Secondary or Black)
        val shortsColor = if (player.team == TeamSide.HOME) Color(0xFF111111) else Color(0xFF003366)
        drawCircle(shortsColor, radius = baseRadius * 0.9f, center = Offset(px, py - (playerHeight * 0.38f)))

        // Left & Right Legs
        drawLine(
            Color(player.skinTone),
            Offset(leftLegX, legY),
            Offset(leftLegX + cosFacing * 4f * scale, py),
            strokeWidth = legWidth
        )
        drawLine(
            Color(player.skinTone),
            Offset(rightLegX, legY),
            Offset(rightLegX - cosFacing * 4f * scale, py),
            strokeWidth = legWidth
        )

        // Cleats / Boots (Gold for Captain/JFLY, White/Neon for others)
        val bootColor = if (player.isCaptain) Color(0xFFFFD700) else Color(0xFFFFFFFF)
        drawCircle(bootColor, radius = 3.5f * scale, center = Offset(leftLegX + cosFacing * 4f * scale, py))
        drawCircle(bootColor, radius = 3.5f * scale, center = Offset(rightLegX - cosFacing * 4f * scale, py))

        // 3. Torso / Jersey (3D Shaded Oval)
        val torsoY = py - (playerHeight * 0.65f)
        val torsoRadiusX = baseRadius * 1.15f
        val torsoRadiusY = baseRadius * 1.35f

        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(primaryColor, primaryColor.copy(alpha = 0.85f), Color(0xFF000000)),
                center = Offset(px - 3f * scale, torsoY - 4f * scale),
                radius = torsoRadiusX * 1.5f
            ),
            topLeft = Offset(px - torsoRadiusX, torsoY - torsoRadiusY),
            size = Size(torsoRadiusX * 2, torsoRadiusY * 2)
        )

        // Jersey Stripes or Gold Sash Accent
        drawCircle(
            secondaryColor,
            radius = 3.5f * scale,
            center = Offset(px, torsoY)
        )

        // 4. Arms & Hands
        val armSwing = if (isMoving) -sin(player.animFrame) * 6f * scale else 0f
        val leftArmX = px - (sinFacing * 12f * scale)
        val rightArmX = px + (sinFacing * 12f * scale)
        val armY = torsoY - 2f * scale

        drawLine(
            primaryColor,
            Offset(leftArmX, armY),
            Offset(leftArmX - cosFacing * armSwing, armY + 10f * scale),
            strokeWidth = 4f * scale
        )
        drawLine(
            primaryColor,
            Offset(rightArmX, armY),
            Offset(rightArmX + cosFacing * armSwing, armY + 10f * scale),
            strokeWidth = 4f * scale
        )

        // Goalkeeper Gloves or Skin Tone Hands
        val handColor = if (player.isGoalkeeper) Color(0xFF00E5FF) else Color(player.skinTone)
        drawCircle(handColor, radius = 3.2f * scale, center = Offset(leftArmX - cosFacing * armSwing, armY + 10f * scale))
        drawCircle(handColor, radius = 3.2f * scale, center = Offset(rightArmX + cosFacing * armSwing, armY + 10f * scale))

        // Captain Armband
        if (player.isCaptain) {
            drawCircle(Color(0xFFFFD700), radius = 2.5f * scale, center = Offset(leftArmX, armY + 4f * scale))
        }

        // 5. Head, Hair & Face Orientation
        val headY = py - (playerHeight * 1.05f)
        val headRadius = baseRadius * 0.78f

        // Head Base Skin Tone
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(player.skinTone), Color(player.skinTone).copy(alpha = 0.8f)),
                center = Offset(px - 2f * scale, headY - 2f * scale),
                radius = headRadius
            ),
            radius = headRadius,
            center = Offset(px, headY)
        )

        // Hair (Layer on top back of head)
        drawCircle(
            Color(player.hairColor),
            radius = headRadius * 0.92f,
            center = Offset(px - cosFacing * 2.5f * scale, headY - 2.5f * scale)
        )

        // 6. Overhead Indicators (Cursor, Name & Stamina for Controlled Player)
        if (player.isControlled) {
            // Golden Chevron Cursor
            val chevronY = headY - 18f * scale
            val chevronPath = Path().apply {
                moveTo(px, chevronY + 8f * scale)
                lineTo(px - 9f * scale, chevronY - 5f * scale)
                lineTo(px + 9f * scale, chevronY - 5f * scale)
                close()
            }
            drawPath(
                path = chevronPath,
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFD700), Color(0xFFFFA000))
                )
            )

            // Stamina & Power Ring around feet
            drawCircle(
                color = Color(0x80FFD700),
                radius = baseRadius * 2.0f,
                center = Offset(groundProj.first, groundProj.second),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5f * scale)
            )

            // Shot Power Bar when charging
            if (player.shotPowerCharge > 0.05f) {
                val barWidth = 45f * scale
                val barHeight = 6f * scale
                val barX = px - barWidth / 2f
                val barY = headY - 30f * scale

                // Bar Background
                drawRect(
                    color = Color(0xAA000000),
                    topLeft = Offset(barX, barY),
                    size = Size(barWidth, barHeight)
                )

                // Charged Fill (Green -> Yellow -> Red)
                val chargeColor = when {
                    player.shotPowerCharge < 0.6f -> Color(0xFF00E676)
                    player.shotPowerCharge < 0.85f -> Color(0xFFFFD700)
                    else -> Color(0xFFFF3D71)
                }
                drawRect(
                    color = chargeColor,
                    topLeft = Offset(barX, barY),
                    size = Size(barWidth * player.shotPowerCharge, barHeight)
                )
            }
        }

        // Draw Player Name Tag & Number using native Canvas
        drawIntoCanvas { canvas ->
            val textPaint = Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 10f * scale.coerceAtLeast(0.8f)
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
                setShadowLayer(4f, 0f, 1f, android.graphics.Color.BLACK)
                isFakeBoldText = true
            }

            // Draw player short name above head
            val displayName = if (player.isControlled) "★ ${player.name} (${player.number})" else player.name
            canvas.nativeCanvas.drawText(displayName, px, headY - 7f * scale, textPaint)
        }
    }
}
