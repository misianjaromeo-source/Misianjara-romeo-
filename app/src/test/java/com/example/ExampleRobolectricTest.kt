package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.FormationType
import com.example.data.model.PlayerData
import com.example.data.model.PositionType
import com.example.data.model.TeamData
import com.example.game.engine.MatchEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("JFLY Football", appName)
    }

    @Test
    fun `test match engine initialization and 22 players on pitch`() {
        val homeSquad = (1..11).map {
            PlayerData(
                id = "home_$it",
                name = "Home Player $it",
                number = it,
                position = PositionType.CM,
                ovr = 85,
                pace = 80,
                shooting = 80,
                passing = 80,
                dribbling = 80,
                defending = 80,
                physical = 80,
                isStarter = true
            )
        }
        val awayTeam = TeamData(
            id = "away_1",
            name = "Test Opponent",
            shortName = "TST",
            primaryColor = 0xFF0000FF,
            secondaryColor = 0xFFFFFFFF,
            rating = 85,
            formation = FormationType.F_4_3_3
        )

        val engine = MatchEngine(
            homeSquad = homeSquad,
            homeFormation = FormationType.F_4_3_3,
            awayTeam = awayTeam,
            difficulty = "Semi-Pro"
        )

        assertNotNull(engine.players)
        assertEquals(22, engine.players.size)
        assertEquals(11, engine.homePlayers.size)
        assertEquals(11, engine.awayPlayers.size)
        assertNotNull(engine.ball)
    }

    @Test
    fun `test shoot mechanics`() {
        val homeSquad = (1..11).map {
            PlayerData(
                id = "p_$it",
                name = "Player $it",
                number = it,
                position = PositionType.CM,
                ovr = 85,
                pace = 80,
                shooting = 90,
                passing = 80,
                dribbling = 80,
                defending = 80,
                physical = 80,
                isStarter = true
            )
        }
        val awayTeam = TeamData("away", "Opponent", "OPP", 0xFFFFFFFF, 0xFF000000, 80)
        val engine = MatchEngine(homeSquad, FormationType.F_4_3_3, awayTeam)

        engine.userShoot(1.0f)
        assertTrue(engine.ball.vz > 0f || engine.ball.vx != 0f || engine.ball.vy != 0f)
    }
}
