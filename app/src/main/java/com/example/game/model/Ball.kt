package com.example.game.model

data class BallTrailPoint(
    val x: Float,
    val y: Float,
    val z: Float,
    val alpha: Float
)

data class Ball(
    var position: Vector3D = Vector3D(500f, 325f, 0f),
    var velocity: Vector3D = Vector3D(0f, 0f, 0f),
    var spinX: Float = 0f,
    var spinY: Float = 0f,
    var isControlledBy: FootballPlayer? = null,
    var lastTouchTeam: TeamSide = TeamSide.HOME,
    var lastTouchPlayer: FootballPlayer? = null,
    var inGoal: Boolean = false,
    val trail: MutableList<BallTrailPoint> = mutableListOf()
) {
    fun reset(x: Float = 500f, y: Float = 325f) {
        position.set(x, y, 0f)
        velocity.set(0f, 0f, 0f)
        spinX = 0f
        spinY = 0f
        isControlledBy = null
        inGoal = false
        trail.clear()
    }

    fun kick(vx: Float, vy: Float, vz: Float, player: FootballPlayer, spin: Float = 0f) {
        isControlledBy = null
        lastTouchPlayer = player
        lastTouchTeam = player.team
        velocity.set(vx, vy, vz)
        spinX = spin
    }
}
