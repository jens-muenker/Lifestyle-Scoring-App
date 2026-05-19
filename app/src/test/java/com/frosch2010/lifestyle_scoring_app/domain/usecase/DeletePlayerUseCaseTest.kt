package com.frosch2010.lifestyle_scoring_app.domain.usecase

import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.PlayerRepository
import org.junit.Test
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

/**
 * Unit tests for [DeletePlayerUseCase].
 */

class DeletePlayerUseCaseTest {

    /**
     * Verifies `invoke_delegatesToRepository` behavior.
     */
    @Test
    fun invoke_delegatesToRepository() {
        val repo = mock<PlayerRepository>()
        val useCase = DeletePlayerUseCase(repo)

        useCase(2)

        verify(repo).deletePlayer(eq(2))
    }
}
