package com.frosch2010.lifestyle_scoring_app.ui

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.ui.activities.MainActivity
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Additional UI coverage for `MainActivity` startup behavior.
 */
@RunWith(AndroidJUnit4::class)
class MainActivityAdditionalUiTest {

    /**
     * Verifies that the add-player dialog is shown when the screen starts.
     */
    @Test
    fun mainScreen_showsAddPlayerDialogOnStart() {
        ActivityScenario.launch(MainActivity::class.java).use {
            MainActivityPlayerDialogTestHelper.ensureAddPlayerNameFieldVisible()
            onView(withId(R.id.edi_player_name)).check(matches(isDisplayed()))
        }
    }
}

