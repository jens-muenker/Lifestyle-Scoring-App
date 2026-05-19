package com.frosch2010.lifestyle_scoring_app.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Robolectric tests for [AppPreferencesDataStore] (blocking language tag read/write).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AppPreferencesDataStoreTest {

    private lateinit var context: Context
    private lateinit var dataStore: AppPreferencesDataStore

    /**
     * Uses the application [Context], constructs [AppPreferencesDataStore], and removes the `app_language_tag` preference so each test starts without a persisted tag unless it sets one.
     */
    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        dataStore = AppPreferencesDataStore(context)
        val languageKey = stringPreferencesKey("app_language_tag")
        runBlocking {
            context.appPreferencesDataStore.edit { prefs ->
                prefs.remove(languageKey)
            }
        }
    }

    /**
     * Verifies `getLanguageTagSync_returnsEmptyWhenUnset` behavior.
     */
    @Test
    fun getLanguageTagSync_returnsEmptyWhenUnset() {
        val tag = dataStore.getLanguageTagSync()

        assertEquals("", tag)
    }

    /**
     * Verifies `setLanguageTagSync_thenGetLanguageTagSync_roundTrips` behavior.
     */
    @Test
    fun setLanguageTagSync_thenGetLanguageTagSync_roundTrips() {
        dataStore.setLanguageTagSync("de-DE")

        assertEquals("de-DE", dataStore.getLanguageTagSync())
    }

    /**
     * Verifies `setLanguageTagSync_withEmptyString_persistsEmpty` behavior.
     */
    @Test
    fun setLanguageTagSync_withEmptyString_persistsEmpty() {
        dataStore.setLanguageTagSync("en")
        dataStore.setLanguageTagSync("")

        assertEquals("", dataStore.getLanguageTagSync())
    }
}
