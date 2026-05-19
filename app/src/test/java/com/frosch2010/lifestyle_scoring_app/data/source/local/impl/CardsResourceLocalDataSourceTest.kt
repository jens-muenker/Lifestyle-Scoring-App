package com.frosch2010.lifestyle_scoring_app.data.source.local.impl

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.models.entities.AnimalCard
import com.frosch2010.lifestyle_scoring_app.models.entities.CarCard
import com.frosch2010.lifestyle_scoring_app.models.entities.LoveCard
import com.frosch2010.lifestyle_scoring_app.models.enums.AnimalTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.CarTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.LoveTypeEnum
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Robolectric tests for [CardsResourceLocalDataSource] (string/drawable-backed catalog).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CardsResourceLocalDataSourceTest {

    /**
     * Verifies `getCardsMap_containsAllManualCatalogEntries` behavior.
     */
    @Test
    fun getCardsMap_containsAllManualCatalogEntries() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dataSource = CardsResourceLocalDataSource(context)

        val map = dataSource.getCardsMap()

        assertEquals(28, map.size)
        assertTrue(map.containsValue(AnimalCard(CardTypeEnum.ANIMAL, AnimalTypeEnum.CAT)))
    }

    /**
     * Verifies `getCardName_returnsLocalizedTitle_forAnimalCat` behavior.
     */
    @Test
    fun getCardName_returnsLocalizedTitle_forAnimalCat() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dataSource = CardsResourceLocalDataSource(context)
        val card = AnimalCard(CardTypeEnum.ANIMAL, AnimalTypeEnum.CAT)

        assertEquals(context.getString(R.string.cat), dataSource.getCardName(card))
    }

    /**
     * Verifies `getCarCardPoints_returnsLadder_forMotorcycle` behavior.
     */
    @Test
    fun getCarCardPoints_returnsLadder_forMotorcycle() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dataSource = CardsResourceLocalDataSource(context)
        val card = CarCard(CardTypeEnum.CAR, CarTypeEnum.MOTORCYCLE, 1)

        assertEquals(listOf(1, 2, 3), dataSource.getCarCardPoints(card))
    }

    /**
     * Verifies `getLoveCardTypes_returnsFourEntries` behavior.
     */
    @Test
    fun getLoveCardTypes_returnsFourEntries() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dataSource = CardsResourceLocalDataSource(context)

        assertEquals(4, dataSource.getLoveCardTypes().size)
    }

    /**
     * Verifies `getLoveIcon_returnsDrawableId_forAnimalLove` behavior.
     */
    @Test
    fun getLoveIcon_returnsDrawableId_forAnimalLove() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dataSource = CardsResourceLocalDataSource(context)
        val love = LoveCard(CardTypeEnum.LOVE, LoveTypeEnum.ANIMAL)

        assertEquals(R.drawable.ic_animal, dataSource.getLoveIcon(love))
    }

    /**
     * Verifies `getLoveCardsWithIcon_returnsFourPairs` behavior.
     */
    @Test
    fun getLoveCardsWithIcon_returnsFourPairs() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dataSource = CardsResourceLocalDataSource(context)

        assertEquals(4, dataSource.getLoveCardsWithIcon().size)
    }
}
