package com.frosch2010.lifestyle_scoring_app.domain.usecase

import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.PlayerRepository
import com.frosch2010.lifestyle_scoring_app.models.entities.Player
import org.junit.Test
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

/**
 * Unit tests for [AddPlayerUseCase].
 */

class AddPlayerUseCaseTest {

    /**
     * Verifies `invoke_delegatesToRepository` behavior.
     */
    @Test
    fun invoke_delegatesToRepository() {
        val repo = mock<PlayerRepository>()
        val useCase = AddPlayerUseCase(repo)
        val player = Player("P", arrayListOf())

        useCase(player)

        verify(repo).addPlayer(eq(player))
    }
}
