package com.frosch2010.lifestyle_scoring_app.ui.dialogs

import android.content.Context
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog

class ListDialog(
    private val context: Context,
    private val title: String,
    private val items: List<String>,
    private val listener: OnItemSelectedListener
) {

    fun show() {
        val builder = AlertDialog.Builder(context)
        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL

        builder.setTitle(title)

        val adapter = ArrayAdapter(context, android.R.layout.simple_list_item_1, items)

        builder.setAdapter(adapter) { _, which ->
            val selected = items[which]
            listener.onItemSelected(selected)
        }

        builder.setView(layout)

        // Setze die Dialog-Abbruchbarkeit auf 'false'
        builder.setCancelable(false)

        val dialog = builder.create()
        dialog.show()
    }

    interface OnItemSelectedListener {
        fun onItemSelected(item: String)
    }
}
