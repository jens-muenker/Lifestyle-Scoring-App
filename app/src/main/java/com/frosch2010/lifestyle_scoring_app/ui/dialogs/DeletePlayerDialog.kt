package com.frosch2010.lifestyle_scoring_app.ui.dialogs

import android.content.Context
import com.frosch2010.lifestyle_scoring_app.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Confirmation dialog before removing a player from the in-memory roster.
 *
 * @param context Theme context for string resources and theming.
 * @param listener Target that receives the affirmative callback with the roster index.
 */
class DeletePlayerDialog(
    private val context: Context,
    private val listener: IDeletePlayerDialogCallback
) {

    /**
     * Presents destructive confirmation messaging for roster deletion.
     *
     * @param title Localized dialog title (typically "Delete player").
     * @param message Localized body explaining irreversibility.
     * @param cardIndex Legacy parameter name carrying the **player roster index** to delete on confirm.
     */
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

    /**
     * Host contract for applying the deletion after explicit user consent.
     */
    interface IDeletePlayerDialogCallback {
        /**
         * Applies the confirmed delete action to the host state.
         *
         * @param playerIndex Roster slot to remove (legacy callers may still name the parameter `cardIndex` in UI code).
         */
        fun onDeleteConfirmed(playerIndex: Int)
    }
}
