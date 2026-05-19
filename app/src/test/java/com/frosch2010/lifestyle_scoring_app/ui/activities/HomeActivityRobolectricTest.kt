package com.frosch2010.lifestyle_scoring_app.ui.activities

import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.view.View
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.ui.util.useActivity
import com.google.android.material.appbar.MaterialToolbar
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper

/**
 * Robolectric-backed tests for [HomeActivity] (JVM, Android APIs via Robolectric).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class HomeActivityRobolectricTest {

    /**
     * Verifies `onCreate_showsToolbarAndPrimaryActions()` behavior.
     */
    @Test
    fun onCreate_showsToolbarAndPrimaryActions() {
        Robolectric.buildActivity(HomeActivity::class.java).setup().useActivity { activity ->
            assertNotNull(activity.findViewById(R.id.toolbar))
            assertEquals(View.VISIBLE, activity.findViewById<View>(R.id.btn_calculate_points).visibility)
            assertEquals(View.VISIBLE, activity.findViewById<View>(R.id.btn_game_rules).visibility)
        }
    }

    /**
     * Verifies `onCreate_showsScrollableContent()` behavior.
     */
    @Test
    fun onCreate_showsScrollableContent() {
        Robolectric.buildActivity(HomeActivity::class.java).setup().useActivity { activity ->
            assertEquals(View.VISIBLE, activity.findViewById<View>(R.id.scroll_content).visibility)
        }
    }

    /**
     * Verifies `settingsMenuItem_startsSettingsActivity()` behavior.
     */
    @Test
    fun settingsMenuItem_startsSettingsActivity() {
        Robolectric.buildActivity(HomeActivity::class.java).setup().useActivity { activity ->
            ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
            val toolbar = activity.findViewById<MaterialToolbar>(R.id.toolbar)
            assertNotNull(toolbar.menu.findItem(R.id.action_settings))
            toolbar.menu.performIdentifierAction(R.id.action_settings, 0)
            val next = shadowOf(activity).nextStartedActivity
            assertNotNull(next)
            assertEquals(SettingsActivity::class.java.name, next.component!!.className)
        }
    }

    /**
     * Verifies `gameRulesButton_startsBrowserIntentWhenResolveInfoExists()` behavior.
     */
    @Test
    fun gameRulesButton_startsBrowserIntentWhenResolveInfoExists() {
        Robolectric.buildActivity(HomeActivity::class.java).setup().useActivity { activity ->
            ShadowLooper.runUiThreadTasksIncludingDelayedTasks()

            val url = activity.getString(R.string.home_game_rules_url)
            val browserComponent = ComponentName(
                "com.example.browser",
                "com.example.browser.BrowserActivity"
            )
            val shadowPackageManager = shadowOf(activity.packageManager)
            shadowPackageManager.addActivityIfNotPresent(browserComponent)
            shadowPackageManager.addIntentFilterForActivity(
                browserComponent,
                IntentFilter(Intent.ACTION_VIEW).apply {
                    addCategory(Intent.CATEGORY_DEFAULT)
                    addDataScheme("http")
                    addDataScheme("https")
                }
            )

            activity.findViewById<View>(R.id.btn_game_rules).performClick()
            val launched = shadowOf(activity).nextStartedActivity
            assertEquals(Intent.ACTION_VIEW, launched.action)
            assertEquals(url, launched.dataString)
        }
    }
}
