package com.frosch2010.lifestyle_scoring_app.ui.dialogs

import android.content.Context
import android.widget.EditText
import androidx.appcompat.app.AlertDialog

class PlayerNameDialog(
    private val context: Context,
    private val listener: OnNameEnteredListener
) {

    fun show() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Spielername eingeben")

        val input = EditText(context)
        builder.setView(input)

        builder.setPositiveButton("OK") { _, _ ->
            val playerName = input.text.toString()
            listener.onNameEntered(playerName)
        }

        builder.setNegativeButton("Abbrechen") { dialog, _ -> dialog.cancel() }

        val dialog = builder.create()
        dialog.show()
    }

    interface OnNameEnteredListener {
        fun onNameEntered(playerName: String)
    }
}