package com.frosch2010.lifestyle_scoring_app.models.repositories

import com.frosch2010.lifestyle_scoring_app.data.repository.impl.InMemoryPlayerRepository
import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.PlayerRepository
import com.frosch2010.lifestyle_scoring_app.data.source.local.impl.InMemoryPlayerLocalDataSource
import com.frosch2010.lifestyle_scoring_app.models.entities.Player
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for the [PlayerRepository] port backed by [InMemoryPlayerRepository] and [InMemoryPlayerLocalDataSource].
 */
class PlayerRepositoryTest {

    private lateinit var playerRepository: PlayerRepository

    /**
     * Constructs a fresh [InMemoryPlayerRepository] so mutations from one test do not affect the next.
     */
    @Before
    fun setUp() {
        playerRepository = InMemoryPlayerRepository(InMemoryPlayerLocalDataSource())
    }

    /**
     * [PlayerRepository.addPlayer] appends the player and exposes it at the end of [PlayerRepository.getPlayers].
     */
    @Test
    fun testAddPlayer() {
        // Arrange
        val player = Player("John Doe", arrayListOf())

        // Act
        playerRepository.addPlayer(player)

        // Assert
        assertEquals(1, playerRepository.getPlayers().size)
        assertEquals(player, playerRepository.getPlayers()[0])
    }

    /**
     * [PlayerRepository.deletePlayer] removes the entry at the given index and clears the roster when the last slot is deleted.
     */
    @Test
    fun testRemovePlayer() {
        // Arrange
        val player = Player("Jane Doe", arrayListOf())
        playerRepository.addPlayer(player)

        // Act
        playerRepository.deletePlayer(0)

        // Assert
        assertEquals(0, playerRepository.getPlayers().size)
    }

    /**
     * [PlayerRepository.updatePlayer] replaces the entity at the index while keeping list size unchanged.
     */
    @Test
    fun testUpdatePlayer() {
        // Arrange
        val originalPlayer = Player("Original Player", arrayListOf())
        playerRepository.addPlayer(originalPlayer)

        val updatedPlayer = Player("Updated Player", arrayListOf())

        // Act
        playerRepository.updatePlayer(0, updatedPlayer)

        // Assert
        assertEquals(1, playerRepository.getPlayers().size)
        assertEquals(updatedPlayer, playerRepository.getPlayers()[0])
        assertNotEquals(originalPlayer, playerRepository.getPlayers()[0])
    }

    /**
     * [PlayerRepository.getPlayer] returns the entity previously stored at that roster index.
     */
    @Test
    fun testGetPlayer() {
        // Arrange
        val player = Player("Test Player", arrayListOf())
        playerRepository.addPlayer(player)

        // Act
        val retrievedPlayer = playerRepository.getPlayer(0)

        // Assert
        assertEquals(player, retrievedPlayer)
    }
}
