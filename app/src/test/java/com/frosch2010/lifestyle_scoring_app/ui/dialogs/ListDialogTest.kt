package com.frosch2010.lifestyle_scoring_app.ui.dialogs

import android.view.ContextThemeWrapper
import androidx.appcompat.app.AlertDialog
import androidx.test.core.app.ApplicationProvider
import com.frosch2010.lifestyle_scoring_app.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowDialog

/**
 * Robolectric-backed tests for [ListDialog] (UI-/Android APIs im JVM-Test).
 */

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ListDialogTest {

    private fun themedContext() = ContextThemeWrapper(
        ApplicationProvider.getApplicationContext(),
        R.style.Theme_LifestyleScoringApp
    )

    /**
     * Verifies `show_listItemClick_notifiesSelectionAndDismisses` behavior.
     */
    @Test
    fun show_listItemClick_notifiesSelectionAndDismisses() {
        var selected: String? = null
        ListDialog(
            themedContext(),
            "Titel",
            listOf("A", "B"),
            object : ListDialog.OnItemSelectedListener {
                override fun onItemSelected(item: String) {
                    selected = item
                }
            }
        ).show()

        val dialog = ShadowDialog.getLatestDialog() as AlertDialog
        val listView = dialog.listView
        val adapter = listView.adapter
        val row = adapter.getView(1, null, listView)
        listView.performItemClick(row, 1, adapter.getItemId(1))

        assertEquals("B", selected)
        assertTrue(!dialog.isShowing)
    }
}
