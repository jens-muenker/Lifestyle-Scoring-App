package com.frosch2010.lifestyle_scoring_app.ui.dialogs

import android.view.ContextThemeWrapper
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.test.core.app.ApplicationProvider
import com.frosch2010.lifestyle_scoring_app.R
import com.google.android.material.textfield.TextInputLayout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowDialog

/**
 * Robolectric tests for validation and callback behavior in [PlayerNameDialog].
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class PlayerNameDialogTest {

    /**
     * Verifies that a valid name triggers callback with trimmed text and closes the dialog.
     */
    @Test
    fun show_withValidName_callsListenerWithTrimmedName_andClosesDialog() {
        val context = ContextThemeWrapper(
            ApplicationProvider.getApplicationContext(),
            R.style.Theme_LifestyleScoringApp
        )
        var capturedName: String? = null
        val dialog = PlayerNameDialog(
            context = context,
            listener = object : PlayerNameDialog.OnNameEnteredListener {
                /**
                 * Captures the submitted player name for assertions.
                 */
                override fun onNameEntered(playerName: String) {
                    capturedName = playerName
                }
            }
        )

        dialog.show()
        val alert = ShadowDialog.getLatestDialog() as AlertDialog
        val editText = alert.findViewById<EditText>(R.id.edi_player_name)!!
        val inputLayout = alert.findViewById<TextInputLayout>(R.id.text_input_layout)!!

        editText.setText("  Alice  ")
        alert.getButton(AlertDialog.BUTTON_POSITIVE).performClick()

        assertEquals("Alice", capturedName)
        assertNull(inputLayout.error)
        assertTrue(!alert.isShowing)
    }

    /**
     * Verifies that an empty name shows an error and keeps the dialog visible.
     */
    @Test
    fun show_withEmptyName_setsValidationError_andKeepsDialogOpen() {
        val context = ContextThemeWrapper(
            ApplicationProvider.getApplicationContext(),
            R.style.Theme_LifestyleScoringApp
        )
        var callbackCalls = 0
        val dialog = PlayerNameDialog(
            context = context,
            listener = object : PlayerNameDialog.OnNameEnteredListener {
                /**
                 * Tracks callback invocations for validation assertions.
                 */
                override fun onNameEntered(playerName: String) {
                    callbackCalls++
                }
            }
        )

        dialog.show()
        val alert = ShadowDialog.getLatestDialog() as AlertDialog
        val editText = alert.findViewById<EditText>(R.id.edi_player_name)!!
        val inputLayout = alert.findViewById<TextInputLayout>(R.id.text_input_layout)!!

        editText.setText("   ")
        alert.getButton(AlertDialog.BUTTON_POSITIVE).performClick()

        assertEquals(0, callbackCalls)
        assertEquals(context.getString(R.string.player_name_empty), inputLayout.error?.toString())
        assertTrue(alert.isShowing)
    }
}

