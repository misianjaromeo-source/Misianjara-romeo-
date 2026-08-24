package com.example.game.engine

import com.example.data.model.FormationType
import com.example.data.model.PlayerData
import com.example.data.model.PositionType
import com.example.data.model.TeamData
import com.example.game.model.Ball
import com.example.game.model.BallTrailPoint
import com.example.game.model.FootballPlayer
import com.example.game.model.MatchPhase
import com.example.game.model.MatchState
import com.example.game.model.PlayerState
import com.example.game.model.TeamSide
import com.example.game.model.Vector3D
import com.example.sound.SoundManager
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

class MatchEngine {
    val ball = Ball()
    val homePlayers = mutableListOf<FootballPlayer>()
    val awayPlayers = mutableListOf<FootballPlayer>()
    val matchState = MatchState()

    var userControlledPlayer: FootballPlayer? = null
    var joystickX: Float = 0f
    var joystickY: Float = 0f
    var isSprintActive: Boolean = false
    var isShotCharging: Boolean = false
    var currentShotCharge: Float = 0f

    // Pitch bounds
    private val pitchW = 1000f
    private val pitchH = 650f

    fun setupMatch(
        homeTeamName: String = "JFLY FC",
        awayTeam: TeamData,
        squad: List<PlayerData>,
        formation: FormationType = FormationType.F_4_3_3,
        difficulty: String = "Semi-Pro"
    ) {
        homePlayers.clear()
        awayPlayers.clear()

        matchState.homeTeamName = homeTeamName
        matchState.awayTeamName = awayTeam.name
        matchState.homeColor = 0xFF111111
        matchState.awayColor = awayTeam.primaryColor
        matchState.difficultyLevel = difficulty
        matchState.homeScore = 0
        matchState.awayScore = 0
        matchState.matchSeconds = 0f
        matchState.phase = MatchPhase.KICKOFF
        matchState.commentaryText = "Coup d'envoi de JFLY vs ${awayTeam.name} !"

        // 1. Initialize Home 11 (Starters)
        val homeStarters = squad.filter { it.isStarter }.take(11)
        val homePositions = getFormationCoords(formation, isHome = true)

        for (i in 0 until 11) {
            val pData = if (i < homeStarters.size) homeStarters[i] else defaultPlayerData(i, isHome = true)
            val basePos = homePositions[i]
            val player = FootballPlayer(
                id = pData.id,
                name = pData.name,
                number = pData.number,
                team = TeamSide.HOME,
                positionType = pData.position,
                baseFormationPos = basePos.copyVector(),
                position = basePos.copyVector(),
                isCaptain = pData.isCaptain,
                ovr = pData.ovr,
                pace = pData.pace.toFloat(),
                shooting = pData.shooting.toFloat(),
                passing = pData.passing.toFloat(),
                dribbling = pData.dribbling.toFloat(),
                defending = pData.defending.toFloat(),
                physical = pData.physical.toFloat()
            )
            homePlayers.add(player)
        }

        // 2. Initialize Away 11
        val awayPositions = getFormationCoords(awayTeam.formation, isHome = false)
        for (i in 0 until 11) {
            val basePos = awayPositions[i]
            val posType = formation.positions.getOrElse(i) { PositionType.CM }
            val player = FootballPlayer(
                id = "away_$i",
                name = getAwayPlayerName(awayTeam.shortName, i),
                number = if (i == 0) 1 else if (i == 9) 10 else i + 2,
                team = TeamSide.AWAY,
                positionType = posType,
                baseFormationPos = basePos.copyVector(),
                position = basePos.copyVector(),
                ovr = awayTeam.rating,
                pace = (awayTeam.rating - 2 + Random.nextInt(5)).toFloat(),
                shooting = (awayTeam.rating - 3 + Random.nextInt(6)).toFloat(),
                passing = (awayTeam.rating - 2 + Random.nextInt(5)).toFloat(),
                dribbling = (awayTeam.rating - 2 + Random.nextInt(5)).toFloat(),
                defending = (awayTeam.rating - 2 + Random.nextInt(5)).toFloat(),
                physical = (awayTeam.rating - 2 + Random.nextInt(5)).toFloat()
            )
            awayPlayers.add(player)
        }

        // Set initial controlled player (Center Forward / JFLY Captain)
        val captain = homePlayers.find { it.isCaptain } ?: homePlayers.find { it.positionType == PositionType.ST } ?: homePlayers[9]
        setControlledPlayer(captain)

        // Reset ball to center spot
        ball.reset(500f, 325f)
        SoundManager.play(SoundManager.SfxType.WHISTLE)
    }

