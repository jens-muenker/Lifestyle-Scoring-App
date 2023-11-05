package com.frosch2010.lifestyle_scoring_app.ui.dialogs

import android.content.Context
import android.view.LayoutInflater
import com.frosch2010.lifestyle_scoring_app.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText

/**
 * This dialog is used to enter the name of a player.
 * @author Jens Münker
 */
class PlayerNameDialog(private val context: Context, private val listener: OnNameEnteredListener) {

    fun show() {
        val builder = MaterialAlertDialogBuilder(context)
        builder.setTitle(context.getString(R.string.enter_player_name))

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_txt_add_player, null)
        builder.setView(dialogView)

        builder.setPositiveButton(context.getString(R.string.ok)) { dialog, _ ->
            val playerName = dialogView.findViewById<TextInputEditText>(R.id.edi_player_name).text.toString()
            listener.onNameEntered(playerName)
            dialog.dismiss()
        }

        builder.setNegativeButton(context.getString(R.string.abort)) { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    interface OnNameEnteredListener {
        fun onNameEntered(playerName: String)
    }
}