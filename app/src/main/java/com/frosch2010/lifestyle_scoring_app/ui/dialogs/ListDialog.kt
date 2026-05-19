package com.frosch2010.lifestyle_scoring_app.ui.dialogs

import android.content.Context
import android.widget.ArrayAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Simple single-column picker backed by `ArrayAdapter` over plain strings (no custom row layout).
 *
 * @param context Theme context for dialog construction.
 * @param title Window title shown above the list.
 * @param items Selectable string rows presented in declaration order.
 * @param listener Receiver invoked with the tapped label when the user confirms a choice.
 */
class ListDialog(
    private val context: Context,
    private val title: String,
    private val items: List<String>,
    private val listener: OnItemSelectedListener
) {

    /**
     * Presents a non-cancelable list dialog; selection dismisses the window after forwarding the label.
     */
    fun show() {
        val builder = MaterialAlertDialogBuilder(context)

        builder.setTitle(title)

        val adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, items)

        builder.setAdapter(adapter) { _, which ->
            val selected = items[which]
            listener.onItemSelected(selected)
        }

        builder.setCancelable(false)

        val dialog = builder.create()
        dialog.show()
    }

    /**
     * Callback for list item activation.
     */
    interface OnItemSelectedListener {
        /**
         * @param item Exact string taken from [items] at the tapped index.
         */
        fun onItemSelected(item: String)
    }
}
