package com.frosch2010.lifestyle_scoring_app.data.repository.impl

import com.frosch2010.lifestyle_scoring_app.data.source.local.interfaces.LanguageLocalDataSource
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Unit tests for [LanguageRepositoryImpl].
 * Robolectric supplies Android stubs because persisting a tag calls [AppLocale.applyLanguageTag], which forwards to [androidx.appcompat.app.AppCompatDelegate].
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class LanguageRepositoryImplTest {

    /**
     * Verifies `getPersistedLanguageTag_delegatesToDataSource` behavior.
     */
    @Test
    fun getPersistedLanguageTag_delegatesToDataSource() {
        val fake = FakeLanguageLocalDataSource(persisted = "de-DE")
        val repository = LanguageRepositoryImpl(fake)

        assertEquals("de-DE", repository.getPersistedLanguageTag())
    }

    /**
     * Asserts the tag is written through [LanguageLocalDataSource.persistLanguageTag]; [AppLocale.applyLanguageTag]
     * also runs (not asserted here) and requires the Robolectric Android environment from the class rule.
     */
    @Test
    fun persistAndApplyLanguageTag_persistsThenAppliesLocale() {
        val fake = FakeLanguageLocalDataSource(persisted = "")
        val repository = LanguageRepositoryImpl(fake)

        repository.persistAndApplyLanguageTag("en")

        assertEquals(listOf("en"), fake.persistedTags)
    }

    private class FakeLanguageLocalDataSource(
        private val persisted: String
    ) : LanguageLocalDataSource {
        val persistedTags = mutableListOf<String>()

        override fun getPersistedLanguageTag(): String = persisted

        override fun persistLanguageTag(tag: String) {
            persistedTags.add(tag)
        }
    }
}
