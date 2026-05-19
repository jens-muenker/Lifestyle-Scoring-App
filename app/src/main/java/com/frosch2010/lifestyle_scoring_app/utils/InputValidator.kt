package com.frosch2010.lifestyle_scoring_app.utils

/**
 * Stateless validators for lightweight text fields that must stay consistent across dialogs.
 */
object InputValidator {
    /**
     * Trims whitespace and classifies player names before persistence or dialog acceptance.
     *
     * @param rawValue Unmodified text field contents possibly containing surrounding whitespace.
     * @return [PlayerNameValidationResult] describing whether the name is usable.
     */
    fun validatePlayerName(rawValue: String): PlayerNameValidationResult {
        val trimmedName = rawValue.trim()

        return when {
            trimmedName.isBlank() -> PlayerNameValidationResult.Empty
            else -> PlayerNameValidationResult.Valid(trimmedName)
        }
    }

    /**
     * Validates manual card search input prior to fuzzy matching against the localized catalog.
     *
     * @param rawValue Free-text query from the manual add screen.
     * @return [ManualCardNameQueryResult] indicating readiness for expensive fuzzy lookups.
     */
    fun validateManualCardNameQuery(rawValue: String): ManualCardNameQueryResult {
        val trimmed = rawValue.trim()
        if (trimmed.isBlank()) {
            return ManualCardNameQueryResult.Empty
        }
        val cleaned = CardNameMatcher.cleanText(trimmed)
        if (cleaned.length <= MIN_MANUAL_CARD_QUERY_LENGTH) {
            return ManualCardNameQueryResult.TooShort
        }
        return ManualCardNameQueryResult.Valid(trimmed)
    }

    /**
     * Minimum inclusive cleaned-character length before fuzzy search should run (noise guardrail).
     */
    private const val MIN_MANUAL_CARD_QUERY_LENGTH = 2
}
