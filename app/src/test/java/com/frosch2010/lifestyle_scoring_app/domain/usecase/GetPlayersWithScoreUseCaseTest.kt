package com.frosch2010.lifestyle_scoring_app.domain.usecase

import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.PlayerRepository
import com.frosch2010.lifestyle_scoring_app.domain.model.PlayerWithScore
import com.frosch2010.lifestyle_scoring_app.models.entities.Player
import com.frosch2010.lifestyle_scoring_app.services.interfaces.IPlayerPointsCalculationService
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.same
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Unit tests for [GetPlayersWithScoreUseCase].
 */

class GetPlayersWithScoreUseCaseTest {

    /**
     * Verifies `invoke_mapsPlayersUsingPointsService` behavior.
     */
    @Test
    fun invoke_mapsPlayersUsingPointsService() {
        val repo = mock<PlayerRepository>()
        val service = mock<IPlayerPointsCalculationService>()
        val p1 = Player("A", arrayListOf())
        val p2 = Player("B", arrayListOf())
        whenever(repo.getPlayers()).thenReturn(listOf(p1, p2))
        whenever(service.calculatePlayerPoints(any())).thenReturn(10).thenReturn(3)
        val useCase = GetPlayersWithScoreUseCase(repo, service)

        val rows = useCase()

        assertEquals(
            listOf(
                PlayerWithScore(name = "A", score = 10),
                PlayerWithScore(name = "B", score = 3)
            ),
            rows
        )
        verify(service).calculatePlayerPoints(same(p1.cards))
        verify(service).calculatePlayerPoints(same(p2.cards))
    }
}
