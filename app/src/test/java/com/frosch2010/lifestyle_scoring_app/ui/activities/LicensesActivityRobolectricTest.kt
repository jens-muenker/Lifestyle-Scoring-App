package com.frosch2010.lifestyle_scoring_app.ui.activities

import android.view.View
import android.widget.TextView
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

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class, sdk = [36])
@UninstallModules(
    RepositoryModule::class,
    ServiceModule::class,
    DataSourceModule::class
)

/**
 * Robolectric-backed tests for [LicensesActivity] (UI-/Android APIs im JVM-Test).
 */
class LicensesActivityRobolectricTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    /**
     * Verifies `showsEmptyState_whenRepositoryReturnsEmptyList` behavior.
     */
    @Test
    fun showsEmptyState_whenRepositoryReturnsEmptyList() {
        hiltRule.inject()
        Robolectric.buildActivity(LicensesActivity::class.java).setup().useActivity { activity ->
            val recycler = activity.findViewById<View>(R.id.rec_licenses)
            val emptyState = activity.findViewById<View>(R.id.empty_licenses_state)
            val emptyMessage = activity.findViewById<TextView>(R.id.txt_licenses_empty_message)

            assertNotNull(recycler)
            assertNotNull(emptyState)
            assertEquals(View.GONE, recycler.visibility)
            assertEquals(View.VISIBLE, emptyState.visibility)
            assertEquals(activity.getString(R.string.licenses_none_listed), emptyMessage.text.toString())
        }
    }

    /**
     * Verifies `backButton_finishesActivity` behavior.
     */
    @Test
    fun backButton_finishesActivity() {
        hiltRule.inject()
        Robolectric.buildActivity(LicensesActivity::class.java).setup().useActivity { activity ->
            activity.findViewById<View>(R.id.btnBack).performClick()
            assertTrue(activity.isFinishing || activity.isDestroyed)
        }
    }
}
