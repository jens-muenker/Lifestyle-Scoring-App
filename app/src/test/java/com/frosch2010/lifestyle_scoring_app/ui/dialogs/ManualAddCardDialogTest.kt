package com.frosch2010.lifestyle_scoring_app.ui.dialogs

import android.view.ContextThemeWrapper
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import androidx.appcompat.app.AlertDialog
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.models.entities.AnimalCard
import com.frosch2010.lifestyle_scoring_app.models.entities.CarCard
import com.frosch2010.lifestyle_scoring_app.models.enums.AnimalTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.CarTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowDialog

/**
 * Unit tests for [ManualAddCardDialog].
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ManualAddCardDialogTest {

    /**
     * Verifies `show_filtersListBySearch_andResolvesClickedCard()` behavior.
     */
    @Test
    fun show_filtersListBySearch_andResolvesClickedCard() {
        val context = ContextThemeWrapper(
            ApplicationProvider.getApplicationContext(),
            R.style.Theme_LifestyleScoringApp
        )
        val animal = AnimalCard(CardTypeEnum.ANIMAL, AnimalTypeEnum.DOG)
        val car = CarCard(CardTypeEnum.CAR, CarTypeEnum.SUV, 3)
        var resolved: ICard? = null

        val dialog = ManualAddCardDialog(
            context = context,
            cardMap = mapOf(
                "Hund" to animal,
                "Auto" to car
            ),
            onCardResolved = { resolved = it }
        )

        dialog.show()
        val alert = ShadowDialog.getLatestDialog() as AlertDialog
        val editSearch = alert.findViewById<EditText>(R.id.edit_manual_card_search)!!
        val listView = alert.findViewById<ListView>(R.id.list_manual_cards)!!
        val noResults = alert.findViewById<TextView>(R.id.text_manual_no_results)!!

        // Initial: both options visible
        assertEquals(2, listView.adapter.count)

        // Filter by "Auto"
        editSearch.setText("Auto")
        assertEquals(1, listView.adapter.count)
        assertTrue(listView.adapter.getItem(0).toString().contains("Auto"))
        assertEquals(android.view.View.GONE, noResults.visibility)

        listView.performItemClick(null, 0, listView.adapter.getItemId(0))
        assertEquals(car, resolved)
        assertTrue(!alert.isShowing)
    }

    /**
     * Verifies `show_whenNoFilterMatch_displaysEmptyView()` behavior.
     */
    @Test
    fun show_whenNoFilterMatch_displaysEmptyView() {
        val context = ContextThemeWrapper(
            ApplicationProvider.getApplicationContext(),
            R.style.Theme_LifestyleScoringApp
        )
        val animal = AnimalCard(CardTypeEnum.ANIMAL, AnimalTypeEnum.DOG)

        val dialog = ManualAddCardDialog(
            context = context,
            cardMap = mapOf("Hund" to animal),
            onCardResolved = { }
        )

        dialog.show()
        val alert = ShadowDialog.getLatestDialog() as AlertDialog
        val editSearch = alert.findViewById<EditText>(R.id.edit_manual_card_search)!!
        val listView = alert.findViewById<ListView>(R.id.list_manual_cards)!!
        val noResults = alert.findViewById<TextView>(R.id.text_manual_no_results)!!

        editSearch.setText("xyz_unbekannt")
        assertEquals(0, listView.adapter.count)
        // Android emptyView mechanism sets it visible when adapter is empty.
        assertEquals(android.view.View.VISIBLE, noResults.visibility)
    }
}

