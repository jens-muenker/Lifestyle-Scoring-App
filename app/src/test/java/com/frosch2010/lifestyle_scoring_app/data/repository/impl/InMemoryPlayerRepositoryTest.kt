package com.frosch2010.lifestyle_scoring_app.data.repository.impl

import com.frosch2010.lifestyle_scoring_app.data.source.local.interfaces.PlayerLocalDataSource
import com.frosch2010.lifestyle_scoring_app.models.entities.Player
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

/**
 * Unit tests for [InMemoryPlayerRepository]: verifies delegation to [PlayerLocalDataSource].
 */
class InMemoryPlayerRepositoryTest {

    /**
     * Verifies `addPlayer_delegatesToDataSource` behavior.
     */
    @Test
    fun addPlayer_delegatesToDataSource() {
        val fake = FakePlayerLocalDataSource()
        val repository = InMemoryPlayerRepository(fake)
        val player = Player("A", arrayListOf())

        repository.addPlayer(player)

        assertEquals(1, fake.players.size)
        assertSame(player, fake.players[0])
    }

    /**
     * Verifies `getPlayers_delegatesToDataSource` behavior.
     */
    @Test
    fun getPlayers_delegatesToDataSource() {
        val fake = FakePlayerLocalDataSource()
        val p = Player("B", arrayListOf())
        fake.players.add(p)
        val repository = InMemoryPlayerRepository(fake)

        assertSame(fake.players, repository.getPlayers())
    }

    /**
     * Verifies `deletePlayer_delegatesToDataSource` behavior.
     */
    @Test
    fun deletePlayer_delegatesToDataSource() {
        val fake = FakePlayerLocalDataSource()
        fake.players.add(Player("C", arrayListOf()))
        val repository = InMemoryPlayerRepository(fake)

        repository.deletePlayer(0)

        assertEquals(0, fake.players.size)
    }

    /**
     * Verifies `updatePlayer_delegatesToDataSource` behavior.
     */
    @Test
    fun updatePlayer_delegatesToDataSource() {
        val fake = FakePlayerLocalDataSource()
        fake.players.add(Player("Old", arrayListOf()))
        val repository = InMemoryPlayerRepository(fake)
        val updated = Player("New", arrayListOf())

        repository.updatePlayer(0, updated)

        assertSame(updated, fake.players[0])
    }

    /**
     * Verifies `getPlayer_delegatesToDataSource` behavior.
     */
    @Test
    fun getPlayer_delegatesToDataSource() {
        val fake = FakePlayerLocalDataSource()
        val p = Player("D", arrayListOf())
        fake.players.add(p)
        val repository = InMemoryPlayerRepository(fake)

        assertSame(p, repository.getPlayer(0))
    }

    private class FakePlayerLocalDataSource : PlayerLocalDataSource {
        val players = ArrayList<Player>()

        override fun addPlayer(player: Player) {
            players.add(player)
        }

        override fun getPlayers(): List<Player> = players

        override fun deletePlayer(index: Int) {
            players.removeAt(index)
        }

        override fun updatePlayer(index: Int, player: Player) {
            players[index] = player
        }

        override fun getPlayer(index: Int): Player = players[index]
    }
}