    private fun setControlledPlayer(player: FootballPlayer) {
        homePlayers.forEach { it.isControlled = false }
        player.isControlled = true
        userControlledPlayer = player
    }

    fun update(deltaSeconds: Float) {
        if (matchState.isPaused) return

        val dt = deltaSeconds.coerceIn(0.005f, 0.05f)

        // Update match timer
        if (matchState.phase == MatchPhase.PLAYING) {
            matchState.matchSeconds += dt * matchState.matchSpeedMultiplier
            if (matchState.matchSeconds >= 2700f && matchState.matchSeconds < 2710f) {
                // Half-time
                matchState.phase = MatchPhase.HALF_TIME
                matchState.commentaryText = "Mi-temps ! Pause tactique."
                SoundManager.play(SoundManager.SfxType.DOUBLE_WHISTLE)
            } else if (matchState.matchSeconds >= 5400f) {
                // Full time
                matchState.phase = MatchPhase.FULL_TIME
                matchState.commentaryText = "Coup de sifflet final ! Fin du match !"
                SoundManager.play(SoundManager.SfxType.LONG_WHISTLE)
            }
        }

        // Update Commentary timer
        if (matchState.commentaryTimer > 0f) {
            matchState.commentaryTimer -= dt
        }

        // Update Goal celebration timer
        if (matchState.phase == MatchPhase.GOAL_SCORED) {
            matchState.goalCelebrationTimer -= dt
            if (matchState.goalCelebrationTimer <= 0f) {
                resetAfterGoal()
            }
            return
        }

        // 1. Update Controlled Player Input
        userControlledPlayer?.let { player ->
            updateUserControlledPlayer(player, dt)
        }

        // 2. Update AI Players (Home and Away)
        val allPlayers = homePlayers + awayPlayers
        for (player in allPlayers) {
            if (player.isControlled) continue
            updateAiPlayer(player, dt)
        }

        // 3. Update Ball Physics
        updateBallPhysics(dt)

        // 4. Check Ball Possession & Collisions
        checkBallCollisions(dt)

        // 5. Check Goal Lines
        checkGoal()

        // Auto-switch defender if ball is far from currently controlled player
        autoSwitchDefenderIfNeeded()
    }

    private fun updateUserControlledPlayer(player: FootballPlayer, dt: Float) {
        val speed = player.speedMultiplier() * (if (isSprintActive && player.stamina > 10f) 1.4f else 1.0f)
        if (isSprintActive && player.stamina > 5f) {
            player.stamina = (player.stamina - dt * 15f).coerceAtLeast(0f)
            player.state = PlayerState.SPRINTING
        } else {
            player.stamina = (player.stamina + dt * 6f).coerceAtMost(100f)
            player.state = if (joystickX != 0f || joystickY != 0f) PlayerState.RUNNING else PlayerState.IDLE
        }

        if (joystickX != 0f || joystickY != 0f) {
            player.velocity.x = joystickX * speed
            player.velocity.y = joystickY * speed
            player.facingAngle = atan2(joystickY, joystickX)
            player.animFrame += dt * 12f
        } else {
            player.velocity.x *= 0.7f
            player.velocity.y *= 0.7f
        }

        // Update position
        player.position.x = (player.position.x + player.velocity.x).coerceIn(10f, pitchW - 10f)
        player.position.y = (player.position.y + player.velocity.y).coerceIn(10f, pitchH - 10f)

        // Shot Charging
        if (isShotCharging) {
            currentShotCharge = (currentShotCharge + dt * 1.8f).coerceAtMost(1.0f)
            player.shotPowerCharge = currentShotCharge
        } else {
            player.shotPowerCharge = 0f
        }

        // If player has ball, carry ball smoothly
        if (ball.isControlledBy == player) {
            val leadDist = 18f
            val ballTargetX = player.position.x + cos(player.facingAngle) * leadDist
            val ballTargetY = player.position.y + sin(player.facingAngle) * leadDist
            ball.position.x += (ballTargetX - ball.position.x) * 0.45f
            ball.position.y += (ballTargetY - ball.position.y) * 0.45f
            ball.position.z = 0f
            ball.velocity.set(player.velocity.x, player.velocity.y, 0f)
        }
    }

