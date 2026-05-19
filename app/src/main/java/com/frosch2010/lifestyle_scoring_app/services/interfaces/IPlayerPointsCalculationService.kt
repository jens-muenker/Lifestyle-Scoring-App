package com.frosch2010.lifestyle_scoring_app.services.interfaces

import com.frosch2010.lifestyle_scoring_app.domain.model.CategoryPointsBreakdown
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard

/**
 * Rule engine port encapsulating Lifestyle Scoring totals and per-category attribution for UI breakdowns.
 */
interface IPlayerPointsCalculationService {

    /**
     * Collapses [cards] into a single integer total after applying synergies, multipliers, and caps.
     *
     * @param cards Player hand contents in any order; duplicates follow physical card semantics.
     * @return Final non-negative score suitable for leaderboards.
     */
    fun calculatePlayerPoints(cards: List<ICard>): Int

    /**
     * Computes both the headline total and fixed enum-ordered rows for the player cards analytics screen.
     *
     * Sport and love cards influence totals indirectly; their breakdown rows intentionally read `0` direct points
     * while still affecting multiplier logic inside [calculatePlayerPoints].
     *
     * @param cards Player hand contents mirroring the total calculation input.
     * @return [CategoryPointsBreakdown] whose `entries` sum to `totalPoints` after cross-category adjustments.
     */
    fun calculateCategoryPointsBreakdown(cards: List<ICard>): CategoryPointsBreakdown
}
