package com.frosch2010.lifestyle_scoring_app.utils

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for [LanguageTags] sentinels used with [AppLocale] / settings persistence.
 */
class LanguageTagsTest {

    /**
     * [LanguageTags.SYSTEM] is the empty string (follow the OS locale list).
     */
    @Test
    fun systemTag_isEmptyString() {
        assertEquals("", LanguageTags.SYSTEM)
    }

    /**
     * Explicit picks use stable BCP-47 language subtags `de` and `en`.
     */
    @Test
    fun explicitLanguageTags_matchBcp47Subtags() {
        assertEquals("de", LanguageTags.DE)
        assertEquals("en", LanguageTags.EN)
    }
}
