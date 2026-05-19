package com.frosch2010.lifestyle_scoring_app.ui.adapters

import android.view.ContextThemeWrapper
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.ui.viewmodels.MainViewModel
import com.frosch2010.lifestyle_scoring_app.ui.viewmodels.dto.PlayerDTO
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper

/**
 * Robolectric-backed tests for [PlayerAdapter] (UI-/Android APIs im JVM-Test).
 */

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class PlayerAdapterTest {

    private fun layoutRecyclerView(recyclerView: RecyclerView) {
        recyclerView.measure(
            View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(4096, View.MeasureSpec.AT_MOST)
        )
        recyclerView.layout(0, 0, recyclerView.measuredWidth, recyclerView.measuredHeight)
    }

    /**
     * Verifies `bind_setsNamePointsAndA11y_andClickNotifiesListener` behavior.
     */
    @Test
    fun bind_setsNamePointsAndA11y_andClickNotifiesListener() {
        val context = ContextThemeWrapper(
            ApplicationProvider.getApplicationContext(),
            R.style.Theme_LifestyleScoringApp
        )
        val viewModel = mock<MainViewModel>()
        val listener = mock<PlayerAdapter.OnPlayerClickedListener>()
        val adapter = PlayerAdapter(listener, context, viewModel)
        val recyclerView = RecyclerView(context).apply {
            layoutManager = LinearLayoutManager(context)
            this.adapter = adapter
        }
        adapter.updateData(listOf(PlayerDTO("Alex", 42)))
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
        layoutRecyclerView(recyclerView)

        val holder = recyclerView.findViewHolderForAdapterPosition(0) as PlayerAdapter.ViewHolder
        assertEquals("Alex", holder.binding.playerName.text.toString())
        val expectedPoints = context.resources.getQuantityString(R.plurals.player_points, 42, 42)
        assertEquals(expectedPoints, holder.binding.playerPoints.text.toString())
        assertEquals(
            context.getString(R.string.a11y_player_row, "Alex", expectedPoints),
            holder.itemView.contentDescription
        )

        holder.itemView.performClick()
        verify(listener).onPlayerClicked(0)
    }

    /**
     * Verifies `onDeleteConfirmed_forwardsIndexToViewModel` behavior.
     */
    @Test
    fun onDeleteConfirmed_forwardsIndexToViewModel() {
        val context = ContextThemeWrapper(
            ApplicationProvider.getApplicationContext(),
            R.style.Theme_LifestyleScoringApp
        )
        val viewModel = mock<MainViewModel>()
        val adapter = PlayerAdapter(
            mock<PlayerAdapter.OnPlayerClickedListener>(),
            context,
            viewModel
        )

        adapter.onDeleteConfirmed(3)

        verify(viewModel).deletePlayer(3)
    }
}
