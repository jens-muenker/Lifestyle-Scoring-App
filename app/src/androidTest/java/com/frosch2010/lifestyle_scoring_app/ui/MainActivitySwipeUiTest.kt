package com.frosch2010.lifestyle_scoring_app.ui

import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.action.ViewActions.swipeRight
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.ui.activities.MainActivity
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI test suite for swipe actions on player rows in `MainActivity`.
 */
@RunWith(AndroidJUnit4::class)
class MainActivitySwipeUiTest {

    /**
     * Verifies that swiping a player row left opens the delete-player dialog.
     */
    @Test
    fun swipeLeftOnPlayerRow_showsDeleteDialog() {
        ActivityScenario.launch(MainActivity::class.java).use {
            MainActivityPlayerDialogTestHelper.ensureAddPlayerNameFieldVisible()
            onView(withId(R.id.edi_player_name)).perform(replaceText("SwipeUser"))
            closeSoftKeyboard()
            onView(withText(R.string.ok)).perform(click())
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()

            onView(withId(R.id.rec_view)).perform(
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, swipeLeft())
            )
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()

            onView(withText(R.string.delete_player)).check(matches(isDisplayed()))
            onView(withText(R.string.abort)).perform(click())
        }
    }

    /**
     * Verifies that swiping a player row right opens the edit-player dialog.
     */
    @Test
    fun swipeRightOnPlayerRow_showsEditDialog() {
        ActivityScenario.launch(MainActivity::class.java).use {
            MainActivityPlayerDialogTestHelper.ensureAddPlayerNameFieldVisible()
            onView(withId(R.id.edi_player_name)).perform(replaceText("EditSwipe"))
            closeSoftKeyboard()
            onView(withText(R.string.ok)).perform(click())
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()

            onView(withId(R.id.rec_view)).perform(
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, swipeRight())
            )
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()

            onView(withText(R.string.edit_player_name)).check(matches(isDisplayed()))
            onView(withText(R.string.abort)).perform(click())
        }
    }
}
