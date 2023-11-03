package com.frosch2010.lifestyle_scoring_app.ui.decorations

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class AdaptiveSpacingItemDecoration(
    private val size: Int,
    private val edgeEnabled: Boolean = false
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        return if (parent.getChildAdapterPosition(view) == 0 && edgeEnabled) {
            outRect.set(size, size, size, size)
        } else {
            outRect.set(size, 0, size, size)
        }
    }
}