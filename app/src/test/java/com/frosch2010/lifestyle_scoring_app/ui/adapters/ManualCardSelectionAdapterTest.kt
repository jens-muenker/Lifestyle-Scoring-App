package com.frosch2010.lifestyle_scoring_app.ui.adapters

import android.view.ContextThemeWrapper
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.models.entities.HouseCard
import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.HouseTypeEnum
import com.frosch2010.lifestyle_scoring_app.ui.viewmodels.dto.ManualCardOptionDTO
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper

/**
 * Robolectric-backed tests for [ManualCardSelectionAdapter] (UI-/Android APIs im JVM-Test).
 */

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ManualCardSelectionAdapterTest {

    private fun layoutRecyclerView(recyclerView: RecyclerView) {
        recyclerView.measure(
            View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(4096, View.MeasureSpec.AT_MOST)
        )
        recyclerView.layout(0, 0, recyclerView.measuredWidth, recyclerView.measuredHeight)
    }

    /**
     * Verifies `bind_setsTextsAndClickEmitsOption` behavior.
     */
    @Test
    fun bind_setsTextsAndClickEmitsOption() {
        val context = ContextThemeWrapper(
            ApplicationProvider.getApplicationContext(),
            R.style.Theme_LifestyleScoringApp
        )
        val card = HouseCard(CardTypeEnum.HOUSE, HouseTypeEnum.VILLA)
        val option = ManualCardOptionDTO(
            name = "Luxusvilla",
            cardType = CardTypeEnum.HOUSE,
            iconRes = R.drawable.ic_expand_more,
            searchableText = "luxusvilla",
            card = card
        )
        var clicked: ManualCardOptionDTO? = null
        val adapter = ManualCardSelectionAdapter { clicked = it }
        val recyclerView = RecyclerView(context).apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = adapter
        }
        adapter.submitList(listOf(option))
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
        layoutRecyclerView(recyclerView)

        val holder = recyclerView.findViewHolderForAdapterPosition(0) as ManualCardSelectionAdapter.ViewHolder
        assertEquals("Luxusvilla", holder.binding.txtCardName.text.toString())
        assertEquals(
            context.getString(R.string.points_category_house),
            holder.binding.txtCardType.text.toString()
        )

        holder.itemView.performClick()
        assertSame(option, clicked)
    }
}
