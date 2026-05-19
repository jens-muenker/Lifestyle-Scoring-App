package com.frosch2010.lifestyle_scoring_app.data.source.local.impl

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Robolectric tests for [LicensesLocalDataSourceImpl] against the packaged `licenses.json` asset.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class LicensesLocalDataSourceImplTest {

    /**
     * Verifies `getLicenses_returnsNonEmptyParsedRows_whenAssetPresent` behavior.
     */
    @Test
    fun getLicenses_returnsNonEmptyParsedRows_whenAssetPresent() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dataSource = LicensesLocalDataSourceImpl(context)

        val rows = dataSource.getLicenses()

        assertTrue(rows.isNotEmpty())
        assertFalse(rows.first().projectName.isBlank())
    }
}
