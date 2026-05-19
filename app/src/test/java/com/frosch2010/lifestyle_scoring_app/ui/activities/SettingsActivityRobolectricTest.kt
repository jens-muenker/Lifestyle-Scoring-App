package com.frosch2010.lifestyle_scoring_app.ui.activities

import android.view.View
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.di.DataSourceModule
import com.frosch2010.lifestyle_scoring_app.di.RepositoryModule
import com.frosch2010.lifestyle_scoring_app.di.ServiceModule
import com.frosch2010.lifestyle_scoring_app.ui.util.useActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.android.testing.UninstallModules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper

/**
 * Robolectric-backed tests for [SettingsActivity] (JVM, Android APIs via Robolectric).
 */
@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class, sdk = [36])
@UninstallModules(
    RepositoryModule::class,
    ServiceModule::class,
    DataSourceModule::class
)
class SettingsActivityRobolectricTest {

    /**
     * Hilt Android rule that installs the test dependency graph for this class.
     */
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    /**
     * Verifies `onCreate_showsToolbarLanguageRowAndBuildInfo()` behavior.
     */
    @Test
    fun onCreate_showsToolbarLanguageRowAndBuildInfo() {
        hiltRule.inject()
        Robolectric.buildActivity(SettingsActivity::class.java).setup().useActivity { activity ->
            assertNotNull(activity.findViewById(R.id.toolbar))
            assertNotNull(activity.findViewById(R.id.auto_complete_language))
            assertNotNull(activity.findViewById(R.id.txt_build_info))
            assertTrue(activity.findViewById<View>(R.id.row_privacy_policy).visibility == View.VISIBLE)
            assertTrue(activity.findViewById<View>(R.id.row_open_source_licenses).visibility == View.VISIBLE)
        }
    }

    /**
     * Verifies `backButton_finishesActivity()` behavior.
     */
    @Test
    fun backButton_finishesActivity() {
        hiltRule.inject()
        Robolectric.buildActivity(SettingsActivity::class.java).setup().useActivity { activity ->
            activity.findViewById<View>(R.id.btnBack).performClick()
            assertTrue(activity.isFinishing || activity.isDestroyed)
        }
    }

    /**
     * Verifies `rowPrivacyPolicy_afterDelay_startsPrivacyPolicyActivity()` behavior.
     */
    @Test
    fun rowPrivacyPolicy_afterDelay_startsPrivacyPolicyActivity() {
        hiltRule.inject()
        Robolectric.buildActivity(SettingsActivity::class.java).setup().useActivity { activity ->
            ShadowLooper.runUiThreadTasksIncludingDelayedTasks()

            activity.findViewById<View>(R.id.row_privacy_policy).performClick()
            ShadowLooper.runUiThreadTasksIncludingDelayedTasks()

            val next = shadowOf(activity).nextStartedActivity
            assertNotNull(next)
            assertEquals(PrivacyPolicyActivity::class.java.name, next.component!!.className)
        }
    }

    /**
     * Verifies `rowOpenSourceLicenses_afterDelay_startsLicensesActivity()` behavior.
     */
    @Test
    fun rowOpenSourceLicenses_afterDelay_startsLicensesActivity() {
        hiltRule.inject()
        Robolectric.buildActivity(SettingsActivity::class.java).setup().useActivity { activity ->
            ShadowLooper.runUiThreadTasksIncludingDelayedTasks()

            activity.findViewById<View>(R.id.row_open_source_licenses).performClick()
            ShadowLooper.runUiThreadTasksIncludingDelayedTasks()

            val next = shadowOf(activity).nextStartedActivity
            assertNotNull(next)
            assertEquals(LicensesActivity::class.java.name, next.component!!.className)
        }
    }
}
