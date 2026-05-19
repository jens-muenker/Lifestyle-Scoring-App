package com.frosch2010.lifestyle_scoring_app.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [InputValidator] player-name and manual-card-query validation ([PlayerNameValidationResult], [ManualCardNameQueryResult]).
 */
class InputValidatorTest {

    /**
     * [InputValidator.validatePlayerName] maps whitespace-only input to [PlayerNameValidationResult.Empty].
     */
    @Test
    fun validatePlayerName_returnsEmpty_forBlankName() {
        val result = InputValidator.validatePlayerName("   ")
        assertTrue(result is PlayerNameValidationResult.Empty)
    }

    /**
     * Non-blank names are trimmed and wrapped in [PlayerNameValidationResult.Valid].
     */
    @Test
    fun validatePlayerName_returnsTrimmedName_forValidValue() {
        val result = InputValidator.validatePlayerName("  Alice  ")
        assertTrue(result is PlayerNameValidationResult.Valid)
        assertEquals("Alice", (result as PlayerNameValidationResult.Valid).value)
    }

    /**
     * [InputValidator.validateManualCardNameQuery] maps whitespace-only queries to [ManualCardNameQueryResult.Empty].
     */
    @Test
    fun validateManualCardNameQuery_returnsEmpty_forBlank() {
        val result = InputValidator.validateManualCardNameQuery("   ")
        assertTrue(result is ManualCardNameQueryResult.Empty)
    }

    /**
     * Queries with fewer than the minimum letter count yield [ManualCardNameQueryResult.TooShort].
     */
    @Test
    fun validateManualCardNameQuery_returnsTooShort_whenNotEnoughLetters() {
        val result = InputValidator.validateManualCardNameQuery("ab")
        assertTrue(result is ManualCardNameQueryResult.TooShort)
    }

    /**
     * Acceptable queries are trimmed and returned as [ManualCardNameQueryResult.Valid].
     */
    @Test
    fun validateManualCardNameQuery_returnsValid_withTrimmedQuery() {
        val result = InputValidator.validateManualCardNameQuery("  Katze  ")
        assertTrue(result is ManualCardNameQueryResult.Valid)
        assertEquals("Katze", (result as ManualCardNameQueryResult.Valid).trimmedQuery)
    }
}
