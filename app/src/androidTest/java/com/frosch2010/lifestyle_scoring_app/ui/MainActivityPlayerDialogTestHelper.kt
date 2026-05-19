package com.frosch2010.lifestyle_scoring_app.ui

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingPolicies
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry
import com.frosch2010.lifestyle_scoring_app.R
import java.util.concurrent.TimeUnit

/**
 * MainActivity shows [R.id.edi_player_name] only inside the add/edit player dialog.
 * With an empty player list the dialog is opened from [android.view.View.post], so the field
 * can appear slightly later. With persisted players there is no auto-dialog — use the FAB
 * or the empty-state button to open it.
 *
 * Waiting uses [PlayerNameFieldIdlingResource] (no [Thread.sleep]); the first Espresso wait
 * uses a short idling timeout so we can fall back to the FAB quickly when no auto-dialog runs.
 */
object MainActivityPlayerDialogTestHelper {

    /**
     * Instrumentation handle used for synchronization and UI thread coordination.
     */
    private val instrumentation = InstrumentationRegistry.getInstrumentation()

    /**
     * Default Espresso idling-resource timeout used to restore [IdlingPolicies] after short waits.
     * Espresso Core 3.5.x does not expose getters for framework defaults; this value matches the usual default.
     */
    private val defaultIdlingResourceTimeoutMs = TimeUnit.SECONDS.toMillis(26)
    /**
     * Default Espresso master policy timeout used to restore [IdlingPolicies] after short waits.
     */
    private val defaultMasterPolicyTimeoutMs = TimeUnit.MINUTES.toMillis(1)

    /**
     * Short timeout window to detect auto-opened player dialog before fallback actions.
     */
    private const val AUTO_DIALOG_WAIT_MS = 2500L

    /**
     * Blocks until the add-player name field is interactable, opening the dialog via FAB or inline
     * action when the auto-open path does not appear within the short idling window.
     */
    fun ensureAddPlayerNameFieldVisible() {
        instrumentation.waitForIdleSync()
        val idling = PlayerNameFieldIdlingResource(R.id.edi_player_name)
        val registry = IdlingRegistry.getInstance()

        IdlingPolicies.setIdlingResourceTimeout(AUTO_DIALOG_WAIT_MS, TimeUnit.MILLISECONDS)
        IdlingPolicies.setMasterPolicyTimeout(AUTO_DIALOG_WAIT_MS, TimeUnit.MILLISECONDS)
        registry.register(idling)
        try {
            onView(withId(R.id.edi_player_name)).check(matches(isDisplayed()))
            return
        } catch (_: Throwable) {
            // No auto-dialog within timeout, or field not in hierarchy yet
        } finally {
            registry.unregister(idling)
            IdlingPolicies.setIdlingResourceTimeout(defaultIdlingResourceTimeoutMs, TimeUnit.MILLISECONDS)
            IdlingPolicies.setMasterPolicyTimeout(defaultMasterPolicyTimeoutMs, TimeUnit.MILLISECONDS)
        }

        if (!tryClickIfDisplayed(R.id.fab_add_player)) {
            tryClickIfDisplayed(R.id.btnAddPlayerInline)
        }

        registry.register(idling)
        try {
            onView(withId(R.id.edi_player_name)).check(matches(isDisplayed()))
        } finally {
            registry.unregister(idling)
        }
    }

    /**
     * Best-effort click used when the auto-open dialog path times out.
     *
     * @param id view resource id to click when already displayed
     * @return `true` when the view was visible and a click was dispatched
     */
    private fun tryClickIfDisplayed(id: Int): Boolean =
        try {
            onView(withId(id)).check(matches(isDisplayed()))
            onView(withId(id)).perform(click())
            instrumentation.waitForIdleSync()
            true
        } catch (_: Throwable) {
            false
        }
}
