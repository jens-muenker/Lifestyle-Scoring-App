package com.frosch2010.lifestyle_scoring_app.domain.usecase

import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.PlayerRepository
import com.frosch2010.lifestyle_scoring_app.models.entities.Player
import org.junit.Test
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

/**
 * Unit tests for [UpdatePlayerUseCase].
 */

class UpdatePlayerUseCaseTest {

    /**
     * Verifies `invoke_delegatesToRepository` behavior.
     */
    @Test
    fun invoke_delegatesToRepository() {
        val repo = mock<PlayerRepository>()
        val useCase = UpdatePlayerUseCase(repo)
        val player = Player("Q", arrayListOf())

        useCase(1, player)

        verify(repo).updatePlayer(eq(1), eq(player))
    }
}
