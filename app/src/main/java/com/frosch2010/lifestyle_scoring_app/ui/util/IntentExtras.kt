package com.frosch2010.lifestyle_scoring_app.ui.util

/**
 * Canonical [`Intent`][android.content.Intent] extra keys for navigation between feature screens.
 *
 * Keeping keys centralized avoids subtle typos that break `ActivityResult` contracts across modules.
 */
object IntentExtras {
    /**
     * Integer roster index identifying which [`Player`][com.frosch2010.lifestyle_scoring_app.models.entities.Player] a downstream screen should load.
     */
    const val EXTRA_PLAYER_NUMBER = "player_number"

    /**
     * Serialized [`ScanResult`][com.frosch2010.lifestyle_scoring_app.utils.ScanResult] returned from scan or manual capture flows.
     */
    const val EXTRA_SCAN_RESULT = "scan_result"
}
