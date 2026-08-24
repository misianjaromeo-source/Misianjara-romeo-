package com.example.game.renderer

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.example.game.model.Ball
import com.example.game.model.CameraMode
import kotlin.math.cos
import kotlin.math.sin

class BallRenderer {

    fun drawBall(
        drawScope: DrawScope,
        ball: Ball,
        camera: Camera3D,
        screenWidth: Float,
        screenHeight: Float,
        cameraMode: CameraMode,
        ballDesign: String = "GOLD_CROWN"
    ) {
        with(drawScope) {
            // 1. Draw Speed Motion Trail
            for (i in ball.trail.indices) {
                val pt = ball.trail[i]
                val trailProj = camera.project(pt.x, pt.y, pt.z, screenWidth, screenHeight, cameraMode)
                val trailRadius = 6f * trailProj.third * (pt.alpha)
                drawCircle(
                    color = Color(0xFFFFD700).copy(alpha = pt.alpha * 0.4f),
                    radius = trailRadius,
                    center = Offset(trailProj.first, trailProj.second)
                )
            }

            // 2. Projected Ground Shadow (on Turf)
            val groundProj = camera.project(ball.position.x, ball.position.y, 0f, screenWidth, screenHeight, cameraMode)
            val altitudeFactor = (1f - (ball.position.z / 120f)).coerceIn(0.2f, 1.0f)
            val shadowRadiusX = (9f * groundProj.third * altitudeFactor)
            val shadowRadiusY = (5.5f * groundProj.third * altitudeFactor)

            drawOval(
                color = Color.Black.copy(alpha = 0.55f * altitudeFactor),
                topLeft = Offset(groundProj.first - shadowRadiusX, groundProj.second - shadowRadiusY),
                size = Size(shadowRadiusX * 2, shadowRadiusY * 2)
            )

            // 3. 3D Ball Sphere
            val ballProj = camera.project(ball.position.x, ball.position.y, ball.position.z, screenWidth, screenHeight, cameraMode)
            val bx = ballProj.first
            val by = ballProj.second
            val scale = ballProj.third
            val ballRadius = 8.5f * scale

            // Ball Outer Sphere with 3D Light Highlight
            val ballColors = if (ballDesign == "GOLD_CROWN") {
                listOf(Color(0xFFFFFFFF), Color(0xFFFFE082), Color(0xFFFFB300), Color(0xFF8D6E00))
            } else {
                listOf(Color(0xFFFFFFFF), Color(0xFFF0F0F0), Color(0xFFD9D9D9), Color(0xFF333333))
            }

            drawCircle(
                brush = Brush.radialGradient(
                    colors = ballColors,
                    center = Offset(bx - ballRadius * 0.35f, by - ballRadius * 0.35f),
                    radius = ballRadius * 1.3f
                ),
                radius = ballRadius,
                center = Offset(bx, by)
            )

            // Ball Pentagons / Geometric Seam Pattern
            val spinAngle = (ball.position.x + ball.position.y) * 0.08f
            val patchRadius = ballRadius * 0.42f

            // Central Pentagon
            drawCircle(
                color = if (ballDesign == "GOLD_CROWN") Color(0xFF1A1A1A) else Color(0xFF111111),
                radius = patchRadius * 0.75f,
                center = Offset(bx + cos(spinAngle) * ballRadius * 0.25f, by + sin(spinAngle) * ballRadius * 0.25f)
            )

            // Surrounding patches
            for (p in 0 until 5) {
                val angle = spinAngle + (p * 2 * Math.PI / 5).toFloat()
                val px = bx + cos(angle) * (ballRadius * 0.65f)
                val py = by + sin(angle) * (ballRadius * 0.65f)
                drawCircle(
                    color = if (ballDesign == "GOLD_CROWN") Color(0xFFB8860B) else Color(0xFF222222),
                    radius = patchRadius * 0.45f,
                    center = Offset(px, py)
                )
            }

            // Specular Shine
            drawCircle(
                color = Color.White.copy(alpha = 0.8f),
                radius = ballRadius * 0.22f,
                center = Offset(bx - ballRadius * 0.4f, by - ballRadius * 0.4f)
            )
        }
    }
}
