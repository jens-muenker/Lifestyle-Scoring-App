package com.frosch2010.lifestyle_scoring_app.ui.dialogs

import android.view.ContextThemeWrapper
import androidx.appcompat.app.AlertDialog
import androidx.test.core.app.ApplicationProvider
import com.frosch2010.lifestyle_scoring_app.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowDialog
import org.robolectric.shadows.ShadowLooper

/**
 * Robolectric-backed tests for [DeletePlayerCardDialog] (UI-/Android APIs im JVM-Test).
 */

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class DeletePlayerCardDialogTest {

    private fun themedContext() = ContextThemeWrapper(
        ApplicationProvider.getApplicationContext(),
        R.style.Theme_LifestyleScoringApp
    )

    /**
     * Verifies `show_positiveButton_invokesCallbackWithCardIndex` behavior.
     */
    @Test
    fun show_positiveButton_invokesCallbackWithCardIndex() {
        var index: Int? = null
        DeletePlayerCardDialog(
            themedContext(),
            object : DeletePlayerCardDialog.IDeletePlayerCardDialogCallback {
                override fun onDeleteConfirmed(cardIndex: Int) {
                    index = cardIndex
                }
            }
        ).show("t", "m", 4)

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
        val dialog = ShadowDialog.getLatestDialog() as AlertDialog
        dialog.findViewById<android.widget.Button>(android.R.id.button1)!!.performClick()
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
        assertEquals(4, index)
    }

    /**
     * Verifies `show_negativeButton_doesNotInvokeCallback` behavior.
     */
    @Test
    fun show_negativeButton_doesNotInvokeCallback() {
        var index: Int? = null
        DeletePlayerCardDialog(
            themedContext(),
            object : DeletePlayerCardDialog.IDeletePlayerCardDialogCallback {
                override fun onDeleteConfirmed(cardIndex: Int) {
                    index = cardIndex
                }
            }
        ).show("t", "m", 3)

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
        val dialog = ShadowDialog.getLatestDialog() as AlertDialog
        dialog.findViewById<android.widget.Button>(android.R.id.button2)!!.performClick()
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
        assertNull(index)
    }

    /**
     * Verifies `show_onDismissRunsAfterNegativeClose` behavior.
     */
    @Test
    fun show_onDismissRunsAfterNegativeClose() {
        var dismissCalls = 0
        DeletePlayerCardDialog(
            themedContext(),
            object : DeletePlayerCardDialog.IDeletePlayerCardDialogCallback {
                override fun onDeleteConfirmed(cardIndex: Int) {}
            }
        ).show("t", "m", 0, onDismiss = { dismissCalls++ })

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
        val dialog = ShadowDialog.getLatestDialog() as AlertDialog
        dialog.findViewById<android.widget.Button>(android.R.id.button2)!!.performClick()
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
        assertEquals(1, dismissCalls)
        assertTrue(!dialog.isShowing)
    }
}
