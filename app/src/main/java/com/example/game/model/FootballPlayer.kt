package com.example.game.model

import com.example.data.model.PositionType

enum class TeamSide {
    HOME, AWAY
}

enum class PlayerState {
    IDLE,
    RUNNING,
    SPRINTING,
    PASSING,
    SHOOTING,
    TACKLING,
    CELEBRATING,
    GK_READY,
    GK_DIVING_LEFT,
    GK_DIVING_RIGHT,
    GK_CATCHING,
    STUNNED
}

data class FootballPlayer(
    val id: String,
    val name: String,
    val number: Int,
    val team: TeamSide,
    val positionType: PositionType,
    val baseFormationPos: Vector3D, // Standard position on pitch (0..1000, 0..650)
    var position: Vector3D,
    var velocity: Vector3D = Vector3D(0f, 0f, 0f),
    var facingAngle: Float = 0f,
    var state: PlayerState = PlayerState.IDLE,
    var stateTimer: Float = 0f,
    var animFrame: Float = 0f,
    var stamina: Float = 100f,
    var isControlled: Boolean = false,
    var isCaptain: Boolean = false,
    val ovr: Int = 85,
    val pace: Float = 85f,
    val shooting: Float = 80f,
    val passing: Float = 82f,
    val dribbling: Float = 84f,
    val defending: Float = 75f,
    val physical: Float = 80f,
    val skinTone: Long = 0xFFD79668,
    val hairColor: Long = 0xFF1C130B,
    var shotPowerCharge: Float = 0f
) {
    val isGoalkeeper: Boolean get() = positionType == PositionType.GK

    fun speedMultiplier(): Float {
        val baseSpeed = (pace / 100f) * 4.2f + 2.0f
        return when (state) {
            PlayerState.SPRINTING -> baseSpeed * 1.45f
            PlayerState.TACKLING -> baseSpeed * 1.6f
            PlayerState.SHOOTING, PlayerState.PASSING -> baseSpeed * 0.3f
            PlayerState.IDLE -> 0f
            else -> baseSpeed
        }
    }
}
