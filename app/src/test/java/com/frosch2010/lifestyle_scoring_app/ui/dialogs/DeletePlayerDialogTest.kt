package com.frosch2010.lifestyle_scoring_app.ui.dialogs

import android.view.ContextThemeWrapper
import androidx.appcompat.app.AlertDialog
import androidx.test.core.app.ApplicationProvider
import com.frosch2010.lifestyle_scoring_app.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowDialog
import org.robolectric.shadows.ShadowLooper

/**
 * Robolectric-backed tests for [DeletePlayerDialog] (UI-/Android APIs im JVM-Test).
 */

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class DeletePlayerDialogTest {

    private fun themedContext() = ContextThemeWrapper(
        ApplicationProvider.getApplicationContext(),
        R.style.Theme_LifestyleScoringApp
    )

    /**
     * Verifies `show_positiveButton_invokesCallbackWithIndex` behavior.
     */
    @Test
    fun show_positiveButton_invokesCallbackWithIndex() {
        var index: Int? = null
        DeletePlayerDialog(
            themedContext(),
            object : DeletePlayerDialog.IDeletePlayerDialogCallback {
                override fun onDeleteConfirmed(playerIndex: Int) {
                    index = playerIndex
                }
            }
        ).show("t", "m", 7)

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
        val dialog = ShadowDialog.getLatestDialog() as AlertDialog
        dialog.findViewById<android.widget.Button>(android.R.id.button1)!!.performClick()
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
        assertEquals(7, index)
    }

    /**
     * Verifies `show_negativeButton_doesNotInvokeCallback` behavior.
     */
    @Test
    fun show_negativeButton_doesNotInvokeCallback() {
        var index: Int? = null
        DeletePlayerDialog(
            themedContext(),
            object : DeletePlayerDialog.IDeletePlayerDialogCallback {
                override fun onDeleteConfirmed(playerIndex: Int) {
                    index = playerIndex
                }
            }
        ).show("t", "m", 2)

        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
        val dialog = ShadowDialog.getLatestDialog() as AlertDialog
        dialog.findViewById<android.widget.Button>(android.R.id.button2)!!.performClick()
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
        assertNull(index)
    }
}
