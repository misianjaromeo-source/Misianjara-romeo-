package com.example.game.renderer

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.game.model.CameraMode
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class PitchRenderer {
    // Pitch dimensions: 0..1000 width, 0..650 height
    private val pitchWidth = 1000f
    private val pitchHeight = 650f

    fun drawStadiumAndPitch(
        drawScope: DrawScope,
        camera: Camera3D,
        screenWidth: Float,
        screenHeight: Float,
        cameraMode: CameraMode,
        stadiumMowPattern: String = "STRIPES", // "STRIPES", "DIAMOND", "CIRCULAR"
        animTime: Float = 0f
    ) {
        with(drawScope) {
            // 1. Draw Stadium Night Background & Crowd Stands
            drawStadiumStands(camera, screenWidth, screenHeight, cameraMode, animTime)

            // 2. Draw Pitch Turf Grass with Realistic Mowing Patterns
            drawTurfGrass(camera, screenWidth, screenHeight, cameraMode, stadiumMowPattern)

            // 3. Draw Pitch Markings (White Lines, Circles, Boxes, Penalty Spots)
            drawPitchMarkings(camera, screenWidth, screenHeight, cameraMode)

            // 4. Draw 3D Goalposts and Net Meshes
            drawGoalposts(camera, screenWidth, screenHeight, cameraMode)

            // 5. Draw Stadium Floodlight Atmospheric Beams
            drawFloodlightsAtmosphere(screenWidth, screenHeight, animTime)
        }
    }

    private fun DrawScope.drawStadiumStands(
        camera: Camera3D,
        screenWidth: Float,
        screenHeight: Float,
        cameraMode: CameraMode,
        animTime: Float
    ) {
        // Grand night arena background
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF040609),
                    Color(0xFF090E17),
                    Color(0xFF10192A)
                ),
                startY = 0f,
                endY = screenHeight
            )
        )

        // Project surrounding stadium apron
        val pTL = camera.project(-120f, -100f, 0f, screenWidth, screenHeight, cameraMode)
        val pTR = camera.project(1120f, -100f, 0f, screenWidth, screenHeight, cameraMode)
        val pBR = camera.project(1120f, 750f, 0f, screenWidth, screenHeight, cameraMode)
        val pBL = camera.project(-120f, 750f, 0f, screenWidth, screenHeight, cameraMode)

        val apronPath = Path().apply {
            moveTo(pTL.first, pTL.second)
            lineTo(pTR.first, pTR.second)
            lineTo(pBR.first, pBR.second)
            lineTo(pBL.first, pBL.second)
            close()
        }

        drawPath(
            path = apronPath,
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF131A26), Color(0xFF1C273A)),
                start = Offset(pTL.first, pTL.second),
                end = Offset(pBR.first, pBR.second)
            )
        )

        // Advertising LED Boards along sidelines
        val adTL = camera.project(-50f, -25f, 0f, screenWidth, screenHeight, cameraMode)
        val adTR = camera.project(1050f, -25f, 0f, screenWidth, screenHeight, cameraMode)
        val adTopTL = camera.project(-50f, -25f, 18f, screenWidth, screenHeight, cameraMode)
        val adTopTR = camera.project(1050f, -25f, 18f, screenWidth, screenHeight, cameraMode)

        val adPath = Path().apply {
            moveTo(adTopTL.first, adTopTL.second)
            lineTo(adTopTR.first, adTopTR.second)
            lineTo(adTR.first, adTR.second)
            lineTo(adTL.first, adTL.second)
            close()
        }
        drawPath(
            path = adPath,
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color(0xFFFFD700),
                    Color(0xFF111111),
                    Color(0xFFFFD700),
                    Color(0xFF00E5FF),
                    Color(0xFFFFD700)
                )
            )
        )
    }

    private fun DrawScope.drawTurfGrass(
        camera: Camera3D,
        screenWidth: Float,
        screenHeight: Float,
        cameraMode: CameraMode,
        pattern: String
    ) {
        val numStripes = 14
        val stripeWidth = pitchWidth / numStripes

        for (i in 0 until numStripes) {
            val xStart = i * stripeWidth
            val xEnd = (i + 1) * stripeWidth

            val pTL = camera.project(xStart, 0f, 0f, screenWidth, screenHeight, cameraMode)
            val pTR = camera.project(xEnd, 0f, 0f, screenWidth, screenHeight, cameraMode)
            val pBR = camera.project(xEnd, pitchHeight, 0f, screenWidth, screenHeight, cameraMode)
            val pBL = camera.project(xStart, pitchHeight, 0f, screenWidth, screenHeight, cameraMode)

            val stripePath = Path().apply {
                moveTo(pTL.first, pTL.second)
                lineTo(pTR.first, pTR.second)
                lineTo(pBR.first, pBR.second)
                lineTo(pBL.first, pBL.second)
                close()
            }

            val grassColor = if (i % 2 == 0) Color(0xFF1B6A2C) else Color(0xFF237F37)
            drawPath(stripePath, color = grassColor)
        }
    }

    private fun DrawScope.drawPitchMarkings(
        camera: Camera3D,
        screenWidth: Float,
        screenHeight: Float,
        cameraMode: CameraMode
    ) {
        val lineColor = Color(0xEEF8FAFC)
        val strokeWidth = 2.5f

        // Outer Boundary
        drawProjectedLine(camera, 0f, 0f, pitchWidth, 0f, screenWidth, screenHeight, cameraMode, lineColor, strokeWidth)
        drawProjectedLine(camera, pitchWidth, 0f, pitchWidth, pitchHeight, screenWidth, screenHeight, cameraMode, lineColor, strokeWidth)
        drawProjectedLine(camera, pitchWidth, pitchHeight, 0f, pitchHeight, screenWidth, screenHeight, cameraMode, lineColor, strokeWidth)
        drawProjectedLine(camera, 0f, pitchHeight, 0f, 0f, screenWidth, screenHeight, cameraMode, lineColor, strokeWidth)

        // Center Line & Center Circle
        drawProjectedLine(camera, 500f, 0f, 500f, pitchHeight, screenWidth, screenHeight, cameraMode, lineColor, strokeWidth)
        drawProjectedCircle(camera, 500f, 325f, 91.5f, screenWidth, screenHeight, cameraMode, lineColor, strokeWidth)
        drawProjectedFilledCircle(camera, 500f, 325f, 4f, screenWidth, screenHeight, cameraMode, lineColor)

        // Left Penalty Box (Home Defense / Away Attack)
        val pBoxWidth = 165f
        val pBoxTop = (pitchHeight - 403f) / 2f
        val pBoxBottom = pBoxTop + 403f
        drawProjectedLine(camera, 0f, pBoxTop, pBoxWidth, pBoxTop, screenWidth, screenHeight, cameraMode, lineColor, strokeWidth)
        drawProjectedLine(camera, pBoxWidth, pBoxTop, pBoxWidth, pBoxBottom, screenWidth, screenHeight, cameraMode, lineColor, strokeWidth)
        drawProjectedLine(camera, pBoxWidth, pBoxBottom, 0f, pBoxBottom, screenWidth, screenHeight, cameraMode, lineColor, strokeWidth)

        // Left Goal Box (6-yard box)
        val gBoxWidth = 55f
        val gBoxTop = (pitchHeight - 183f) / 2f
        val gBoxBottom = gBoxTop + 183f
        drawProjectedLine(camera, 0f, gBoxTop, gBoxWidth, gBoxTop, screenWidth, screenHeight, cameraMode, lineColor, strokeWidth)
        drawProjectedLine(camera, gBoxWidth, gBoxTop, gBoxWidth, gBoxBottom, screenWidth, screenHeight, cameraMode, lineColor, strokeWidth)
        drawProjectedLine(camera, gBoxWidth, gBoxBottom, 0f, gBoxBottom, screenWidth, screenHeight, cameraMode, lineColor, strokeWidth)
        drawProjectedFilledCircle(camera, 110f, 325f, 4f, screenWidth, screenHeight, cameraMode, lineColor) // Penalty spot

        // Right Penalty Box (Away Defense / Home Attack)
        val rpBoxLeft = pitchWidth - pBoxWidth
        drawProjectedLine(camera, pitchWidth, pBoxTop, rpBoxLeft, pBoxTop, screenWidth, screenHeight, cameraMode, lineColor, strokeWidth)
        drawProjectedLine(camera, rpBoxLeft, pBoxTop, rpBoxLeft, pBoxBottom, screenWidth, screenHeight, cameraMode, lineColor, strokeWidth)
        drawProjectedLine(camera, rpBoxLeft, pBoxBottom, pitchWidth, pBoxBottom, screenWidth, screenHeight, cameraMode, lineColor, strokeWidth)

        // Right Goal Box
        val rgBoxLeft = pitchWidth - gBoxWidth
        drawProjectedLine(camera, pitchWidth, gBoxTop, rgBoxLeft, gBoxTop, screenWidth, screenHeight, cameraMode, lineColor, strokeWidth)
        drawProjectedLine(camera, rgBoxLeft, gBoxTop, rgBoxLeft, gBoxBottom, screenWidth, screenHeight, cameraMode, lineColor, strokeWidth)
        drawProjectedLine(camera, rgBoxLeft, gBoxBottom, pitchWidth, gBoxBottom, screenWidth, screenHeight, cameraMode, lineColor, strokeWidth)
        drawProjectedFilledCircle(camera, pitchWidth - 110f, 325f, 4f, screenWidth, screenHeight, cameraMode, lineColor)

        // Corner Arcs
        drawProjectedArc(camera, 0f, 0f, 20f, 0f, 90f, screenWidth, screenHeight, cameraMode, lineColor, strokeWidth)
        drawProjectedArc(camera, pitchWidth, 0f, 20f, 90f, 180f, screenWidth, screenHeight, cameraMode, lineColor, strokeWidth)
        drawProjectedArc(camera, pitchWidth, pitchHeight, 20f, 180f, 270f, screenWidth, screenHeight, cameraMode, lineColor, strokeWidth)
        drawProjectedArc(camera, 0f, pitchHeight, 20f, 270f, 360f, screenWidth, screenHeight, cameraMode, lineColor, strokeWidth)
    }

    private fun DrawScope.drawGoalposts(
        camera: Camera3D,
        screenWidth: Float,
        screenHeight: Float,
        cameraMode: CameraMode
    ) {
        val goalTopY = (pitchHeight - 120f) / 2f
        val goalBottomY = goalTopY + 120f
        val goalDepth = 35f
        val goalHeight3D = 35f

        // LEFT GOAL
        val lPost1Ground = camera.project(0f, goalTopY, 0f, screenWidth, screenHeight, cameraMode)
        val lPost1Top = camera.project(0f, goalTopY, goalHeight3D, screenWidth, screenHeight, cameraMode)
        val lPost2Ground = camera.project(0f, goalBottomY, 0f, screenWidth, screenHeight, cameraMode)
        val lPost2Top = camera.project(0f, goalBottomY, goalHeight3D, screenWidth, screenHeight, cameraMode)

        val lBack1Top = camera.project(-goalDepth, goalTopY, goalHeight3D, screenWidth, screenHeight, cameraMode)
        val lBack2Top = camera.project(-goalDepth, goalBottomY, goalHeight3D, screenWidth, screenHeight, cameraMode)
        val lBack1Ground = camera.project(-goalDepth, goalTopY, 0f, screenWidth, screenHeight, cameraMode)
        val lBack2Ground = camera.project(-goalDepth, goalBottomY, 0f, screenWidth, screenHeight, cameraMode)

        // Left Net Mesh
        val netPathLeft = Path().apply {
            moveTo(lPost1Top.first, lPost1Top.second)
            lineTo(lBack1Top.first, lBack1Top.second)
            lineTo(lBack1Ground.first, lBack1Ground.second)
            lineTo(lBack2Ground.first, lBack2Ground.second)
            lineTo(lBack2Top.first, lBack2Top.second)
            lineTo(lPost2Top.first, lPost2Top.second)
            close()
        }
        drawPath(netPathLeft, color = Color(0x35FFFFFF))

        // Left Goal Frame (Poles & Crossbar)
        val poleColor = Color(0xFFFFFFFF)
        val poleStroke = 3.5f
        drawLine(poleColor, Offset(lPost1Ground.first, lPost1Ground.second), Offset(lPost1Top.first, lPost1Top.second), poleStroke)
        drawLine(poleColor, Offset(lPost2Ground.first, lPost2Ground.second), Offset(lPost2Top.first, lPost2Top.second), poleStroke)
        drawLine(poleColor, Offset(lPost1Top.first, lPost1Top.second), Offset(lPost2Top.first, lPost2Top.second), poleStroke)

        // RIGHT GOAL
        val rPost1Ground = camera.project(pitchWidth, goalTopY, 0f, screenWidth, screenHeight, cameraMode)
        val rPost1Top = camera.project(pitchWidth, goalTopY, goalHeight3D, screenWidth, screenHeight, cameraMode)
        val rPost2Ground = camera.project(pitchWidth, goalBottomY, 0f, screenWidth, screenHeight, cameraMode)
        val rPost2Top = camera.project(pitchWidth, goalBottomY, goalHeight3D, screenWidth, screenHeight, cameraMode)

        val rBack1Top = camera.project(pitchWidth + goalDepth, goalTopY, goalHeight3D, screenWidth, screenHeight, cameraMode)
        val rBack2Top = camera.project(pitchWidth + goalDepth, goalBottomY, goalHeight3D, screenWidth, screenHeight, cameraMode)
        val rBack1Ground = camera.project(pitchWidth + goalDepth, goalTopY, 0f, screenWidth, screenHeight, cameraMode)
        val rBack2Ground = camera.project(pitchWidth + goalDepth, goalBottomY, 0f, screenWidth, screenHeight, cameraMode)

        val netPathRight = Path().apply {
            moveTo(rPost1Top.first, rPost1Top.second)
            lineTo(rBack1Top.first, rBack1Top.second)
            lineTo(rBack1Ground.first, rBack1Ground.second)
            lineTo(rBack2Ground.first, rBack2Ground.second)
            lineTo(rBack2Top.first, rBack2Top.second)
            lineTo(rPost2Top.first, rPost2Top.second)
            close()
        }
        drawPath(netPathRight, color = Color(0x35FFFFFF))

        drawLine(poleColor, Offset(rPost1Ground.first, rPost1Ground.second), Offset(rPost1Top.first, rPost1Top.second), poleStroke)
        drawLine(poleColor, Offset(rPost2Ground.first, rPost2Ground.second), Offset(rPost2Top.first, rPost2Top.second), poleStroke)
        drawLine(poleColor, Offset(rPost1Top.first, rPost1Top.second), Offset(rPost2Top.first, rPost2Top.second), poleStroke)
    }

    private fun DrawScope.drawFloodlightsAtmosphere(screenWidth: Float, screenHeight: Float, animTime: Float) {
        // Glowing stadium floodlights in corners
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0x33FFFFFF), Color(0x10FFD700), Color(0x00000000)),
                center = Offset(screenWidth * 0.15f, 0f),
                radius = screenWidth * 0.45f
            ),
            radius = screenWidth * 0.45f,
            center = Offset(screenWidth * 0.15f, 0f)
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0x33FFFFFF), Color(0x1000E5FF), Color(0x00000000)),
                center = Offset(screenWidth * 0.85f, 0f),
                radius = screenWidth * 0.45f
            ),
            radius = screenWidth * 0.45f,
            center = Offset(screenWidth * 0.85f, 0f)
        )
    }

    private fun DrawScope.drawProjectedLine(
        camera: Camera3D,
        x1: Float, y1: Float, x2: Float, y2: Float,
        screenWidth: Float, screenHeight: Float,
        cameraMode: CameraMode,
        color: Color, strokeWidth: Float
    ) {
        val p1 = camera.project(x1, y1, 0f, screenWidth, screenHeight, cameraMode)
        val p2 = camera.project(x2, y2, 0f, screenWidth, screenHeight, cameraMode)
        drawLine(color, Offset(p1.first, p1.second), Offset(p2.first, p2.second), strokeWidth * ((p1.third + p2.third) / 2f))
    }

    private fun DrawScope.drawProjectedCircle(
        camera: Camera3D,
        cx: Float, cy: Float, radius: Float,
        screenWidth: Float, screenHeight: Float,
        cameraMode: CameraMode,
        color: Color, strokeWidth: Float
    ) {
        val segments = 24
        val path = Path()
        for (i in 0..segments) {
            val angle = (i * 2 * PI / segments).toFloat()
            val px = cx + radius * cos(angle)
            val py = cy + radius * sin(angle)
            val proj = camera.project(px, py, 0f, screenWidth, screenHeight, cameraMode)
            if (i == 0) path.moveTo(proj.first, proj.second) else path.lineTo(proj.first, proj.second)
        }
        val centerProj = camera.project(cx, cy, 0f, screenWidth, screenHeight, cameraMode)
        drawPath(path, color = color, style = Stroke(width = strokeWidth * centerProj.third))
    }

    private fun DrawScope.drawProjectedFilledCircle(
        camera: Camera3D,
        cx: Float, cy: Float, radius: Float,
        screenWidth: Float, screenHeight: Float,
        cameraMode: CameraMode,
        color: Color
    ) {
        val proj = camera.project(cx, cy, 0f, screenWidth, screenHeight, cameraMode)
        drawCircle(color, radius = radius * proj.third, center = Offset(proj.first, proj.second))
    }

    private fun DrawScope.drawProjectedArc(
        camera: Camera3D,
        cx: Float, cy: Float, radius: Float,
        startDeg: Float, sweepDeg: Float,
        screenWidth: Float, screenHeight: Float,
        cameraMode: CameraMode,
        color: Color, strokeWidth: Float
    ) {
        val segments = 8
        val path = Path()
        for (i in 0..segments) {
            val angle = ((startDeg + i * (sweepDeg / segments)) * PI / 180.0).toFloat()
            val px = cx + radius * cos(angle)
            val py = cy + radius * sin(angle)
            val proj = camera.project(px, py, 0f, screenWidth, screenHeight, cameraMode)
            if (i == 0) path.moveTo(proj.first, proj.second) else path.lineTo(proj.first, proj.second)
        }
        val centerProj = camera.project(cx, cy, 0f, screenWidth, screenHeight, cameraMode)
        drawPath(path, color = color, style = Stroke(width = strokeWidth * centerProj.third))
    }
}