    private fun updateAiPlayer(player: FootballPlayer, dt: Float) {
        val isHome = player.team == TeamSide.HOME
        val teamHasBall = (ball.isControlledBy?.team == player.team)
        val ballDist = player.position.distanceTo(ball.position)

        // Goalkeeper behavior
        if (player.isGoalkeeper) {
            val goalX = if (isHome) 30f else pitchW - 30f
            val targetY = (325f + (ball.position.y - 325f) * 0.35f).coerceIn(280f, 370f)
            val targetX = if (ballDist < 120f && !teamHasBall) {
                // Rush out to smother ball
                if (isHome) (goalX + 50f).coerceAtMost(ball.position.x) else (goalX - 50f).coerceAtLeast(ball.position.x)
            } else {
                goalX
            }

            val dx = targetX - player.position.x
            val dy = targetY - player.position.y
            val dist = sqrt(dx * dx + dy * dy)
            if (dist > 3f) {
                val moveSpeed = 3.2f
                player.velocity.x = (dx / dist) * moveSpeed
                player.velocity.y = (dy / dist) * moveSpeed
                player.position.x += player.velocity.x
                player.position.y += player.velocity.y
                player.facingAngle = atan2(dy, dx)
                player.animFrame += dt * 8f
                player.state = PlayerState.RUNNING
            } else {
                player.state = PlayerState.GK_READY
                player.facingAngle = if (isHome) 0f else PI.toFloat()
            }
            return
        }

        // Field player AI logic
        val closestTeammateToBall = getClosestPlayerToBall(player.team)
        val isPresser = (closestTeammateToBall == player)

        val targetX: Float
        val targetY: Float

        if (isPresser && !teamHasBall) {
            // Chase ball to intercept/tackle
            targetX = ball.position.x
            targetY = ball.position.y
        } else if (teamHasBall) {
            // Offensive positioning: shift forward towards opponent goal
            val shiftX = if (isHome) 120f else -120f
            val ballShiftX = (ball.position.x - 500f) * 0.3f
            targetX = (player.baseFormationPos.x + shiftX + ballShiftX).coerceIn(40f, pitchW - 40f)
            targetY = (player.baseFormationPos.y + (ball.position.y - 325f) * 0.25f).coerceIn(30f, pitchH - 30f)
        } else {
            // Defensive positioning: drop back towards own goal
            val shiftX = if (isHome) -60f else 60f
            val ballShiftX = (ball.position.x - 500f) * 0.4f
            targetX = (player.baseFormationPos.x + shiftX + ballShiftX).coerceIn(40f, pitchW - 40f)
            targetY = (player.baseFormationPos.y + (ball.position.y - 325f) * 0.35f).coerceIn(30f, pitchH - 30f)
        }

        val dx = targetX - player.position.x
        val dy = targetY - player.position.y
        val dist = sqrt(dx * dx + dy * dy)

        if (dist > 8f) {
            val speed = player.speedMultiplier() * (if (isPresser) 1.25f else 0.85f)
            player.velocity.x = (dx / dist) * speed
            player.velocity.y = (dy / dist) * speed
            player.position.x += player.velocity.x
            player.position.y += player.velocity.y
            player.facingAngle = atan2(dy, dx)
            player.animFrame += dt * 10f
            player.state = PlayerState.RUNNING
        } else {
            player.velocity.x *= 0.5f
            player.velocity.y *= 0.5f
            player.state = PlayerState.IDLE
        }

        // AI decision when in possession of ball
        if (ball.isControlledBy == player) {
            val opponentGoalX = if (isHome) pitchW else 0f
            val distToGoal = (opponentGoalX - player.position.x).let { if (it < 0) -it else it }

            // Carry ball
            val leadDist = 18f
            val ballTargetX = player.position.x + cos(player.facingAngle) * leadDist
            val ballTargetY = player.position.y + sin(player.facingAngle) * leadDist
            ball.position.x += (ballTargetX - ball.position.x) * 0.45f
            ball.position.y += (ballTargetY - ball.position.y) * 0.45f
            ball.position.z = 0f

            // If close to goal, SHOOT!
            if (distToGoal < 240f && Random.nextFloat() < 0.04f) {
                aiShoot(player)
            } else if (Random.nextFloat() < 0.025f) {
                // Pass to teammate
                aiPass(player)
            }
        }
    }

