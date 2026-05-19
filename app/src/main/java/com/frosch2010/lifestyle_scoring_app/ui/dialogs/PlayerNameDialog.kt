package com.frosch2010.lifestyle_scoring_app.ui.dialogs

import android.content.Context
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AlertDialog
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.databinding.DialogTxtAddPlayerBinding
import com.frosch2010.lifestyle_scoring_app.utils.InputValidator
import com.frosch2010.lifestyle_scoring_app.utils.PlayerNameValidationResult
import com.google.android.material.dialog.MaterialAlertDialogBuilder
/**
 * Material text-input dialog for creating or renaming players with immediate inline validation.
 *
 * Positive button clicks are intercepted so empty names never dismiss the dialog silently.
 *
 * @param context Theme context for inflation and string resources.
 * @param listener Receiver of the trimmed valid name after OK or IME action.
 * @param dialogTitleRes Title string resource distinguishing add vs rename flows.
 * @param initialName Seed text placed in the field when editing an existing player.
 * @param onDismiss Optional hook invoked for any dismissal path after the window closes.
 */
class PlayerNameDialog(
    private val context: Context,
    private val listener: OnNameEnteredListener,
    private val dialogTitleRes: Int = R.string.enter_player_name,
    private val initialName: String = "",
    private val onDismiss: (() -> Unit)? = null
) {

    /**
     * Builds the dialog, wires IME actions to the same validation path as OK, and shows the window.
     */
    fun show() {
        val builder = MaterialAlertDialogBuilder(context)
        builder.setTitle(context.getString(dialogTitleRes))

        val binding = DialogTxtAddPlayerBinding.inflate(android.view.LayoutInflater.from(context))
        builder.setView(binding.root)

        builder.setPositiveButton(context.getString(R.string.ok), null)

        builder.setNegativeButton(context.getString(R.string.abort)) { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()

        val tryConfirm: () -> Unit = {
            when (val result = InputValidator.validatePlayerName(binding.ediPlayerName.text?.toString().orEmpty())) {
                is PlayerNameValidationResult.Valid -> {
                    binding.textInputLayout.error = null
                    listener.onNameEntered(result.value)
                    dialog.dismiss()
                }
                PlayerNameValidationResult.Empty -> {
                    binding.textInputLayout.error = context.getString(R.string.player_name_empty)
                }
            }
        }

        dialog.setOnDismissListener {
            onDismiss?.invoke()
        }

        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            tryConfirm()
        }

        binding.ediPlayerName.setText(initialName)
        binding.ediPlayerName.setSelection(binding.ediPlayerName.text?.length ?: 0)

        binding.ediPlayerName.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                actionId == EditorInfo.IME_ACTION_GO ||
                actionId == EditorInfo.IME_ACTION_SEND
            ) {
                tryConfirm()
                true
            } else {
                false
            }
        }
    }

    /**
     * Host-implemented callback fired exactly once per successful confirmation.
     */
    interface OnNameEnteredListener {
        /**
         * @param playerName Non-blank trimmed name ready for repository persistence.
         */
        fun onNameEntered(playerName: String)
    }
}
