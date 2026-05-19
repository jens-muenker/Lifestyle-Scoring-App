package com.frosch2010.lifestyle_scoring_app.ui.activities

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.PlayerRepository
import com.frosch2010.lifestyle_scoring_app.di.DataSourceModule
import com.frosch2010.lifestyle_scoring_app.di.RepositoryModule
import com.frosch2010.lifestyle_scoring_app.di.ServiceModule
import com.frosch2010.lifestyle_scoring_app.models.entities.AnimalCard
import com.frosch2010.lifestyle_scoring_app.models.entities.Player
import com.frosch2010.lifestyle_scoring_app.models.enums.AnimalTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import com.frosch2010.lifestyle_scoring_app.ui.util.IntentExtras
import com.frosch2010.lifestyle_scoring_app.ui.util.useActivity
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
import org.robolectric.shadows.ShadowLooper
import javax.inject.Inject

/**
 * Robolectric-backed tests for [PlayerCardsActivity] (JVM, Android APIs via Robolectric).
 */
@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class, sdk = [36])
@UninstallModules(
    RepositoryModule::class,
    ServiceModule::class,
    DataSourceModule::class
)
class PlayerCardsActivityRobolectricTest {

    /**
     * Hilt Android rule that installs the test dependency graph for this class.
     */
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    /**
     * In-memory player repository backing player-card navigation in these tests.
     */
    @Inject
    lateinit var playerRepository: PlayerRepository

    /**
     * Verifies `withSeededPlayer_showsEmptyCardsState_whenPlayerHasNoCards()` behavior.
     */
    @Test
    fun withSeededPlayer_showsEmptyCardsState_whenPlayerHasNoCards() {
        hiltRule.inject()
        playerRepository.addPlayer(Player("TestPlayer", arrayListOf()))

        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            PlayerCardsActivity::class.java
        ).apply {
            putExtra(IntentExtras.EXTRA_PLAYER_NUMBER, 0)
        }

        Robolectric.buildActivity(PlayerCardsActivity::class.java, intent).setup().useActivity { activity ->
            assertEquals(View.VISIBLE, activity.findViewById<View>(R.id.layoutEmptyCardsState).visibility)
            assertNotNull(activity.findViewById(R.id.btn_scan))
            assertNotNull(activity.findViewById(R.id.btn_manual_add))
        }
    }

    /**
     * Verifies `backButton_finishesActivity()` behavior.
     */
    @Test
    fun backButton_finishesActivity() {
        hiltRule.inject()
        playerRepository.addPlayer(Player("Bob", arrayListOf()))
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            PlayerCardsActivity::class.java
        ).apply { putExtra(IntentExtras.EXTRA_PLAYER_NUMBER, 0) }
        Robolectric.buildActivity(PlayerCardsActivity::class.java, intent).setup().useActivity { activity ->
            activity.findViewById<View>(R.id.btnBack).performClick()
            assertTrue(activity.isFinishing || activity.isDestroyed)
        }
    }

    /**
     * Verifies `btnScan_startsScanCardActivity()` behavior.
     */
    @Test
    fun btnScan_startsScanCardActivity() {
        hiltRule.inject()
        playerRepository.addPlayer(Player("ScanTest", arrayListOf()))
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            PlayerCardsActivity::class.java
        ).apply { putExtra(IntentExtras.EXTRA_PLAYER_NUMBER, 0) }
        Robolectric.buildActivity(PlayerCardsActivity::class.java, intent).setup().useActivity { activity ->
            activity.findViewById<View>(R.id.btn_scan).performClick()
            val next = shadowOf(activity).nextStartedActivity
            assertNotNull(next)
            assertEquals(ScanCardActivity::class.java.name, next.component!!.className)
        }
    }

    /**
     * Verifies `btnManualAdd_startsManualAddCardActivity()` behavior.
     */
    @Test
    fun btnManualAdd_startsManualAddCardActivity() {
        hiltRule.inject()
        playerRepository.addPlayer(Player("ManualTest", arrayListOf()))
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            PlayerCardsActivity::class.java
        ).apply { putExtra(IntentExtras.EXTRA_PLAYER_NUMBER, 0) }
        Robolectric.buildActivity(PlayerCardsActivity::class.java, intent).setup().useActivity { activity ->
            activity.findViewById<View>(R.id.btn_manual_add).performClick()
            val next = shadowOf(activity).nextStartedActivity
            assertNotNull(next)
            assertEquals(ManualAddCardActivity::class.java.name, next.component!!.className)
        }
    }

    /**
     * Verifies `withCard_showsRecycler_toggleBreakdown_expandsRows()` behavior.
     */
    @Test
    fun withCard_showsRecycler_toggleBreakdown_expandsRows() {
        hiltRule.inject()
        val cards = arrayListOf<ICard>(AnimalCard(CardTypeEnum.ANIMAL, AnimalTypeEnum.DOG))
        playerRepository.addPlayer(Player("WithCard", cards))
        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            PlayerCardsActivity::class.java
        ).apply { putExtra(IntentExtras.EXTRA_PLAYER_NUMBER, 0) }
        Robolectric.buildActivity(PlayerCardsActivity::class.java, intent).setup().useActivity { activity ->
            assertEquals(View.VISIBLE, activity.findViewById<View>(R.id.rec_view).visibility)
            val rv = activity.findViewById<RecyclerView>(R.id.rec_view)
            layoutRecyclerView(rv)
            val summaryHolder = rv.findViewHolderForAdapterPosition(0)
            assertNotNull(summaryHolder)
            val item = summaryHolder!!.itemView
            val scrollRows = item.findViewById<View>(R.id.scroll_points_rows)
            assertEquals(View.GONE, scrollRows.visibility)

            item.findViewById<View>(R.id.btn_toggle_breakdown).performClick()
            ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
            layoutRecyclerView(rv)
            val after = rv.findViewHolderForAdapterPosition(0)!!.itemView.findViewById<View>(R.id.scroll_points_rows)
            assertEquals(View.VISIBLE, after.visibility)
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