    private fun aiPass(player: FootballPlayer) {
        val teammates = (if (player.team == TeamSide.HOME) homePlayers else awayPlayers).filter { it != player && !it.isGoalkeeper }
        val target = teammates.minByOrNull {
            val aheadBonus = if (player.team == TeamSide.HOME) -(it.position.x - player.position.x) else (it.position.x - player.position.x)
            player.position.distanceTo(it.position) + aheadBonus * 0.5f
        }
        if (target != null) {
            val angle = player.position.angleTo(target.position)
            val passSpeed = 16f
            ball.kick(cos(angle) * passSpeed, sin(angle) * passSpeed, 2f, player)
            SoundManager.play(SoundManager.SfxType.KICK_PASS)
        }
    }

    private fun aiShoot(player: FootballPlayer) {
        val isHome = player.team == TeamSide.HOME
        val goalX = if (isHome) pitchW - 10f else 10f
        val goalY = 325f + (Random.nextFloat() - 0.5f) * 80f
        val angle = player.position.angleTo(goalX, goalY)
        val shotPower = 21f + Random.nextFloat() * 4f
        val shotHeight = 6f + Random.nextFloat() * 6f

        ball.kick(cos(angle) * shotPower, sin(angle) * shotPower, shotHeight, player)
        SoundManager.play(SoundManager.SfxType.KICK_POWER_SHOT)
        matchState.commentaryText = "🔥 Tir puissant de ${player.name} !"
        matchState.commentaryTimer = 3f

        if (isHome) matchState.stats.homeShots++ else matchState.stats.awayShots++
    }

    // USER ACTIONS
    fun onPassButtonPressed() {
        val player = userControlledPlayer ?: return
        if (ball.isControlledBy != player && player.position.distanceTo(ball.position) > 35f) return

        // Find best teammate in facing direction or nearest forward
        val teammates = homePlayers.filter { it != player && !it.isGoalkeeper }
        var bestTeammate: FootballPlayer? = null
        var bestScore = Float.MAX_VALUE

        for (tm in teammates) {
            val angleToTm = player.position.angleTo(tm.position)
            val angleDiff = Math.abs(angleToTm - player.facingAngle).let { if (it > PI) 2 * PI - it else it }.toFloat()
            val dist = player.position.distanceTo(tm.position)

            // Favor teammates aligned with facing joystick direction
            val score = dist * (1f + angleDiff * 1.8f)
            if (score < bestScore) {
                bestScore = score
                bestTeammate = tm
            }
        }

        val target = bestTeammate ?: teammates.firstOrNull()
        if (target != null) {
            val angle = player.position.angleTo(target.position)
            val dist = player.position.distanceTo(target.position)
            val passSpeed = (dist * 0.12f).coerceIn(14f, 22f)
            val passHeight = if (dist > 180f) 5f else 1f

            ball.kick(cos(angle) * passSpeed, sin(angle) * passSpeed, passHeight, player)
            SoundManager.play(SoundManager.SfxType.KICK_PASS)
            matchState.stats.homePassesCompleted++
            matchState.commentaryText = "Superbe passe de ${player.name} vers ${target.name}"
            matchState.commentaryTimer = 2.5f

            // Auto-switch to receiving player
            setControlledPlayer(target)
        }
    }

    fun onShootButtonDown() {
        val player = userControlledPlayer ?: return
        if (ball.isControlledBy == player || player.position.distanceTo(ball.position) < 35f) {
            isShotCharging = true
            currentShotCharge = 0.1f
        }
    }

