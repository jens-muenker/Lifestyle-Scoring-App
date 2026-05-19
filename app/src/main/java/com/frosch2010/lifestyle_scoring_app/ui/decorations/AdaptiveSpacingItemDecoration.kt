package com.frosch2010.lifestyle_scoring_app.ui.decorations

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Applies uniform inter-item spacing with optional symmetric padding on the first list row.
 *
 * @param size Pixel spacing applied as horizontal margins and bottom margin for every item.
 * @param edgeEnabled When `true`, the first item also receives top spacing so grids breathe against the toolbar edge.
 */
class AdaptiveSpacingItemDecoration(
    private val size: Int,
    private val edgeEnabled: Boolean = false
) : RecyclerView.ItemDecoration() {

    /**
     * Insets each child so adjacent cards never touch while respecting the first-row top gutter policy.
     *
     * @param outRect Rect to populate with pixel offsets for the child.
     * @param view Child view being measured.
     * @param parent RecyclerView hosting the item.
     * @param state Current scroll and adapter state (unused but forwarded by the framework).
     */
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        return if (parent.getChildAdapterPosition(view) == 0 && edgeEnabled) {
            outRect.set(size, size, size, size)
        } else {
            outRect.set(size, 0, size, size)
        }
    }
}
