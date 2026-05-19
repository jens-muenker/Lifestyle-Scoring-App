package com.frosch2010.lifestyle_scoring_app.domain.usecase

/**
 * Discriminated outcome for card-add validation used by ViewModels to branch UX messaging.
 */
sealed class AddCardUseCaseResult {
    /**
     * Card is structurally unique within the player's collection and safe to persist.
     */
    data object Ready : AddCardUseCaseResult()

    /**
     * An equivalent card instance is already present; UI should block the add and explain why.
     */
    data object Duplicate : AddCardUseCaseResult()
}