    fun onShootButtonRelease() {
        if (!isShotCharging) return
        isShotCharging = false
        val player = userControlledPlayer ?: return

        val power = currentShotCharge
        val goalX = pitchW - 5f
        val goalCornerY = if (joystickY < -0.2f) 290f else if (joystickY > 0.2f) 360f else 325f

        val angle = player.position.angleTo(goalX, goalCornerY)
        val shotSpeed = 16f + power * 14f // Up to 30 speed
        val shotHeight = 3f + power * 12f // Loft trajectory

        ball.kick(cos(angle) * shotSpeed, sin(angle) * shotSpeed, shotHeight, player, spin = joystickY * 2.5f)
        SoundManager.play(SoundManager.SfxType.KICK_POWER_SHOT)

        matchState.stats.homeShots++
        if (power > 0.7f) matchState.stats.homeShotsOnTarget++

        matchState.commentaryText = if (power > 0.8f) "🚀 FRAPPE SURPUISSANTE DE ${player.name} !!" else "Frappe cadrée de ${player.name} !"
        matchState.commentaryTimer = 3f
    }

    fun onTackleButtonPressed() {
        val player = userControlledPlayer ?: return
        player.state = PlayerState.TACKLING
        player.stateTimer = 0.4f
        SoundManager.play(SoundManager.SfxType.TACKLE)

        // Tackle burst forward
        val tackleAngle = player.facingAngle
        player.velocity.x = cos(tackleAngle) * 9.5f
        player.velocity.y = sin(tackleAngle) * 9.5f

        // Check if ball is in tackle range
        if (player.position.distanceTo(ball.position) < 45f) {
            ball.kick(cos(tackleAngle) * 12f, sin(tackleAngle) * 12f, 2f, player)
            ball.isControlledBy = player
            matchState.commentaryText = "Tacle parfait de ${player.name} qui récupère le ballon !"
            matchState.commentaryTimer = 2.5f
        }
    }

    fun onSwitchButtonPressed() {
        val closest = homePlayers
            .filter { !it.isGoalkeeper && it != userControlledPlayer }
            .minByOrNull { it.position.distanceTo(ball.position) }

        if (closest != null) {
            setControlledPlayer(closest)
            SoundManager.play(SoundManager.SfxType.UI_CLICK)
        }
    }

    private fun autoSwitchDefenderIfNeeded() {
        if (ball.isControlledBy?.team == TeamSide.AWAY) {
            val controlled = userControlledPlayer
            if (controlled != null && controlled.position.distanceTo(ball.position) > 280f) {
                val closest = homePlayers
                    .filter { !it.isGoalkeeper }
                    .minByOrNull { it.position.distanceTo(ball.position) }
                if (closest != null && closest != controlled) {
                    setControlledPlayer(closest)
                }
            }
        }
    }

    private fun updateBallPhysics(dt: Float) {
        if (ball.isControlledBy != null) {
            // Ball motion is tied to carrier
            return
        }

        // Apply velocity
        ball.position.x += ball.velocity.x
        ball.position.y += ball.velocity.y
        ball.position.z += ball.velocity.z

        // Gravity on Z axis
        val gravity = 0.65f
        ball.velocity.z -= gravity

        // Turf bounce and ground friction
        if (ball.position.z <= 0f) {
            ball.position.z = 0f
            if (ball.velocity.z < -1.5f) {
                ball.velocity.z = -ball.velocity.z * 0.55f // Bounce restitution
            } else {
                ball.velocity.z = 0f
            }
            // Ground friction
            ball.velocity.x *= 0.965f
            ball.velocity.y *= 0.965f
        } else {
            // Air drag
            ball.velocity.x *= 0.992f
            ball.velocity.y *= 0.992f
            // Curl / Spin effect
            ball.velocity.y += ball.spinX * 0.08f
        }

        // Update motion trail
        val speed = ball.velocity.length()
        if (speed > 10f) {
            ball.trail.add(0, BallTrailPoint(ball.position.x, ball.position.y, ball.position.z, 1.0f))
            if (ball.trail.size > 8) ball.trail.removeAt(ball.trail.size - 1)
        } else {
            if (ball.trail.isNotEmpty()) ball.trail.removeAt(ball.trail.size - 1)
        }

        // Pitch Out-of-Bounds Rebounds (Keep game flowing smoothly)
        if (ball.position.y < 5f) {
            ball.position.y = 5f
            ball.velocity.y = -ball.velocity.y * 0.6f
        } else if (ball.position.y > pitchH - 5f) {
            ball.position.y = pitchH - 5f
            ball.velocity.y = -ball.velocity.y * 0.6f
        }

        // Endline bounds (except inside goal mouth)
        val isGoalY = ball.position.y in 270f..380f
        if (!isGoalY) {
            if (ball.position.x < 5f) {
                ball.position.x = 5f
                ball.velocity.x = -ball.velocity.x * 0.6f
            } else if (ball.position.x > pitchW - 5f) {
                ball.position.x = pitchW - 5f
                ball.velocity.x = -ball.velocity.x * 0.6f
            }
        }
    }

