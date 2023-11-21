package com.frosch2010.lifestyle_scoring_app.ui.dialogs

import android.content.Context
import com.frosch2010.lifestyle_scoring_app.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DeletePlayerDialog(private val context: Context, private val listener: IDeletePlayerDialogCallback) {

    fun show(title: String, message: String, cardIndex: Int) {
        val builder = MaterialAlertDialogBuilder(context)
        builder.setTitle(title)
        builder.setMessage(message)

        builder.setPositiveButton(context.getString(R.string.yes)) { dialog, _ ->
            listener.onDeleteConfirmed(cardIndex)
            dialog.dismiss()
        }

        builder.setNegativeButton(context.getString(R.string.abort)) { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    interface IDeletePlayerDialogCallback {
        fun onDeleteConfirmed(playerIndex: Int)
    }
}