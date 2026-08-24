package com.example.game.model

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class Vector3D(
    var x: Float = 0f,
    var y: Float = 0f,
    var z: Float = 0f
) {
    fun set(nx: Float, ny: Float, nz: Float = 0f) {
        x = nx
        y = ny
        z = nz
    }

    fun add(vx: Float, vy: Float, vz: Float = 0f) {
        x += vx
        y += vy
        z += vz
    }

    fun length(): Float = sqrt(x * x + y * y + z * z)
    fun length2D(): Float = sqrt(x * x + y * y)

    fun distanceTo(other: Vector3D): Float {
        val dx = x - other.x
        val dy = y - other.y
        val dz = z - other.z
        return sqrt(dx * dx + dy * dy + dz * dz)
    }

    fun distance2D(other: Vector3D): Float {
        val dx = x - other.x
        val dy = y - other.y
        return sqrt(dx * dx + dy * dy)
    }

    fun distanceTo(ox: Float, oy: Float): Float {
        val dx = x - ox
        val dy = y - oy
        return sqrt(dx * dx + dy * dy)
    }

    fun angleTo(target: Vector3D): Float {
        return atan2(target.y - y, target.x - x)
    }

    fun angleTo(tx: Float, ty: Float): Float {
        return atan2(ty - y, tx - x)
    }

    fun copyVector(): Vector3D = Vector3D(x, y, z)
}
