package com.frosch2010.lifestyle_scoring_app.domain.usecase

import com.frosch2010.lifestyle_scoring_app.models.entities.AnimalCard
import com.frosch2010.lifestyle_scoring_app.models.enums.AnimalTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for [AddCardUseCase].
 */
class AddCardUseCaseTest {

    private val useCase = AddCardUseCase()

    /**
     * Verifies `invoke_returnsReady_whenNoDuplicate()` behavior.
     */
    @Test
    fun invoke_returnsReady_whenNoDuplicate() {
        val card = AnimalCard(CardTypeEnum.ANIMAL, AnimalTypeEnum.CAT)
        val existing = listOf(AnimalCard(CardTypeEnum.ANIMAL, AnimalTypeEnum.DOG))

        val result = useCase(card, existing)

        assertEquals(AddCardUseCaseResult.Ready, result)
    }

    /**
     * Verifies `invoke_returnsDuplicate_whenSameCardAlreadyPresent()` behavior.
     */
    @Test
    fun invoke_returnsDuplicate_whenSameCardAlreadyPresent() {
        val card = AnimalCard(CardTypeEnum.ANIMAL, AnimalTypeEnum.CAT)
        val existing = listOf(AnimalCard(CardTypeEnum.ANIMAL, AnimalTypeEnum.CAT))

        val result = useCase(card, existing)

        assertEquals(AddCardUseCaseResult.Duplicate, result)
    }
}
