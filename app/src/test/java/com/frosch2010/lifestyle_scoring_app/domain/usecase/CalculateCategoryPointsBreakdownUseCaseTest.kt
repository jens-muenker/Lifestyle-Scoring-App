package com.frosch2010.lifestyle_scoring_app.domain.usecase

import com.frosch2010.lifestyle_scoring_app.domain.model.CategoryPointsBreakdown
import com.frosch2010.lifestyle_scoring_app.domain.model.CategoryPointsEntry
import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import com.frosch2010.lifestyle_scoring_app.services.impl.PlayerPointsCalculationService
import com.frosch2010.lifestyle_scoring_app.services.interfaces.IPlayerPointsCalculationService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [CalculateCategoryPointsBreakdownUseCase].
 */
class CalculateCategoryPointsBreakdownUseCaseTest {

    /**
     * Asserts [CalculateCategoryPointsBreakdownUseCase] returns the [CategoryPointsBreakdown] from
     * [IPlayerPointsCalculationService.calculateCategoryPointsBreakdown] and forwards the hand once (tracked via [FakePointsService.calls]).
     */
    @Test
    fun invoke_delegatesToPointsService() {
        val expected = CategoryPointsBreakdown(
            totalPoints = 21,
            entries = listOf(
                CategoryPointsEntry(CardTypeEnum.ANIMAL, 1),
                CategoryPointsEntry(CardTypeEnum.CAR, 2)
            )
        )
        val service = FakePointsService(expected)
        val useCase = CalculateCategoryPointsBreakdownUseCase(service)

        val result = useCase(listOf(FakeCard(CardTypeEnum.ANIMAL)))

        assertEquals(expected, result)
        assertEquals(1, service.calls)
    }

    /**
     * With [PlayerPointsCalculationService], an empty hand yields total zero and one [CategoryPointsEntry] per
     * [CardTypeEnum] in enum declaration order, each with zero direct category points.
     */
    @Test
    fun invoke_emptyHand_returnsBreakdownInEnumOrderWithZeros() {
        val useCase = CalculateCategoryPointsBreakdownUseCase(PlayerPointsCalculationService())

        val breakdown = useCase(emptyList())

        assertEquals(0, breakdown.totalPoints)
        assertEquals(CardTypeEnum.entries, breakdown.entries.map { it.category })
        assertTrue(breakdown.entries.all { it.points == 0 })
    }

    private class FakePointsService(
        private val breakdown: CategoryPointsBreakdown
    ) : IPlayerPointsCalculationService {
        var calls: Int = 0

        /**
         * Not used by these tests; fails fast if invoked unexpectedly.
         *
         * @param cards ignored
         * @throws IllegalStateException always via [error]
         */
        override fun calculatePlayerPoints(cards: List<ICard>): Int = error("not needed")

        /**
         * Returns the configured breakdown and increments [calls] for each invocation.
         *
         * @param cards ignored by this fake
         * @return [breakdown] supplied to the fake service constructor
         */
        override fun calculateCategoryPointsBreakdown(cards: List<ICard>): CategoryPointsBreakdown {
            calls++
            return breakdown
        }
    }

    private data class FakeCard(override val cardType: CardTypeEnum) : ICard
}

