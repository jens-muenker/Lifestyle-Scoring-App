package com.frosch2010.lifestyle_scoring_app.ui

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.Visibility
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.ui.activities.LicensesActivity
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI test suite for legal licenses screen behavior.
 */
@RunWith(AndroidJUnit4::class)
class LicensesActivityUiTest {

    /**
     * Verifies that core toolbar/content containers are present.
     */
    @Test
    fun licensesScreen_displaysCoreViews() {
        ActivityScenario.launch(LicensesActivity::class.java).use {
            onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
            // Depending on data loading, either list content or empty state is shown.
            it.onActivity { activity ->
                val recyclerVisible = activity.findViewById<android.view.View>(R.id.rec_licenses).visibility == android.view.View.VISIBLE
                val emptyVisible = activity.findViewById<android.view.View>(R.id.empty_licenses_state).visibility == android.view.View.VISIBLE
                assertTrue("Either recycler or empty state must be visible", recyclerVisible || emptyVisible)
                assertFalse("Recycler and empty state must not be visible together", recyclerVisible && emptyVisible)
            }
        }
    }

    /**
     * Verifies that the back button closes the licenses screen.
     */
    @Test
    fun licensesScreen_backButton_finishesActivity() {
        ActivityScenario.launch(LicensesActivity::class.java).use { scenario ->
            onView(withId(R.id.btnBack)).perform(click())
            scenario.onActivity { activity ->
                assertTrue(activity.isFinishing || activity.isDestroyed)
            }
        }
    }

    /**
     * Verifies recycler interaction when licenses are available.
     */
    @Test
    fun licensesScreen_whenListVisible_supportsRecyclerInteraction() {
        ActivityScenario.launch(LicensesActivity::class.java).use { scenario ->
            var recyclerVisible = false
            var canScrollDown = false
            scenario.onActivity { activity ->
                val recycler = activity.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rec_licenses)
                recyclerVisible = recycler.visibility == android.view.View.VISIBLE
                canScrollDown = recycler.canScrollVertically(1)
            }
            if (recyclerVisible) {
                onView(withId(R.id.empty_licenses_state))
                    .check(matches(withEffectiveVisibility(Visibility.GONE)))
                if (canScrollDown) {
                    onView(withId(R.id.rec_licenses))
                        .perform(RecyclerViewActions.scrollToPosition<androidx.recyclerview.widget.RecyclerView.ViewHolder>(1))
                    onView(withId(R.id.rec_licenses)).check(matches(isDisplayed()))
                }
            }
        }
    }
}

