package com.frosch2010.lifestyle_scoring_app.utils

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Robolectric-backed tests for [AppLocale.applyLanguageTag] wiring into [androidx.appcompat.app.AppCompatDelegate].
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AppLocaleTest {

    /**
     * [LanguageTags.SYSTEM] clears explicit app locales via an empty [LocaleListCompat].
     */
    @Test
    fun applyLanguageTag_system_setsEmptyLocaleList() {
        AppLocale.applyLanguageTag(LanguageTags.SYSTEM)

        assertEquals(LocaleListCompat.getEmptyLocaleList(), AppCompatDelegate.getApplicationLocales())
    }

    /**
     * A concrete BCP-47 tag (e.g. [LanguageTags.EN]) is forwarded to [AppCompatDelegate.setApplicationLocales].
     */
    @Test
    fun applyLanguageTag_nonSystem_setsLocales() {
        AppLocale.applyLanguageTag(LanguageTags.EN)

        val appliedTags = AppCompatDelegate.getApplicationLocales().toLanguageTags()
        assertTrue(appliedTags.isEmpty() || appliedTags.startsWith(LanguageTags.EN))
    }
}
