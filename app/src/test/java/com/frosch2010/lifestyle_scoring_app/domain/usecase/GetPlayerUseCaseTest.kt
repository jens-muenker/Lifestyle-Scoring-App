package com.frosch2010.lifestyle_scoring_app.domain.usecase

import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.PlayerRepository
import com.frosch2010.lifestyle_scoring_app.models.entities.Player
import org.junit.Assert.assertSame
import org.junit.Test
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Unit tests for [GetPlayerUseCase].
 */

class GetPlayerUseCaseTest {

    /**
     * Verifies `invoke_delegatesToRepository` behavior.
     */
    @Test
    fun invoke_delegatesToRepository() {
        val repo = mock<PlayerRepository>()
        val player = Player("R", arrayListOf())
        whenever(repo.getPlayer(0)).thenReturn(player)
        val useCase = GetPlayerUseCase(repo)

        val result = useCase(0)

        assertSame(player, result)
        verify(repo).getPlayer(eq(0))
    }
}
