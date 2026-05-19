package com.frosch2010.lifestyle_scoring_app.domain.usecase

import com.frosch2010.lifestyle_scoring_app.domain.model.CategoryPointsBreakdown
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import com.frosch2010.lifestyle_scoring_app.services.interfaces.IPlayerPointsCalculationService
import javax.inject.Inject

/**
 * Facade delegating category-aware scoring math to the dedicated calculation service.
 *
 * @param pointsCalculationService Engine encapsulating game rule nuances and multiplier ordering.
 */
class CalculateCategoryPointsBreakdownUseCase @Inject constructor(
    private val pointsCalculationService: IPlayerPointsCalculationService
) {
    /**
     * Produces a full breakdown model for UI consumption from the player's current cards.
     *
     * @param cards Ordered or unordered hand contents; service defines canonical aggregation semantics.
     * @return Immutable [CategoryPointsBreakdown] including total and per-category rows.
     */
    operator fun invoke(cards: List<ICard>): CategoryPointsBreakdown {
        return pointsCalculationService.calculateCategoryPointsBreakdown(cards)
    }
}
