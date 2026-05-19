package com.frosch2010.lifestyle_scoring_app.ui

import android.app.Activity
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Root
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItem
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.contrib.RecyclerViewActions.scrollTo
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.ui.activities.MainActivity
import com.frosch2010.lifestyle_scoring_app.ui.activities.PlayerCardsActivity
import org.hamcrest.Description
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI test suite for card swipe/delete flows in `PlayerCardsActivity`.
 */
@RunWith(AndroidJUnit4::class)
class PlayerCardsActivitySwipeUiTest {

    /**
     * [ActivityLifecycleMonitorRegistry.getActivitiesInStage] must run on the main thread.
     * JUnit's `@Test(timeout = …)` runs the test body on a worker thread, so this hops to the main thread via `Instrumentation.runOnMainSync`.
     *
     * @return decor view of the resumed [PlayerCardsActivity], or the sole resumed activity when stacked
     */
    private fun playerCardsActivityDecorView(): View {
        val holder = arrayOfNulls<View>(1)
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            @Suppress("DEPRECATION")
            val resumed: Collection<Activity> =
                ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED)
            val activity = resumed.filterIsInstance<PlayerCardsActivity>().firstOrNull()
                ?: resumed.firstOrNull()
                ?: error("No resumed activity")
            holder[0] = activity.window.decorView
        }
        return holder[0] ?: error("No PlayerCards decor view")
    }

    /**
     * [withDecorView] alone can still match the invisible androidx.test `EmptyActivity` window
     * (hidden decor view, no window focus), which triggers [RootViewWithoutFocusException].
     * [androidx.test.espresso.matcher.RootMatchers.hasWindowFocus] is private in older Espresso versions,
     * so this custom [Root] matcher selects a focused, visible non-activity window for the delete dialog.
     *
     * @return matcher that accepts focused overlay windows excluding the activity decor view
     */
    private fun deleteDialogRoot(): org.hamcrest.Matcher<Root> {
        val activityDecor = playerCardsActivityDecorView()
        return object : TypeSafeMatcher<Root>() {
            override fun describeTo(description: Description) {
                description.appendText("root with window focus, visible decor, not PlayerCardsActivity window")
            }

            override fun matchesSafely(root: Root): Boolean {
                if (root.decorView === activityDecor) return false
                if (root.decorView.visibility != View.VISIBLE) return false
                if (!root.decorView.hasWindowFocus()) return false
                return true
            }
        }
    }

    /**
     * Verifies that swiping a manually added card left opens the delete-card dialog.
     */
    @Test(timeout = 60_000)
    fun manualAddAnimalCard_swipeLeft_showsDeleteCardDialog() {
        ActivityScenario.launch(MainActivity::class.java).use {
            MainActivityPlayerDialogTestHelper.ensureAddPlayerNameFieldVisible()
            onView(withId(R.id.edi_player_name)).perform(replaceText("CardSwipe"))
            closeSoftKeyboard()
            onView(withText(R.string.ok)).perform(click())
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()

            onView(withId(R.id.rec_view)).perform(
                actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click())
            )
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()

            onView(withId(R.id.btn_manual_add)).perform(click())
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()

            onView(withId(R.id.editSearchCard)).perform(replaceText("Hund"))
            closeSoftKeyboard()
            // Filter "hund" matches several cards (substring in searchableText); pick the row named "Hund".
            onView(withId(R.id.recManualCards)).perform(
                scrollTo<RecyclerView.ViewHolder>(hasDescendant(withText("Hund")))
            )
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
            onView(
                allOf(
                    withId(R.id.txtCardName),
                    withText("Hund"),
                    isDescendantOfA(withId(R.id.recManualCards)),
                    isDisplayed()
                )
            ).perform(click())
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()
            // ManualAddCardActivity.finish() — wait until PlayerCards is on top again (no rec_view on manual screen).
            onView(withId(R.id.btn_manual_add)).check(matches(isDisplayed()))

            // Verify the card was actually added before swiping (and also waits for the adapter to bind it).
            onView(
                allOf(
                    withId(R.id.card_name),
                    withText("Hund"),
                    isDescendantOfA(withId(R.id.rec_view)),
                    isDisplayed()
                )
            ).check(matches(isDisplayed()))

            // Swipe the card row identified by its name (robust against ConcatAdapter index shifts).
            onView(withId(R.id.rec_view)).perform(
                actionOnItem<RecyclerView.ViewHolder>(hasDescendant(withText("Hund")), swipeLeft())
            )
            InstrumentationRegistry.getInstrumentation().waitForIdleSync()

            onView(withText(R.string.message_delete_card))
                .inRoot(deleteDialogRoot())
                .check(matches(isDisplayed()))
            onView(withText(R.string.abort))
                .inRoot(deleteDialogRoot())
                .perform(click())
        }
    }
}