    private fun checkBallCollisions(dt: Float) {
        if (ball.isControlledBy != null) return

        val allPlayers = homePlayers + awayPlayers
        for (player in allPlayers) {
            val dist = player.position.distance2D(ball.position)
            val captureDist = if (player.isGoalkeeper) 36f else 22f

            // Ball within player reach and low altitude
            if (dist < captureDist && ball.position.z < 25f) {
                // If Goalkeeper and saving a fast shot
                if (player.isGoalkeeper && ball.velocity.length() > 12f) {
                    // Goalkeeper diving save
                    player.state = if (ball.position.y < player.position.y) PlayerState.GK_DIVING_LEFT else PlayerState.GK_DIVING_RIGHT
                    ball.velocity.x = -ball.velocity.x * 0.4f + (Random.nextFloat() - 0.5f) * 6f
                    ball.velocity.y = (Random.nextFloat() - 0.5f) * 12f
                    ball.velocity.z = 4f
                    SoundManager.play(SoundManager.SfxType.TACKLE)
                    matchState.commentaryText = "🧤 ARRÊT DU GARDIEN ${player.name} !"
                    matchState.commentaryTimer = 3f
                    break
                }

                // Control / Steal Ball
                ball.isControlledBy = player
                ball.lastTouchPlayer = player
                ball.lastTouchTeam = player.team
                if (player.team == TeamSide.HOME && !player.isControlled) {
                    setControlledPlayer(player)
                }
                break
            }
        }
    }

    private fun checkGoal() {
        if (matchState.phase == MatchPhase.GOAL_SCORED) return

        val goalTopY = (pitchH - 120f) / 2f
        val goalBottomY = goalTopY + 120f

        // HOME GOAL (Right side x > pitchW)
        if (ball.position.x >= pitchW && ball.position.y in goalTopY..goalBottomY && ball.position.z < 35f) {
            matchState.homeScore++
            triggerGoal("JFLY FC", matchState.homeTeamName)
        }
        // AWAY GOAL (Left side x <= 0)
        else if (ball.position.x <= 0f && ball.position.y in goalTopY..goalBottomY && ball.position.z < 35f) {
            matchState.awayScore++
            triggerGoal(matchState.awayTeamName, matchState.awayTeamName)
        }
    }

    private fun triggerGoal(scoringTeam: String, scorerName: String) {
        matchState.phase = MatchPhase.GOAL_SCORED
        matchState.goalCelebrationTimer = 4.0f
        matchState.goalScorerText = "⚽ BUT POUR $scoringTeam !"
        matchState.commentaryText = "⚽ GOOOAAAL ! Magnifique but de $scoringTeam !"
        matchState.commentaryTimer = 4.0f

        SoundManager.play(SoundManager.SfxType.GOAL_ROAR)
        SoundManager.play(SoundManager.SfxType.LONG_WHISTLE)

        // All teammates celebrate
        val scorers = if (scoringTeam == "JFLY FC") homePlayers else awayPlayers
        scorers.forEach { it.state = PlayerState.CELEBRATING }
    }

    private fun resetAfterGoal() {
        matchState.phase = MatchPhase.PLAYING
        matchState.goalScorerText = null
        ball.reset(500f, 325f)

        // Reset player positions
        val homePositions = getFormationCoords(FormationType.F_4_3_3, isHome = true)
        val awayPositions = getFormationCoords(FormationType.F_4_3_3, isHome = false)

        homePlayers.forEachIndexed { i, p ->
            p.position = homePositions.getOrElse(i) { p.baseFormationPos }.copyVector()
            p.state = PlayerState.IDLE
        }
        awayPlayers.forEachIndexed { i, p ->
            p.position = awayPositions.getOrElse(i) { p.baseFormationPos }.copyVector()
            p.state = PlayerState.IDLE
        }

        val captain = homePlayers.find { it.isCaptain } ?: homePlayers[9]
        setControlledPlayer(captain)
        SoundManager.play(SoundManager.SfxType.WHISTLE)
    }

