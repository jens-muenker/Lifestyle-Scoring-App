package com.frosch2010.lifestyle_scoring_app.ui.viewmodels.dto

/**
 * Lightweight list row model for the main scoreboard without exposing domain entities to adapters.
 *
 * @property name Player display name for the current session.
 * @property score Aggregated points computed from the player's hand.
 */
data class PlayerDTO(val name: String, val score: Int)
