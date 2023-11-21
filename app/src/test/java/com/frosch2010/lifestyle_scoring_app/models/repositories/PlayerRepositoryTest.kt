package com.frosch2010.lifestyle_scoring_app.models.repositories

import com.frosch2010.lifestyle_scoring_app.models.entities.Player
import com.frosch2010.lifestyle_scoring_app.models.interfaces.IPlayerRepository
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

/**
 * This class tests the PlayerRepository class.
 * @autor Jens Münker
 */
@RunWith(MockitoJUnitRunner::class)
class PlayerRepositoryTest {

    private lateinit var playerRepository: IPlayerRepository

    @Before
    fun setUp() {
        playerRepository = PlayerRepository()
    }

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
