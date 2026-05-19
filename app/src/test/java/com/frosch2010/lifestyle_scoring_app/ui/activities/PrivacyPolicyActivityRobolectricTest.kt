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
import org.robolectric.annotation.Config

/**
 * Robolectric-backed tests for [PrivacyPolicyActivity] (JVM, Android APIs via Robolectric).
 */
@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class, sdk = [36])
@UninstallModules(
    RepositoryModule::class,
    ServiceModule::class,
    DataSourceModule::class
)
class PrivacyPolicyActivityRobolectricTest {

    /**
     * Hilt Android rule that installs the test dependency graph for this class.
     */
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    /**
     * Verifies `onCreate_showsToolbarAndScrollContent()` behavior.
     */
    @Test
    fun onCreate_showsToolbarAndScrollContent() {
        hiltRule.inject()
        Robolectric.buildActivity(PrivacyPolicyActivity::class.java).setup().useActivity { activity ->
            assertNotNull(activity.findViewById(R.id.toolbar))
            assertEquals(View.VISIBLE, activity.findViewById<View>(R.id.scroll_content).visibility)
        }
    }

    /**
     * Verifies `backButton_finishesActivity()` behavior.
     */
    @Test
    fun backButton_finishesActivity() {
        hiltRule.inject()
        Robolectric.buildActivity(PrivacyPolicyActivity::class.java).setup().useActivity { activity ->
            activity.findViewById<View>(R.id.btnBack).performClick()
            assertTrue(activity.isFinishing || activity.isDestroyed)
        }
    }
}
