package com.frosch2010.lifestyle_scoring_app.ui.util

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for [IntentExtras].
 */

class IntentExtrasTest {

    /**
     * Verifies `extraKeys_areStableContractStrings` behavior.
     */
    @Test
    fun extraKeys_areStableContractStrings() {
        assertEquals("player_number", IntentExtras.EXTRA_PLAYER_NUMBER)
        assertEquals("scan_result", IntentExtras.EXTRA_SCAN_RESULT)
    }
}
