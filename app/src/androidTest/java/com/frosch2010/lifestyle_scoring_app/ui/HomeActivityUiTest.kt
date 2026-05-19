package com.frosch2010.lifestyle_scoring_app.ui

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.appcompat.widget.Toolbar
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.ui.activities.HomeActivity
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI test suite for verifying the initial Home screen rendering.
 */
@RunWith(AndroidJUnit4::class)
class HomeActivityUiTest {

    /**
     * Verifies that the Home screen shows the primary action buttons and toolbar menu entry.
     */
    @Test
    fun homeScreen_displaysPrimaryActions() {
        ActivityScenario.launch(HomeActivity::class.java).use { scenario ->
            onView(withId(R.id.btn_calculate_points)).check(matches(isDisplayed()))
            onView(withId(R.id.btn_game_rules)).check(matches(isDisplayed()))
            onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
            scenario.onActivity { activity ->
                val toolbar = activity.findViewById<Toolbar>(R.id.toolbar)
                assertNotNull(toolbar.menu.findItem(R.id.action_settings))
            }
        }
    }
}
