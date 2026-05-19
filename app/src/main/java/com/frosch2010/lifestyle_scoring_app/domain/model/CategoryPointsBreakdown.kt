package com.frosch2010.lifestyle_scoring_app.domain.model

/**
 * Aggregated scoring view for one player's hand: final total plus per-category rows for UI breakdown.
 *
 * Sport and love cards influence totals indirectly (job tiers, love multiplier); their dedicated
 * rows therefore intentionally surface `0` direct points while still participating in rule logic upstream.
 *
 * @property totalPoints Final score after all category rules, caps, and cross-category multipliers.
 * @property entries Ordered rows driving the per-category list in the player cards screen.
 */
data class CategoryPointsBreakdown(
    val totalPoints: Int,
    val entries: List<CategoryPointsEntry>
)
