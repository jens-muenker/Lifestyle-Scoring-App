package com.frosch2010.lifestyle_scoring_app.ui.adapters

import android.view.ContextThemeWrapper
import android.widget.FrameLayout
import androidx.test.core.app.ApplicationProvider
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.domain.model.CategoryPointsEntry
import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Unit tests for [PointsSummaryAdapter].
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class PointsSummaryAdapterTest {

    /**
     * Verifies `bind_collapsedState_setsTextAndHidesBreakdownRows()` behavior.
     */
    @Test
    fun bind_collapsedState_setsTextAndHidesBreakdownRows() {
        var toggleCalls = 0
        val adapter = PointsSummaryAdapter { toggleCalls++ }
        adapter.update(
            total = "Total: 8",
            breakdownEntries = listOf(
                CategoryPointsEntry(CardTypeEnum.CAR, 3),
                CategoryPointsEntry(CardTypeEnum.ANIMAL, 5)
            ),
            expanded = false
        )

        val context = ContextThemeWrapper(
            ApplicationProvider.getApplicationContext(),
            R.style.Theme_LifestyleScoringApp
        )
        val parent = FrameLayout(context)
        val holder = adapter.onCreateViewHolder(parent, 0)
        adapter.onBindViewHolder(holder, 0)

        assertEquals("Total: 8", holder.binding.txtCategoryPointsTotal.text.toString())
        assertEquals(context.getString(R.string.player_cards_show_breakdown), holder.binding.btnToggleBreakdown.text.toString())
        assertEquals(android.view.View.GONE, holder.binding.scrollPointsRows.visibility)
        assertEquals(android.view.View.GONE, holder.binding.dividerPointsSummary.visibility)
        assertEquals(2, holder.binding.layoutPointsRows.childCount)

        holder.binding.btnToggleBreakdown.performClick()
        assertEquals(1, toggleCalls)
    }

    /**
     * Verifies `bind_expandedState_showsBreakdownRowsAndHideLabel()` behavior.
     */
    @Test
    fun bind_expandedState_showsBreakdownRowsAndHideLabel() {
        val adapter = PointsSummaryAdapter { }
        adapter.update(
            total = "Total: 3",
            breakdownEntries = listOf(CategoryPointsEntry(CardTypeEnum.CAR, 3)),
            expanded = true
        )

        val context = ContextThemeWrapper(
            ApplicationProvider.getApplicationContext(),
            R.style.Theme_LifestyleScoringApp
        )
        val parent = FrameLayout(context)
        val holder = adapter.onCreateViewHolder(parent, 0)
        adapter.onBindViewHolder(holder, 0)

        assertEquals(context.getString(R.string.player_cards_hide_breakdown), holder.binding.btnToggleBreakdown.text.toString())
        assertEquals(android.view.View.VISIBLE, holder.binding.scrollPointsRows.visibility)
        assertEquals(android.view.View.VISIBLE, holder.binding.dividerPointsSummary.visibility)
        assertEquals(1, holder.binding.layoutPointsRows.childCount)
        assertTrue(holder.binding.layoutSummaryContent.minimumHeight > 0)
    }
}

