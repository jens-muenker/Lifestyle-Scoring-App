package com.frosch2010.lifestyle_scoring_app.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.databinding.LstItemPointsSummaryBinding
import com.frosch2010.lifestyle_scoring_app.domain.model.CategoryPointsEntry
import com.frosch2010.lifestyle_scoring_app.ui.util.pointsCategoryIconResId
import com.frosch2010.lifestyle_scoring_app.ui.util.pointsCategoryLabelResId
import com.frosch2010.lifestyle_scoring_app.ui.util.pointsCategoryTintResId
import com.google.android.material.card.MaterialCardView
import com.google.android.material.color.MaterialColors
import com.google.android.material.textview.MaterialTextView

/**
 * Concat-adapter header that visualizes total points, expands into per-category rows, and stays diff-friendly.
 *
 * @param onToggleRequested Invoked when the user toggles breakdown visibility; host flips persisted expand state.
 */
class PointsSummaryAdapter(
    private val onToggleRequested: () -> Unit
) : ListAdapter<PointsSummaryAdapter.SummaryItem, PointsSummaryAdapter.ViewHolder>(
    AsyncDifferConfig.Builder(DiffCallback())
        .setBackgroundThreadExecutor { runnable -> runnable.run() }
        .build()
) {

    /**
     * Single-row payload (list length is always one) carrying totals, breakdown entries, and expansion bit.
     *
     * @property totalLabel Preformatted headline string for the total score line.
     * @property entries Per-category rows rendered when expanded.
     * @property isExpanded Whether nested breakdown rows and dividers are visible.
     */
    data class SummaryItem(
        val totalLabel: String,
        val entries: List<CategoryPointsEntry>,
        val isExpanded: Boolean
    )

    /**
     * View holder for `LstItemPointsSummary`.
     */
    class ViewHolder(val binding: LstItemPointsSummaryBinding) : RecyclerView.ViewHolder(binding.root)

    /**
     * @param parent Inflating RecyclerView.
     * @param viewType Unused.
     * @return Holder with summary binding inflated.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LstItemPointsSummaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    /**
     * Binds total label, toggle affordance, expansion visibility, padding, and dynamically built breakdown rows.
     *
     * @param holder Summary cardholder.
     * @param position Always `0` for the single summary item.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.txtCategoryPointsTotal.text = item.totalLabel
        holder.binding.btnToggleBreakdown.setText(
            if (item.isExpanded) R.string.player_cards_hide_breakdown else R.string.player_cards_show_breakdown
        )
        holder.binding.btnToggleBreakdown.setIconResource(
            if (item.isExpanded) R.drawable.ic_expand_less else R.drawable.ic_expand_more
        )
        holder.binding.scrollPointsRows.visibility = if (item.isExpanded) View.VISIBLE else View.GONE
        holder.binding.dividerPointsSummary.visibility = if (item.isExpanded) View.VISIBLE else View.GONE
        holder.binding.btnToggleBreakdown.setOnClickListener { onToggleRequested() }
        applySummaryContainerSizing(holder, item)

        renderRows(holder, item)
    }

    /**
     * Publishes a fresh summary snapshot; list size remains one to simplify concat interactions.
     *
     * @param total Formatted total points string for the headline.
     * @param breakdownEntries Category rows from the domain breakdown model.
     * @param expanded Current expansion flag from activity/view model state.
     */
    fun update(total: String, breakdownEntries: List<CategoryPointsEntry>, expanded: Boolean) {
        submitList(listOf(SummaryItem(total, breakdownEntries, expanded)))
    }

    /**
     * Adjusts vertical padding and minimum height so collapsed vs expanded layouts feel intentional.
     *
     * @param holder Row whose `layoutSummaryContent` receives padding tweaks.
     * @param item Expansion flag driving compact vs relaxed vertical padding.
     */
    private fun applySummaryContainerSizing(holder: ViewHolder, item: SummaryItem) {
        val context = holder.itemView.context
        val horizontal = context.resources.getDimensionPixelSize(R.dimen.screen_horizontal_padding)
        val compactVertical = context.resources.getDimensionPixelSize(R.dimen.spacing_xsmall) / 2
        val expandedVertical = context.resources.getDimensionPixelSize(R.dimen.spacing_medium)

        holder.binding.layoutSummaryContent.minimumHeight =
            context.resources.getDimensionPixelSize(R.dimen.list_item_min_height)
        holder.binding.layoutSummaryContent.setPadding(
            horizontal,
            if (item.isExpanded) expandedVertical else compactVertical,
            horizontal,
            if (item.isExpanded) expandedVertical else compactVertical
        )
    }

    /**
     * Builds programmatic `MaterialCardView` rows for each [CategoryPointsEntry] with icon, label, and value.
     *
     * @param holder Summary holder whose `layoutPointsRows` container is cleared and repopulated.
     * @param item Snapshot containing ordered breakdown entries.
     */
    private fun renderRows(holder: ViewHolder, item: SummaryItem) {
        holder.binding.layoutPointsRows.removeAllViews()
        val context = holder.itemView.context

        val rowMinHeight = context.resources.getDimensionPixelSize(R.dimen.list_item_min_height)
        val rowPaddingVertical = context.resources.getDimensionPixelSize(R.dimen.spacing_xsmall) / 2
        val rowPaddingHorizontal = context.resources.getDimensionPixelSize(R.dimen.spacing_small)
        val iconBoxSize = context.resources.getDimensionPixelSize(R.dimen.icon_container_size)
        val iconSize = context.resources.getDimensionPixelSize(R.dimen.icon_medium)
        val labelColor = MaterialColors.getColor(holder.binding.root, com.google.android.material.R.attr.colorOnSurfaceVariant)
        val valueColor = MaterialColors.getColor(holder.binding.root, com.google.android.material.R.attr.colorOnSurface)
        val iconContainerColor = MaterialColors.getColor(holder.binding.root, com.google.android.material.R.attr.colorSurfaceContainerLow)

        item.entries.forEachIndexed { index, rowData ->
            val rowCard = MaterialCardView(context).apply {
                layoutParams = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    if (index > 0) topMargin = context.resources.getDimensionPixelSize(R.dimen.spacing_xsmall) / 2
                }
                radius = context.resources.getDimension(R.dimen.radius_large)
                strokeWidth = context.resources.getDimensionPixelSize(R.dimen.stroke_width_hairline)
                strokeColor = MaterialColors.getColor(this, com.google.android.material.R.attr.colorOutlineVariant)
                setCardBackgroundColor(MaterialColors.getColor(this, com.google.android.material.R.attr.colorSurface))
            }

            val row = LinearLayout(context).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                orientation = LinearLayout.HORIZONTAL
                gravity = android.view.Gravity.CENTER_VERTICAL
                minimumHeight = rowMinHeight
                setPadding(rowPaddingHorizontal, rowPaddingVertical, rowPaddingHorizontal, rowPaddingVertical)
            }

            val iconContainer = LinearLayout(context).apply {
                layoutParams = LinearLayout.LayoutParams(iconBoxSize, iconBoxSize)
                gravity = android.view.Gravity.CENTER
                background = ContextCompat.getDrawable(context, R.drawable.bg_manual_icon_circle)
                backgroundTintList = android.content.res.ColorStateList.valueOf(iconContainerColor)
                setPadding(
                    context.resources.getDimensionPixelSize(R.dimen.spacing_xsmall) / 2,
                    context.resources.getDimensionPixelSize(R.dimen.spacing_xsmall) / 2,
                    context.resources.getDimensionPixelSize(R.dimen.spacing_xsmall) / 2,
                    context.resources.getDimensionPixelSize(R.dimen.spacing_xsmall) / 2
                )
            }

            val iconView = ImageView(context).apply {
                layoutParams = LinearLayout.LayoutParams(iconSize, iconSize)
                setImageResource(rowData.category.pointsCategoryIconResId())
                imageTintList = android.content.res.ColorStateList.valueOf(
                    ContextCompat.getColor(context, rowData.category.pointsCategoryTintResId())
                )
                contentDescription = null
                importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
            }
            iconContainer.addView(iconView)

            val labelView = MaterialTextView(context).apply {
                val labelMarginStart = context.resources.getDimensionPixelSize(R.dimen.spacing_small)
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                    .apply { marginStart = labelMarginStart }
                text = context.getString(rowData.category.pointsCategoryLabelResId())
                setTextColor(labelColor)
                setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_BodyMedium)
            }

            val valueView = MaterialTextView(context).apply {
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                text = rowData.points.toString()
                setTextColor(valueColor)
                setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_BodyLarge)
                setTypeface(typeface, android.graphics.Typeface.BOLD)
            }

            row.addView(iconContainer)
            row.addView(labelView)
            row.addView(valueView)
            rowCard.addView(row)
            holder.binding.layoutPointsRows.addView(rowCard)
        }
    }

    /**
     * Because only one logical row exists, items are always considered the same id with payload equality checks.
     */
    private class DiffCallback : DiffUtil.ItemCallback<SummaryItem>() {
        override fun areItemsTheSame(oldItem: SummaryItem, newItem: SummaryItem): Boolean = true

        override fun areContentsTheSame(oldItem: SummaryItem, newItem: SummaryItem): Boolean =
            oldItem == newItem
    }
}
