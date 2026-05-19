package com.frosch2010.lifestyle_scoring_app.utils

/**
 * Canonical BCP-47 fragments and sentinel values exchanged between settings UI and persistence.
 *
 * Centralizing literals prevents drift between DataStore serialization and `AppCompatDelegate` wiring.
 */
object LanguageTags {
    /**
     * Sentinel instructing `AppLocale` to clear explicit overrides and follow the OS locale list.
     */
    const val SYSTEM: String = ""

    /**
     * German language subtag persisted when the user selects Deutsch explicitly.
     */
    const val DE: String = "de"

    /**
     * English language subtag persisted when the user selects English explicitly.
     */
    const val EN: String = "en"
}
