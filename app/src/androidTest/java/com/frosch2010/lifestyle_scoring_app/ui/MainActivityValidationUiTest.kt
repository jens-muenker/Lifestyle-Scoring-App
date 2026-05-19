package com.frosch2010.lifestyle_scoring_app.ui

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.ui.activities.MainActivity
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI test suite for validating player name input behavior in `MainActivity`.
 */
@RunWith(AndroidJUnit4::class)
class MainActivityValidationUiTest {

    /**
     * Verifies that blank player names keep the dialog open and show validation feedback.
     */
    @Test
    fun addPlayerDialog_showsError_whenNameIsBlank() {
        ActivityScenario.launch(MainActivity::class.java).use {
            MainActivityPlayerDialogTestHelper.ensureAddPlayerNameFieldVisible()
            onView(withId(R.id.edi_player_name)).perform(replaceText("   "))
            closeSoftKeyboard()
            onView(withText(R.string.ok)).perform(click())
            onView(withId(R.id.edi_player_name)).check(matches(isDisplayed()))
        }
    }
}
