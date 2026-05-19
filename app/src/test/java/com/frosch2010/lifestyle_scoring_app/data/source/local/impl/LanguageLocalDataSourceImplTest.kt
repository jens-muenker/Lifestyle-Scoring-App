package com.frosch2010.lifestyle_scoring_app.data.source.local.impl

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.frosch2010.lifestyle_scoring_app.data.datastore.AppPreferencesDataStore
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Robolectric tests for [LanguageLocalDataSourceImpl] against a real [AppPreferencesDataStore].
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class LanguageLocalDataSourceImplTest {

    /**
     * Verifies `persistLanguageTag_thenGetPersistedLanguageTag_returnsTag` behavior.
     */
    @Test
    fun persistLanguageTag_thenGetPersistedLanguageTag_returnsTag() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val store = AppPreferencesDataStore(context)
        val dataSource = LanguageLocalDataSourceImpl(store)

        dataSource.persistLanguageTag("fr-CA")

        assertEquals("fr-CA", dataSource.getPersistedLanguageTag())
    }
}
