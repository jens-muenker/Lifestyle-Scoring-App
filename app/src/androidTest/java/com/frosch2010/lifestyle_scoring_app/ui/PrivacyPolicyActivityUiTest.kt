package com.frosch2010.lifestyle_scoring_app.ui

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.Visibility
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.ui.activities.PrivacyPolicyActivity
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI test suite for privacy policy screen rendering and interaction.
 */
@RunWith(AndroidJUnit4::class)
class PrivacyPolicyActivityUiTest {

    /**
     * Verifies toolbar and Markdown content visibility.
     */
    @Test
    fun privacyPolicyScreen_displaysToolbarAndContent() {
        ActivityScenario.launch(PrivacyPolicyActivity::class.java).use {
            onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
            onView(withId(R.id.txt_privacy_policy)).check(matches(isDisplayed()))
        }
    }

    /**
     * Verifies scrolling behavior and top divider state transitions.
     */
    @Test
    fun privacyPolicyScreen_supportsScrollAndKeepsContentVisible() {
        ActivityScenario.launch(PrivacyPolicyActivity::class.java).use {
            onView(withId(R.id.topBarDivider)).check(matches(withEffectiveVisibility(Visibility.GONE)))
            onView(withId(R.id.scroll_content)).perform(swipeUp())
            onView(withId(R.id.topBarDivider)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
            onView(withId(R.id.txt_privacy_policy)).check(matches(isDisplayed()))
        }
    }

    /**
     * Verifies that tapping the back button finishes the screen.
     */
    @Test
    fun privacyPolicyScreen_backButton_finishesActivity() {
        ActivityScenario.launch(PrivacyPolicyActivity::class.java).use { scenario ->
            onView(withId(R.id.btnBack)).perform(click())
            scenario.onActivity { activity ->
                assertTrue(activity.isFinishing || activity.isDestroyed)
            }
        }
    }
}

