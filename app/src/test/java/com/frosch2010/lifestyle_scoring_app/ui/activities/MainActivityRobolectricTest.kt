package com.frosch2010.lifestyle_scoring_app.ui.activities

import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.di.DataSourceModule
import com.frosch2010.lifestyle_scoring_app.di.RepositoryModule
import com.frosch2010.lifestyle_scoring_app.di.ServiceModule
import com.frosch2010.lifestyle_scoring_app.ui.util.IntentExtras
import com.frosch2010.lifestyle_scoring_app.ui.util.useActivity
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.android.testing.UninstallModules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowDialog
import org.robolectric.shadows.ShadowLooper

/**
 * Robolectric-backed tests for [MainActivity] (JVM, Android APIs via Robolectric).
 */
@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class, sdk = [36])
@UninstallModules(
    RepositoryModule::class,
    ServiceModule::class,
    DataSourceModule::class
)
class MainActivityRobolectricTest {

    /**
     * Hilt Android rule that installs the test dependency graph for this class.
     */
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    /**
     * Verifies `onCreate_withNoPlayers_showsAddPlayerDialog_thenEmptyStateAfterDismiss()` behavior.
     */
    @Test
    fun onCreate_withNoPlayers_showsAddPlayerDialog_thenEmptyStateAfterDismiss() {
        hiltRule.inject()
        Robolectric.buildActivity(MainActivity::class.java).setup().useActivity { activity ->
            val dialog = ShadowDialog.getLatestDialog() as AlertDialog
            assertNotNull(dialog)
            assertTrue(dialog.isShowing)
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).performClick()

            assertEquals(View.VISIBLE, activity.findViewById<View>(R.id.emptyPlayersState).visibility)
            assertEquals(View.GONE, activity.findViewById<View>(R.id.fab_add_player).visibility)
        }
    }

    /**
     * Verifies `backButton_finishesActivity()` behavior.
     */
    @Test
    fun backButton_finishesActivity() {
        hiltRule.inject()
        Robolectric.buildActivity(MainActivity::class.java).setup().useActivity { activity ->
            (ShadowDialog.getLatestDialog() as AlertDialog).getButton(AlertDialog.BUTTON_NEGATIVE).performClick()

            activity.findViewById<View>(R.id.btnBack).performClick()
            assertTrue(activity.isFinishing || activity.isDestroyed)
        }
    }

    /**
     * Verifies `addPlayerWithValidName_showsListAndFab()` behavior.
     */
    @Test
    fun addPlayerWithValidName_showsListAndFab() {
        hiltRule.inject()
        Robolectric.buildActivity(MainActivity::class.java).setup().useActivity { activity ->
            val dialog = ShadowDialog.getLatestDialog() as AlertDialog
            dialog.findViewById<TextInputEditText>(R.id.edi_player_name)!!.setText("Alice")
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick()
            ShadowLooper.runUiThreadTasksIncludingDelayedTasks()

            assertEquals(View.GONE, activity.findViewById<View>(R.id.emptyPlayersState).visibility)
            assertEquals(View.VISIBLE, activity.findViewById<View>(R.id.fab_add_player).visibility)
            assertEquals(View.VISIBLE, activity.findViewById<View>(R.id.rec_view).visibility)
        }
    }

    /**
     * Verifies `addPlayerInline_opensSecondDialog()` behavior.
     */
    @Test
    fun addPlayerInline_opensSecondDialog() {
        hiltRule.inject()
        Robolectric.buildActivity(MainActivity::class.java).setup().useActivity { activity ->
            (ShadowDialog.getLatestDialog() as AlertDialog).getButton(AlertDialog.BUTTON_NEGATIVE).performClick()

            activity.findViewById<View>(R.id.btnAddPlayerInline).performClick()
            val second = ShadowDialog.getLatestDialog() as AlertDialog
            assertNotNull(second.findViewById<TextInputEditText>(R.id.edi_player_name))
        }
    }

    /**
     * Verifies `fabAddPlayer_opensNameDialog()` behavior.
     */
    @Test
    fun fabAddPlayer_opensNameDialog() {
        hiltRule.inject()
        Robolectric.buildActivity(MainActivity::class.java).setup().useActivity { activity ->
            val first = ShadowDialog.getLatestDialog() as AlertDialog
            first.findViewById<TextInputEditText>(R.id.edi_player_name)!!.setText("Bob")
            first.getButton(AlertDialog.BUTTON_POSITIVE).performClick()
            ShadowLooper.runUiThreadTasksIncludingDelayedTasks()

            activity.findViewById<View>(R.id.fab_add_player).performClick()
            val again = ShadowDialog.getLatestDialog() as AlertDialog
            assertNotNull(again.findViewById(R.id.edi_player_name))
        }
    }

    /**
     * Verifies `clickPlayerRow_startsPlayerCardsWithPlayerIndex()` behavior.
     */
    @Test
    fun clickPlayerRow_startsPlayerCardsWithPlayerIndex() {
        hiltRule.inject()
        Robolectric.buildActivity(MainActivity::class.java).setup().useActivity { activity ->
            val dialog = ShadowDialog.getLatestDialog() as AlertDialog
            dialog.findViewById<TextInputEditText>(R.id.edi_player_name)!!.setText("Carol")
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick()
            ShadowLooper.runUiThreadTasksIncludingDelayedTasks()

            val rv = activity.findViewById<RecyclerView>(R.id.rec_view)
            layoutRecyclerView(rv)
            val vh = rv.findViewHolderForAdapterPosition(0)
            assertNotNull(vh)
            vh!!.itemView.performClick()

            val next = shadowOf(activity).nextStartedActivity
            assertNotNull(next)
            assertEquals(PlayerCardsActivity::class.java.name, next.component!!.className)
            assertEquals(0, next.getIntExtra(IntentExtras.EXTRA_PLAYER_NUMBER, -1))
        }
    }

    private fun layoutRecyclerView(recyclerView: RecyclerView) {
        recyclerView.measure(
            View.MeasureSpec.makeMeasureSpec(1080, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(4096, View.MeasureSpec.AT_MOST)
        )
        recyclerView.layout(0, 0, recyclerView.measuredWidth, recyclerView.measuredHeight)
    }
}
