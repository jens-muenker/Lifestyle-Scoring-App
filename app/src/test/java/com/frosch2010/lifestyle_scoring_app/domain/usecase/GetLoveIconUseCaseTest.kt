package com.frosch2010.lifestyle_scoring_app.domain.usecase

import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.CardsRepository
import com.frosch2010.lifestyle_scoring_app.models.entities.LoveCard
import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.LoveTypeEnum
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Unit tests for [GetLoveIconUseCase].
 */

class GetLoveIconUseCaseTest {

    /**
     * Verifies `invoke_delegatesToRepository` behavior.
     */
    @Test
    fun invoke_delegatesToRepository() {
        val repo = mock<CardsRepository>()
        val useCase = GetLoveIconUseCase(repo)
        val card = LoveCard(CardTypeEnum.LOVE, LoveTypeEnum.HOUSE)
        whenever(repo.getLoveIcon(card)).thenReturn(1234)

        val icon = useCase(card)

        assertEquals(1234, icon)
        verify(repo).getLoveIcon(card)
    }
}
