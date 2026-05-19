package com.frosch2010.lifestyle_scoring_app.ui

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.ui.activities.SettingsActivity
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI test suite for language controls in the Settings screen.
 */
@RunWith(AndroidJUnit4::class)
class SettingsActivityLanguageUiTest {

    /**
     * Verifies that the language selector is displayed in Settings.
     */
    @Test
    fun settings_showsLanguageSpinner() {
        ActivityScenario.launch(SettingsActivity::class.java).use {
            onView(withId(R.id.auto_complete_language)).check(matches(isDisplayed()))
        }
    }
}