    private fun getClosestPlayerToBall(team: TeamSide): FootballPlayer? {
        val players = if (team == TeamSide.HOME) homePlayers else awayPlayers
        return players.filter { !it.isGoalkeeper }.minByOrNull { it.position.distanceTo(ball.position) }
    }

    private fun getFormationCoords(formation: FormationType, isHome: Boolean): List<Vector3D> {
        val dir = if (isHome) 1f else -1f
        val startX = if (isHome) 0f else pitchW

        return when (formation) {
            FormationType.F_4_3_3 -> listOf(
                // GK
                Vector3D(startX + dir * 35f, 325f),
                // LB, CB, CB, RB
                Vector3D(startX + dir * 180f, 130f),
                Vector3D(startX + dir * 160f, 260f),
                Vector3D(startX + dir * 160f, 390f),
                Vector3D(startX + dir * 180f, 520f),
                // CDM, CM, CAM
                Vector3D(startX + dir * 280f, 325f),
                Vector3D(startX + dir * 340f, 210f),
                Vector3D(startX + dir * 350f, 440f),
                // LW, ST, RW
                Vector3D(startX + dir * 430f, 130f),
                Vector3D(startX + dir * 450f, 325f),
                Vector3D(startX + dir * 430f, 520f)
            )
            FormationType.F_4_4_2 -> listOf(
                Vector3D(startX + dir * 35f, 325f),
                Vector3D(startX + dir * 180f, 130f),
                Vector3D(startX + dir * 160f, 260f),
                Vector3D(startX + dir * 160f, 390f),
                Vector3D(startX + dir * 180f, 520f),
                Vector3D(startX + dir * 320f, 120f),
                Vector3D(startX + dir * 300f, 270f),
                Vector3D(startX + dir * 300f, 380f),
                Vector3D(startX + dir * 320f, 530f),
                Vector3D(startX + dir * 440f, 270f),
                Vector3D(startX + dir * 440f, 380f)
            )
            FormationType.F_3_5_2 -> listOf(
                Vector3D(startX + dir * 35f, 325f),
                Vector3D(startX + dir * 170f, 200f),
                Vector3D(startX + dir * 150f, 325f),
                Vector3D(startX + dir * 170f, 450f),
                Vector3D(startX + dir * 310f, 110f),
                Vector3D(startX + dir * 280f, 260f),
                Vector3D(startX + dir * 320f, 325f),
                Vector3D(startX + dir * 280f, 390f),
                Vector3D(startX + dir * 310f, 540f),
                Vector3D(startX + dir * 440f, 260f),
                Vector3D(startX + dir * 440f, 390f)
            )
            FormationType.F_4_2_3_1 -> listOf(
                Vector3D(startX + dir * 35f, 325f),
                Vector3D(startX + dir * 180f, 130f),
                Vector3D(startX + dir * 160f, 260f),
                Vector3D(startX + dir * 160f, 390f),
                Vector3D(startX + dir * 180f, 520f),
                Vector3D(startX + dir * 270f, 260f),
                Vector3D(startX + dir * 270f, 390f),
                Vector3D(startX + dir * 360f, 140f),
                Vector3D(startX + dir * 380f, 325f),
                Vector3D(startX + dir * 360f, 510f),
                Vector3D(startX + dir * 450f, 325f)
            )
        }
    }

    private fun defaultPlayerData(idx: Int, isHome: Boolean): PlayerData {
        return PlayerData(
            id = "starter_$idx",
            name = if (idx == 9) "JFLY KING" else "Joueur $idx",
            number = if (idx == 9) 10 else idx + 1,
            position = PositionType.CM,
            ovr = 86,
            pace = 85,
            shooting = 82,
            passing = 84,
            dribbling = 85,
            defending = 75,
            physical = 80,
            isCaptain = (idx == 9)
        )
    }

    private fun getAwayPlayerName(shortName: String, idx: Int): String {
        val names = listOf("Courtois", "Militão", "Rüdiger", "Mendy", "Valverde", "Tchouaméni", "Bellingham", "Rodrygo", "Mbappé", "Vinicius Jr", "Güler")
        return names.getOrElse(idx) { "$shortName N°${idx + 1}" }
    }
}
