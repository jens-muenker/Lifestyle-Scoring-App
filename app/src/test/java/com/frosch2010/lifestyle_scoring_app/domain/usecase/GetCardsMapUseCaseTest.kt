package com.frosch2010.lifestyle_scoring_app.domain.usecase

import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.CardsRepository
import com.frosch2010.lifestyle_scoring_app.models.entities.AnimalCard
import com.frosch2010.lifestyle_scoring_app.models.enums.AnimalTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Unit tests for [GetCardsMapUseCase].
 */

class GetCardsMapUseCaseTest {

    /**
     * Verifies `invoke_delegatesToRepository` behavior.
     */
    @Test
    fun invoke_delegatesToRepository() {
        val repo = mock<CardsRepository>()
        val useCase = GetCardsMapUseCase(repo)
        val map = mapOf("Cat" to AnimalCard(CardTypeEnum.ANIMAL, AnimalTypeEnum.CAT))
        whenever(repo.getCardsMap()).thenReturn(map)

        val result = useCase()

        assertEquals(map, result)
        verify(repo).getCardsMap()
    }
}
