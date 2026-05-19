package com.frosch2010.lifestyle_scoring_app.domain.model

/**
 * Lightweight read model pairing a player's display identity with their computed standing.
 *
 * Used wherever the UI ranks participants without dragging full entity graphs through presentation layers.
 *
 * @property name Player label as entered for the current session.
 * @property score Aggregated points derived from the player's current card list and game rules.
 */
data class PlayerWithScore(
    val name: String,
    val score: Int
)
