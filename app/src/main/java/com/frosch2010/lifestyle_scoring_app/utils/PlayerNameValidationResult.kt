package com.frosch2010.lifestyle_scoring_app.utils

/**
 * Outcome of validating a player name field prior to roster mutation.
 */
sealed class PlayerNameValidationResult {
    /**
     * User left the field blank or whitespace-only after trimming.
     */
    data object Empty : PlayerNameValidationResult()

    /**
     * Non-blank trimmed name safe to store on [com.frosch2010.lifestyle_scoring_app.models.entities.Player].
     *
     * @property value Sanitized display string ready for persistence.
     */
    data class Valid(val value: String) : PlayerNameValidationResult()
}
