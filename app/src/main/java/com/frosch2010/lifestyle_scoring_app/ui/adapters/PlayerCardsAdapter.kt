package com.frosch2010.lifestyle_scoring_app.ui.adapters

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.databinding.LstItemCardBinding
import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import com.frosch2010.lifestyle_scoring_app.ui.util.pointsCategoryIconResId
import com.frosch2010.lifestyle_scoring_app.ui.util.pointsCategoryLabelResId
import com.frosch2010.lifestyle_scoring_app.ui.dialogs.DeletePlayerCardDialog
import com.frosch2010.lifestyle_scoring_app.ui.viewmodels.dto.CardDTO
import com.google.android.material.card.MaterialCardView
import com.google.android.material.color.MaterialColors
import com.frosch2010.lifestyle_scoring_app.ui.viewmodels.PlayerCardsViewModel

/**
 * Renders a single player's hand with category tinting, optional car points, and long-press delete.
 *
 * When used inside a [androidx.recyclerview.widget.ConcatAdapter], [leadingAdapterItemCount] translates global
 * adapter positions back into repository-aligned card indices for delete dialogs.
 *
 * @param context Host context for colors, drawables, and string plurals.
 * @param viewModel Mutates repository state after delete confirmation.
 * @param leadingAdapterItemCount Supplier returning the number of items ahead of this adapter in the concat chain.
 */
class PlayerCardsAdapter(
    private val context: Context,
    private val viewModel: PlayerCardsViewModel,
    private val leadingAdapterItemCount: () -> Int
) : ListAdapter<CardDTO, PlayerCardsAdapter.ViewHolder>(DiffCallback()),
    DeletePlayerCardDialog.IDeletePlayerCardDialogCallback {

    /**
     * View holder for one `LstItemCard` row.
     */
    class ViewHolder(val binding: LstItemCardBinding) : RecyclerView.ViewHolder(binding.root)

    /**
     * @param parent Inflating RecyclerView.
     * @param viewType Unused single type.
     * @return Holder with binding inflated.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LstItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    /**
     * Binds labels, car point plurals, stroke tint, iconography, and long-press delete menu wiring.
     *
     * @param holder Row view holder.
     * @param position Local adapter index for this sub-adapter.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.binding.cardName.text = item.name
        holder.binding.cardType.text = context.getString(item.cardType.pointsCategoryLabelResId())

        if (item.cardType == CardTypeEnum.CAR) {
            holder.binding.cardPoints.visibility = View.VISIBLE
            val points = item.points ?: 0
            holder.binding.cardPoints.text = context.resources.getQuantityString(
                R.plurals.points_count,
                points,
                points
            )
            val pointsA11y = context.resources.getQuantityString(
                R.plurals.points_count,
                points,
                points
            )
            holder.binding.root.contentDescription = context.getString(
                R.string.a11y_card_row_with_points,
                item.name,
                pointsA11y
            )
        } else {
            holder.binding.cardPoints.visibility = View.GONE
            holder.binding.root.contentDescription = context.getString(R.string.a11y_card_row, item.name)
        }

        val color = when (item.cardType) {
            CardTypeEnum.CAR -> ContextCompat.getColor(context, R.color.card_car)
            CardTypeEnum.HOUSE -> ContextCompat.getColor(context, R.color.card_house)
            CardTypeEnum.JOB -> ContextCompat.getColor(context, R.color.card_job)
            CardTypeEnum.ANIMAL -> ContextCompat.getColor(context, R.color.card_animal)
            CardTypeEnum.LOVE -> ContextCompat.getColor(context, R.color.card_love)
            CardTypeEnum.SPORT -> ContextCompat.getColor(context, R.color.card_sport)
        }
        (holder.binding.root as? MaterialCardView)?.strokeColor = color
        holder.binding.cardIcon.imageTintList = android.content.res.ColorStateList.valueOf(color)
        holder.binding.iconContainer.backgroundTintList =
            android.content.res.ColorStateList.valueOf(
                MaterialColors.getColor(holder.binding.root, com.google.android.material.R.attr.colorSurfaceContainerLow)
            )

        holder.itemView.setOnLongClickListener {
            val globalPosition = holder.absoluteAdapterPosition
            if (globalPosition == RecyclerView.NO_POSITION) {
                return@setOnLongClickListener true
            }
            val cardIndex = globalPosition - leadingAdapterItemCount()
            if (cardIndex < 0) {
                return@setOnLongClickListener true
            }
            val popupMenu = PopupMenu(it.context, it)
            popupMenu.menuInflater.inflate(R.menu.reuse_menu_delete, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                if (menuItem.itemId == R.id.reuse_menu_delete) {
                    val deletePlayerCardDialog = DeletePlayerCardDialog(context, this)
                    deletePlayerCardDialog.show(
                        context.getString(R.string.delete_card),
                        context.getString(R.string.message_delete_card),
                        cardIndex
                    )
                }
                true
            }
            popupMenu.gravity = Gravity.END
            popupMenu.show()
            true
        }

        val iconRes = item.icon ?: item.cardType.pointsCategoryIconResId()
        holder.binding.cardIcon.setImageDrawable(AppCompatResources.getDrawable(context, iconRes))
        holder.binding.cardIcon.visibility = View.VISIBLE
    }

    /**
     * @param newCards Updated hand snapshot from the ViewModel.
     */
    fun updateData(newCards: List<CardDTO>) {
        submitList(newCards)
    }

    /**
     * @param cardIndex Repository-aligned index computed during long-press handling.
     */
    override fun onDeleteConfirmed(cardIndex: Int) {
        viewModel.deleteCard(cardIndex)
    }

    /**
     * Structural equality on the entire [CardDTO] because cards lack stable ids beyond value equality.
     */
    private class DiffCallback : DiffUtil.ItemCallback<CardDTO>() {
        override fun areItemsTheSame(oldItem: CardDTO, newItem: CardDTO): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: CardDTO, newItem: CardDTO): Boolean {
            return oldItem == newItem
        }
    }
}
