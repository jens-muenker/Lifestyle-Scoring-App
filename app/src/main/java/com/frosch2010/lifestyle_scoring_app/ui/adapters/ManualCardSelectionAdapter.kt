package com.frosch2010.lifestyle_scoring_app.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.databinding.LstItemManualCardBinding
import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import com.frosch2010.lifestyle_scoring_app.ui.viewmodels.dto.ManualCardOptionDTO
/**
 * Searchable list rows for manual card selection with category-colored strokes and icon treatments.
 *
 * @param onCardClicked Invoked with the full [ManualCardOptionDTO] when a row is tapped.
 */
class ManualCardSelectionAdapter(
    private val onCardClicked: (ManualCardOptionDTO) -> Unit
) : ListAdapter<ManualCardOptionDTO, ManualCardSelectionAdapter.ViewHolder>(DiffCallback()) {

    /**
     * Row holder for `LstItemManualCard`.
     */
    class ViewHolder(val binding: LstItemManualCardBinding) : RecyclerView.ViewHolder(binding.root)

    /**
     * @param parent RecyclerView inflating this adapter.
     * @param viewType Unused single view type id.
     * @return Inflated [ViewHolder].
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LstItemManualCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    /**
     * Binds display strings, accent colors derived from [CardTypeEnum], and click forwarding.
     *
     * @param holder Target row.
     * @param position Index into the current filtered list.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val accentColor = getTypeColor(holder, item.cardType)
        holder.binding.txtCardName.text = item.name
        holder.binding.txtCardType.text = getTypeLabel(holder, item.cardType)
        holder.binding.imgTypeIcon.setImageResource(item.iconRes)
        holder.binding.imgTypeIcon.setColorFilter(accentColor)
        holder.binding.iconContainer.background.setTint(ColorUtils.setAlphaComponent(accentColor, 45))
        holder.binding.root.strokeColor = ColorUtils.setAlphaComponent(accentColor, 90)
        holder.itemView.setOnClickListener {
            onCardClicked(item)
        }
    }

    /**
     * @param holder Row whose [android.content.Context] is used for string resolution.
     * @param type Card pillar determining which category label to show.
     * @return Localized short category title.
     */
    private fun getTypeLabel(holder: ViewHolder, type: CardTypeEnum): String {
        val context = holder.itemView.context
        return when (type) {
            CardTypeEnum.ANIMAL -> context.getString(R.string.points_category_animal)
            CardTypeEnum.CAR -> context.getString(R.string.points_category_car)
            CardTypeEnum.HOUSE -> context.getString(R.string.points_category_house)
            CardTypeEnum.JOB -> context.getString(R.string.points_category_job)
            CardTypeEnum.SPORT -> context.getString(R.string.points_category_sport)
            CardTypeEnum.LOVE -> context.getString(R.string.points_category_love)
        }
    }

    /**
     * @param holder Row whose context resolves theme colors.
     * @param type Card pillar determining the accent RGB.
     * @return Fully opaque color for strokes and filters.
     */
    private fun getTypeColor(holder: ViewHolder, type: CardTypeEnum): Int {
        val context = holder.itemView.context
        return when (type) {
            CardTypeEnum.ANIMAL -> ContextCompat.getColor(context, R.color.card_animal)
            CardTypeEnum.CAR -> ContextCompat.getColor(context, R.color.card_car)
            CardTypeEnum.HOUSE -> ContextCompat.getColor(context, R.color.card_house)
            CardTypeEnum.JOB -> ContextCompat.getColor(context, R.color.card_job)
            CardTypeEnum.SPORT -> ContextCompat.getColor(context, R.color.card_sport)
            CardTypeEnum.LOVE -> ContextCompat.getColor(context, R.color.card_love)
        }
    }

    /**
     * Uses composite name+type equality so renamed love rows still diff correctly.
     */
    private class DiffCallback : DiffUtil.ItemCallback<ManualCardOptionDTO>() {
        override fun areItemsTheSame(oldItem: ManualCardOptionDTO, newItem: ManualCardOptionDTO): Boolean =
            oldItem.name == newItem.name && oldItem.cardType == newItem.cardType

        override fun areContentsTheSame(oldItem: ManualCardOptionDTO, newItem: ManualCardOptionDTO): Boolean =
            oldItem == newItem
    }
}
