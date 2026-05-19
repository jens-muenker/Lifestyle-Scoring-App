package com.frosch2010.lifestyle_scoring_app.ui

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.ui.activities.HomeActivity
import com.frosch2010.lifestyle_scoring_app.ui.activities.MainActivity
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI test suite for navigation flow from Home to Main screen.
 */
@RunWith(AndroidJUnit4::class)
class HomeToMainFlowUiTest {

    /**
     * Initializes Espresso-Intents before each navigation test.
     */
    @Before
    fun setUp() {
        Intents.init()
    }

    /**
     * Releases Espresso-Intents resources after each test.
     */
    @After
    fun tearDown() {
        Intents.release()
    }

    /**
     * Verifies that tapping "Calculate Points" starts `MainActivity`.
     */
    @Test
    fun clickingCalculatePoints_opensMainActivity() {
        ActivityScenario.launch(HomeActivity::class.java).use {
            onView(withId(R.id.btn_calculate_points)).perform(click())
            intended(hasComponent(MainActivity::class.java.name))
        }
    }
}
