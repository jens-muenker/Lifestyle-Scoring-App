package com.frosch2010.lifestyle_scoring_app.ui.activities

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.di.DataSourceModule
import com.frosch2010.lifestyle_scoring_app.di.RepositoryModule
import com.frosch2010.lifestyle_scoring_app.di.ServiceModule
import com.frosch2010.lifestyle_scoring_app.ui.adapters.ManualCardSelectionAdapter
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
import org.robolectric.shadows.ShadowLooper

/**
 * Robolectric-backed tests for [ManualAddCardActivity] (JVM, Android APIs via Robolectric).
 */
@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class, sdk = [36])
@UninstallModules(
    RepositoryModule::class,
    ServiceModule::class,
    DataSourceModule::class
)
class ManualAddCardActivityRobolectricTest {

    /**
     * Hilt Android rule that installs the test dependency graph for this class.
     */
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    /**
     * Verifies `onCreate_showsToolbarAndPopulatedCardList()` behavior.
     */
    @Test
    fun onCreate_showsToolbarAndPopulatedCardList() {
        hiltRule.inject()
        Robolectric.buildActivity(ManualAddCardActivity::class.java).setup().useActivity { activity ->
            assertNotNull(activity.findViewById(R.id.toolbar))
            val list = activity.findViewById<RecyclerView>(R.id.recManualCards)
            assertNotNull(list)
            assertTrue((list.adapter?.itemCount ?: 0) > 0)
        }
    }

    /**
     * Verifies `searchWithNoMatch_showsEmptyState()` behavior.
     */
    @Test
    fun searchWithNoMatch_showsEmptyState() {
        hiltRule.inject()
        Robolectric.buildActivity(ManualAddCardActivity::class.java).setup().useActivity { activity ->
            activity.findViewById<TextInputEditText>(R.id.editSearchCard).setText("__no_such_card_match_xyz__")
            ShadowLooper.runUiThreadTasksIncludingDelayedTasks()

            assertEquals(View.VISIBLE, activity.findViewById<View>(R.id.layoutEmptyState).visibility)
            assertEquals(View.GONE, activity.findViewById<RecyclerView>(R.id.recManualCards).visibility)
        }
    }

    /**
     * Verifies `clearSearch_restoresList()` behavior.
     */
    @Test
    fun clearSearch_restoresList() {
        hiltRule.inject()
        Robolectric.buildActivity(ManualAddCardActivity::class.java).setup().useActivity { activity ->
            activity.findViewById<TextInputEditText>(R.id.editSearchCard).setText("__nomatch__")
            ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
            activity.findViewById<View>(R.id.txtClearSearch).performClick()
            ShadowLooper.runUiThreadTasksIncludingDelayedTasks()

            assertEquals(View.GONE, activity.findViewById<View>(R.id.layoutEmptyState).visibility)
            val count = activity.findViewById<RecyclerView>(R.id.recManualCards).adapter?.itemCount ?: 0
            assertTrue(count > 0)
        }
    }

    /**
     * Verifies `existingCardNamesExtra_filtersOutMatchingCard()` behavior.
     */
    @Test
    fun existingCardNamesExtra_filtersOutMatchingCard() {
        hiltRule.inject()
        var excludedName = ""
        var fullCount = 0
        Robolectric.buildActivity(ManualAddCardActivity::class.java).setup().useActivity { base ->
            val adapter = base.findViewById<RecyclerView>(R.id.recManualCards).adapter as ManualCardSelectionAdapter
            excludedName = adapter.currentList.first().name
            fullCount = adapter.currentList.size
            base.finish()
        }

        val intent = Intent(
            ApplicationProvider.getApplicationContext(),
            ManualAddCardActivity::class.java
        ).apply {
            putStringArrayListExtra(
                ManualAddCardActivity.EXTRA_EXISTING_CARD_NAMES,
                arrayListOf(excludedName)
            )
        }
        Robolectric.buildActivity(ManualAddCardActivity::class.java, intent).setup().useActivity { filtered ->
            val filteredAdapter =
                filtered.findViewById<RecyclerView>(R.id.recManualCards).adapter as ManualCardSelectionAdapter
            assertEquals(fullCount - 1, filteredAdapter.currentList.size)
        }
    }

    /**
     * Verifies `backButton_finishesActivity()` behavior.
     */
    @Test
    fun backButton_finishesActivity() {
        hiltRule.inject()
        Robolectric.buildActivity(ManualAddCardActivity::class.java).setup().useActivity { activity ->
            activity.findViewById<View>(R.id.btnBack).performClick()
            assertTrue(activity.isFinishing || activity.isDestroyed)
        }
    }

    /**
     * Verifies `clickingListedCard_finishesWithResultOk()` behavior.
     */
    @Test
    fun clickingListedCard_finishesWithResultOk() {
        hiltRule.inject()
        Robolectric.buildActivity(ManualAddCardActivity::class.java).setup().useActivity { activity ->
            val rv = activity.findViewById<RecyclerView>(R.id.recManualCards)
            layoutRecyclerView(rv)
            val vh = rv.findViewHolderForAdapterPosition(0)
            assertNotNull(vh)
            vh!!.itemView.performClick()
            ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
            assertTrue(activity.isFinishing || activity.isDestroyed)
            assertEquals(Activity.RESULT_OK, shadowOf(activity).resultCode)
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
