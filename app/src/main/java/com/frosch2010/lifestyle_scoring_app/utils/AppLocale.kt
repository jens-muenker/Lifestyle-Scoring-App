package com.frosch2010.lifestyle_scoring_app.utils

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

/**
 * Applies per-app locale overrides through `AppCompatDelegate` while keeping persistence elsewhere.
 *
 * Callers persist tags via repositories; this object only mutates runtime configuration for immediate effect.
 */
object AppLocale {
    /**
     * Updates application locales so subsequent resource lookups honor [languageTag].
     *
     * @param languageTag BCP-47 tag, or empty string to reset to the system default list.
     */
    fun applyLanguageTag(languageTag: String) {
        val locales = if (languageTag.isEmpty()) {
            LocaleListCompat.getEmptyLocaleList()
        } else {
            LocaleListCompat.forLanguageTags(languageTag)
        }
        AppCompatDelegate.setApplicationLocales(locales)
    }
}
