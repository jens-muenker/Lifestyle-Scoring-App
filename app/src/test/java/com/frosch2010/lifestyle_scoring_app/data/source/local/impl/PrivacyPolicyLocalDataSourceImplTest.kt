package com.frosch2010.lifestyle_scoring_app.data.source.local.impl

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Locale

/**
 * Robolectric tests for [PrivacyPolicyLocalDataSourceImpl] (locale selection + raw Markdown).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class PrivacyPolicyLocalDataSourceImplTest {

    /**
     * Verifies `getMarkdown_forGermanLocale_containsHeadingMarkers` behavior.
     */
    @Test
    fun getMarkdown_forGermanLocale_containsHeadingMarkers() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dataSource = PrivacyPolicyLocalDataSourceImpl(context)
        Locale.setDefault(Locale.GERMAN)

        val md = dataSource.getMarkdown()

        assertTrue(md.contains("##"))
    }

    /**
     * Verifies `getMarkdown_forEnglishLocale_containsPrivacyWord` behavior.
     */
    @Test
    fun getMarkdown_forEnglishLocale_containsPrivacyWord() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dataSource = PrivacyPolicyLocalDataSourceImpl(context)
        Locale.setDefault(Locale.ENGLISH)

        val md = dataSource.getMarkdown()

        assertTrue(md.contains("Privacy", ignoreCase = true))
    }
}
