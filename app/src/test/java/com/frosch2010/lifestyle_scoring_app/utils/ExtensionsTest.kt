package com.frosch2010.lifestyle_scoring_app.utils

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for the `String.normalize()` extension (NFD + combining-mark strip) in `Extensions.kt`.
 */
class ExtensionsTest {

    /**
     * Latin letters with combining accents collapse to their base ASCII letters.
     */
    @Test
    fun normalize_stripsCombiningMarksFromLatin() {
        assertEquals("Cafe", "Café".normalize())
        assertEquals("Muller", "Müller".normalize())
    }

    /**
     * Plain ASCII input is returned unchanged by the normalization extension.
     */
    @Test
    fun normalize_asciiUnchanged() {
        assertEquals("abc", "abc".normalize())
    }
}
