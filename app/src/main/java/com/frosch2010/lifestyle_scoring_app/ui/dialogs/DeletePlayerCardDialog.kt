package com.frosch2010.lifestyle_scoring_app.ui.dialogs

import android.content.Context
import com.frosch2010.lifestyle_scoring_app.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Confirmation dialog before removing a single card from a player's hand.
 *
 * @param context Theme context for Material alert styling.
 * @param listener ViewModel or activity fragment receiving the confirmed card index.
 */
class DeletePlayerCardDialog(
    private val context: Context,
    private val listener: IDeletePlayerCardDialogCallback
) {

    /**
     * Shows yes/no confirmation, optionally notifying the host when the dialog fully dismisses.
     *
     * @param title Localized title such as "Delete card".
     * @param message Localized explanation shown in the content area.
     * @param cardIndex Hand index aligned with repository `Player.cards` ordering.
     * @param onDismiss Optional callback after any close path; used to reset swipe animations on the row.
     */
    fun show(
        title: String,
        message: String,
        cardIndex: Int,
        onDismiss: (() -> Unit)? = null
    ) {
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
        if (onDismiss != null) {
            dialog.setOnDismissListener { onDismiss.invoke() }
        }
        dialog.show()
    }

    /**
     * Callback applied after the user confirms deletion.
     */
    interface IDeletePlayerCardDialogCallback {
        /**
         * @param cardIndex Index within the player's card list to remove.
         */
        fun onDeleteConfirmed(cardIndex: Int)
    }
}
