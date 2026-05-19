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
 * Robolectric tests for [AppInfoLocalDataSourceImpl] (PackageManager-backed build metadata).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AppInfoLocalDataSourceImplTest {

    /**
     * Verifies `getBuildInfo_returnsNonBlankVersionNameAndNonNegativeCode` behavior.
     */
    @Test
    fun getBuildInfo_returnsNonBlankVersionNameAndNonNegativeCode() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dataSource = AppInfoLocalDataSourceImpl(context)

        val info = dataSource.getBuildInfo()

        assertFalse(info.versionName.isBlank())
        assertTrue(info.versionCode >= 0L)
    }
}
