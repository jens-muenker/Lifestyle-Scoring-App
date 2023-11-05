package com.frosch2010.lifestyle_scoring_app.ui.dialogs

import android.content.Context
import android.widget.ArrayAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * This dialog is used to select an item from a list of items.
 * @author Jens Münker
 */
class ListDialog(private val context: Context, private val title: String, private val items: List<String>, private val listener: OnItemSelectedListener) {

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

    interface OnItemSelectedListener {
        fun onItemSelected(item: String)
    }
}
