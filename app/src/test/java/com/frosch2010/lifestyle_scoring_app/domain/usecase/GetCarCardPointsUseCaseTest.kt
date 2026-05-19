package com.frosch2010.lifestyle_scoring_app.domain.usecase

import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.CardsRepository
import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Unit tests for [GetCarCardPointsUseCase].
 */

class GetCarCardPointsUseCaseTest {

    /**
     * Verifies `invoke_delegatesToRepository` behavior.
     */
    @Test
    fun invoke_delegatesToRepository() {
        val repo = mock<CardsRepository>()
        val useCase = GetCarCardPointsUseCase(repo)
        val card = FakeCard(CardTypeEnum.CAR)
        whenever(repo.getCarCardPoints(card)).thenReturn(listOf(1, 2, 3))

        val points = useCase(card)

        assertEquals(listOf(1, 2, 3), points)
        verify(repo).getCarCardPoints(card)
    }

    private data class FakeCard(override val cardType: CardTypeEnum) : ICard
}
