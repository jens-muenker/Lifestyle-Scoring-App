package com.frosch2010.lifestyle_scoring_app.ui.adapters

import android.view.ContextThemeWrapper
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import com.frosch2010.lifestyle_scoring_app.ui.viewmodels.PlayerCardsViewModel
import com.frosch2010.lifestyle_scoring_app.ui.viewmodels.dto.CardDTO
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper

/**
 * Robolectric-backed tests for [PlayerCardsAdapter] (UI-/Android APIs im JVM-Test).
 */

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class PlayerCardsAdapterTest {

    private fun layoutRecyclerView(recyclerView: RecyclerView) {
        recyclerView.measure(
            View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(4096, View.MeasureSpec.AT_MOST)
        )
        recyclerView.layout(0, 0, recyclerView.measuredWidth, recyclerView.measuredHeight)
    }

    /**
     * Verifies `bind_car_showsPointsChipAndPluralText` behavior.
     */
    @Test
    fun bind_car_showsPointsChipAndPluralText() {
        val context = ContextThemeWrapper(
            ApplicationProvider.getApplicationContext(),
            R.style.Theme_LifestyleScoringApp
        )
        val viewModel = mock<PlayerCardsViewModel>()
        val adapter = PlayerCardsAdapter(context, viewModel) { 0 }
        val recyclerView = RecyclerView(context).apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = adapter
        }
        adapter.updateData(
            listOf(
                CardDTO(
                    cardType = CardTypeEnum.CAR,
                    name = "Sportwagen",
                    points = 7
                )
            )
        )
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
        layoutRecyclerView(recyclerView)

        val holder = recyclerView.findViewHolderForAdapterPosition(0) as PlayerCardsAdapter.ViewHolder
        assertEquals(View.VISIBLE, holder.binding.cardPoints.visibility)
        val pointsText = context.resources.getQuantityString(R.plurals.points_count, 7, 7)
        assertEquals(pointsText, holder.binding.cardPoints.text.toString())
        assertEquals(
            context.getString(R.string.a11y_card_row_with_points, "Sportwagen", pointsText),
            holder.itemView.contentDescription
        )
    }

    /**
     * Verifies `bind_nonCar_hidesPointsChipAndUsesRowA11y` behavior.
     */
    @Test
    fun bind_nonCar_hidesPointsChipAndUsesRowA11y() {
        val context = ContextThemeWrapper(
            ApplicationProvider.getApplicationContext(),
            R.style.Theme_LifestyleScoringApp
        )
        val viewModel = mock<PlayerCardsViewModel>()
        val adapter = PlayerCardsAdapter(context, viewModel) { 0 }
        val recyclerView = RecyclerView(context).apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = adapter
        }
        adapter.updateData(
            listOf(
                CardDTO(
                    cardType = CardTypeEnum.HOUSE,
                    name = "Villa"
                )
            )
        )
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
        layoutRecyclerView(recyclerView)

        val holder = recyclerView.findViewHolderForAdapterPosition(0) as PlayerCardsAdapter.ViewHolder
        assertEquals(View.GONE, holder.binding.cardPoints.visibility)
        assertEquals(
            context.getString(R.string.a11y_card_row, "Villa"),
            holder.itemView.contentDescription
        )
        assertNotNull(holder.binding.cardIcon.drawable)
    }

    /**
     * Verifies `onDeleteConfirmed_forwardsCardIndexToViewModel` behavior.
     */
    @Test
    fun onDeleteConfirmed_forwardsCardIndexToViewModel() {
        val context = ContextThemeWrapper(
            ApplicationProvider.getApplicationContext(),
            R.style.Theme_LifestyleScoringApp
        )
        val viewModel = mock<PlayerCardsViewModel>()
        val adapter = PlayerCardsAdapter(context, viewModel) { 0 }

        adapter.onDeleteConfirmed(4)

        verify(viewModel).deleteCard(4)
    }
}
