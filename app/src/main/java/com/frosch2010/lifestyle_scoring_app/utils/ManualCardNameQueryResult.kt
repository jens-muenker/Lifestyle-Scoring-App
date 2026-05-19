package com.frosch2010.lifestyle_scoring_app.utils

/**
 * Outcome of validating manual card search input before hitting fuzzy matchers.
 */
sealed class ManualCardNameQueryResult {
    /**
     * Query empty after trimming; UI should short-circuit without searching.
     */
    data object Empty : ManualCardNameQueryResult()

    /**
     * Cleaned query still too short to yield meaningful fuzzy scores.
     */
    data object TooShort : ManualCardNameQueryResult()

    /**
     * Query long enough to proceed with fuzzy extraction against the card map.
     *
     * @property trimmedQuery User text after trimming; still retains spaces for display echoing.
     */
    data class Valid(val trimmedQuery: String) : ManualCardNameQueryResult()
}
