package com.example.game.renderer

import androidx.compose.ui.geometry.Offset
import com.example.game.model.CameraMode
import com.example.game.model.Vector3D
import kotlin.math.cos
import kotlin.math.sin

class Camera3D {
    var targetX: Float = 500f
    var targetY: Float = 325f
    var currentX: Float = 500f
    var currentY: Float = 325f

    // Camera parameters
    var pitchAngle: Float = 0.58f // ~33 degrees tilt
    var zoom: Float = 1.0f
    var basePitchWidth: Float = 1000f
    var basePitchHeight: Float = 650f

    fun update(ballPos: Vector3D, controlledPlayerPos: Vector3D?, cameraMode: CameraMode, delta: Float) {
        val target = when (cameraMode) {
            CameraMode.PLAYER_FOCUS -> controlledPlayerPos ?: ballPos
            CameraMode.DYNAMIC_TV -> {
                // Blend between ball and controlled player with lead
                val player = controlledPlayerPos ?: ballPos
                Vector3D(
                    (ballPos.x * 0.65f + player.x * 0.35f),
                    (ballPos.y * 0.65f + player.y * 0.35f)
                )
            }
            CameraMode.TACTICAL_TOP -> ballPos
            CameraMode.BROADCAST_3D -> {
                // Sideline broadcast camera tracking ball X with bounded Y
                Vector3D(ballPos.x, 325f + (ballPos.y - 325f) * 0.45f)
            }
        }

        // Clamp camera target so pitch remains visible
        val clampedX = target.x.coerceIn(200f, 800f)
        val clampedY = target.y.coerceIn(160f, 490f)

        val smoothFactor = (delta * 6.5f).coerceIn(0.05f, 0.4f)
        currentX += (clampedX - currentX) * smoothFactor
        currentY += (clampedY - currentY) * smoothFactor
    }

    /**
     * Transforms 3D world coordinate (x, y, z) into 2D Screen Canvas (sx, sy, scale)
     */
    fun project(
        worldX: Float,
        worldY: Float,
        worldZ: Float,
        screenWidth: Float,
        screenHeight: Float,
        cameraMode: CameraMode = CameraMode.BROADCAST_3D
    ): Triple<Float, Float, Float> {
        val centerX = screenWidth / 2f
        val centerY = screenHeight / 2f

        // Relative coordinates to camera center
        val relX = worldX - currentX
        val relY = worldY - currentY

        val effectiveZoom = when (cameraMode) {
            CameraMode.TACTICAL_TOP -> (screenWidth / 1100f) * 0.9f
            CameraMode.PLAYER_FOCUS -> (screenWidth / 700f) * 1.25f
            CameraMode.DYNAMIC_TV -> (screenWidth / 850f) * 1.1f
            CameraMode.BROADCAST_3D -> (screenWidth / 920f) * 1.05f
        } * zoom

        val yPerspective = when (cameraMode) {
            CameraMode.TACTICAL_TOP -> 1.0f
            else -> 0.68f // Vertical perspective foreshortening
        }

        // Depth perspective scale: items higher on screen (smaller worldY) appear slightly further
        val depthFactor = if (cameraMode == CameraMode.TACTICAL_TOP) 1.0f else (1.0f + (relY / 800f) * 0.35f)
        val scale = effectiveZoom * depthFactor

        val screenX = centerX + relX * effectiveZoom * depthFactor
        val screenY = centerY + relY * effectiveZoom * yPerspective - (worldZ * scale * 1.35f)

        return Triple(screenX, screenY, scale)
    }

    fun screenToWorld(
        screenX: Float,
        screenY: Float,
        screenWidth: Float,
        screenHeight: Float,
        cameraMode: CameraMode
    ): Offset {
        val centerX = screenWidth / 2f
        val centerY = screenHeight / 2f
        val effectiveZoom = (screenWidth / 920f) * zoom
        val yPerspective = if (cameraMode == CameraMode.TACTICAL_TOP) 1.0f else 0.68f

        val relX = (screenX - centerX) / effectiveZoom
        val relY = (screenY - centerY) / (effectiveZoom * yPerspective)

        return Offset(currentX + relX, currentY + relY)
    }
}
