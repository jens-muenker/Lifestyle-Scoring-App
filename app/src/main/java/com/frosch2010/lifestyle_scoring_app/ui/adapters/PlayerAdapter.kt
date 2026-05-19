package com.frosch2010.lifestyle_scoring_app.ui.adapters

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.databinding.LstItemPlayerBinding
import com.frosch2010.lifestyle_scoring_app.ui.dialogs.DeletePlayerDialog
import com.frosch2010.lifestyle_scoring_app.ui.viewmodels.MainViewModel
import com.frosch2010.lifestyle_scoring_app.ui.viewmodels.dto.PlayerDTO

/**
 * Renders the ordered player roster with tap-to-open and long-press delete affordances.
 *
 * @param callback Host activity implementing navigation into player detail flows.
 * @param context Used for pluralized score strings, menus, and dialog construction.
 * @param viewModel Source of truth for roster mutations triggered from delete confirmation.
 */
class PlayerAdapter(
    private val callback: OnPlayerClickedListener,
    private val context: Context,
    private val viewModel: MainViewModel
) : ListAdapter<PlayerDTO, PlayerAdapter.ViewHolder>(DiffCallback()),
    DeletePlayerDialog.IDeletePlayerDialogCallback {

    /**
     * View holder wrapping player row view binding.
     */
    class ViewHolder(val binding: LstItemPlayerBinding) : RecyclerView.ViewHolder(binding.root)

    /**
     * Listener contract for opening a player when a row is tapped.
     */
    interface OnPlayerClickedListener {
        /**
         * @param index Adapter position of the tapped player, aligned with repository indices.
         */
        fun onPlayerClicked(index: Int)
    }

    /**
     * @param parent RecyclerView inflating this holder.
     * @param viewType Unused single view type.
     * @return Row [ViewHolder] with `LstItemPlayerBinding` inflated.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LstItemPlayerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    /**
     * Binds name, pluralized points, accessibility text, click navigation, and delete popup wiring.
     *
     * @param holder Row container.
     * @param position Adapter index for the current [PlayerDTO].
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.binding.playerName.text = item.name
        val pointsLabel = context.resources.getQuantityString(
            R.plurals.player_points,
            item.score,
            item.score
        )
        holder.binding.playerPoints.text = pointsLabel
        holder.binding.root.contentDescription =
            context.getString(R.string.a11y_player_row, item.name, pointsLabel)
        holder.itemView.setOnClickListener {
            val currentPosition = holder.bindingAdapterPosition
            if (currentPosition != RecyclerView.NO_POSITION) {
                callback.onPlayerClicked(currentPosition)
            }
        }

        holder.itemView.setOnLongClickListener {
            val currentPosition = holder.bindingAdapterPosition
            if (currentPosition == RecyclerView.NO_POSITION) {
                return@setOnLongClickListener true
            }
            val popupMenu = PopupMenu(it.context, it)
            popupMenu.menuInflater.inflate(R.menu.reuse_menu_delete, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                if (menuItem.itemId == R.id.reuse_menu_delete) {
                    val deletePlayerDialog = DeletePlayerDialog(context, this)
                    deletePlayerDialog.show(
                        context.getString(R.string.delete_player),
                        context.getString(R.string.message_delete_player),
                        currentPosition
                    )
                }
                true
            }
            popupMenu.gravity = Gravity.END
            popupMenu.show()
            true
        }
    }

    /**
     * Replaces the visible list with [newPlayers], delegating diffing to [ListAdapter.submitList].
     *
     * @param newPlayers Latest roster snapshot from the ViewModel.
     */
    fun updateData(newPlayers: List<PlayerDTO>) {
        submitList(newPlayers)
    }

    /**
     * Forwards confirmed deletion to the ViewModel after [DeletePlayerDialog] acceptance.
     *
     * @param playerIndex Roster index originally passed into the dialog.
     */
    override fun onDeleteConfirmed(playerIndex: Int) {
        viewModel.deletePlayer(playerIndex)
    }

    /**
     * Diff strategy treating a player row as stable when the display name matches (identity proxy).
     */
    private class DiffCallback : DiffUtil.ItemCallback<PlayerDTO>() {
        /**
         * @param oldItem Player row currently rendered in the old list snapshot.
         * @param newItem Player row at the same diff position in the new snapshot.
         * @return `true` when both rows represent the same roster slot by name equality.
         */
        override fun areItemsTheSame(oldItem: PlayerDTO, newItem: PlayerDTO): Boolean {
            return oldItem.name == newItem.name
        }

        /**
         * @param oldItem Player row currently rendered in the old list snapshot.
         * @param newItem Player row at the same diff position in the new snapshot.
         * @return `true` when all public fields match.
         */
        override fun areContentsTheSame(oldItem: PlayerDTO, newItem: PlayerDTO): Boolean {
            return oldItem == newItem
        }
    }
}
