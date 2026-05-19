package com.frosch2010.lifestyle_scoring_app.data.repository.impl

import com.frosch2010.lifestyle_scoring_app.data.source.local.interfaces.CardsLocalDataSource
import com.frosch2010.lifestyle_scoring_app.models.entities.LoveCard
import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.LoveTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

/**
 * Unit tests for [CardsRepositoryImpl]: verifies pass-through delegation to [CardsLocalDataSource].
 */
class CardsRepositoryImplTest {

    /**
     * Verifies `getCardsMap_delegatesToDataSource` behavior.
     */
    @Test
    fun getCardsMap_delegatesToDataSource() {
        val fake = FakeCardsLocalDataSource()
        val map = mapOf("a" to LoveCard(CardTypeEnum.LOVE, LoveTypeEnum.UNKNOWN))
        fake.cardsMapReturn = map
        val repository = CardsRepositoryImpl(fake)

        assertEquals(map, repository.getCardsMap())
        assertEquals(1, fake.getCardsMapCalls)
    }

    /**
     * Verifies `getCardName_delegatesToDataSource` behavior.
     */
    @Test
    fun getCardName_delegatesToDataSource() {
        val fake = FakeCardsLocalDataSource()
        val card = LoveCard(CardTypeEnum.LOVE, LoveTypeEnum.UNKNOWN)
        fake.cardNameReturn = "Title"
        val repository = CardsRepositoryImpl(fake)

        assertEquals("Title", repository.getCardName(card))
        assertSame(card, fake.lastCardForName)
    }

    /**
     * Verifies `getCarCardPoints_delegatesToDataSource` behavior.
     */
    @Test
    fun getCarCardPoints_delegatesToDataSource() {
        val fake = FakeCardsLocalDataSource()
        val card = LoveCard(CardTypeEnum.LOVE, LoveTypeEnum.UNKNOWN)
        fake.carPointsReturn = listOf(1, 2, 3)
        val repository = CardsRepositoryImpl(fake)

        assertEquals(listOf(1, 2, 3), repository.getCarCardPoints(card))
        assertSame(card, fake.lastCardForCarPoints)
    }

    /**
     * Verifies `getLoveCardTypes_delegatesToDataSource` behavior.
     */
    @Test
    fun getLoveCardTypes_delegatesToDataSource() {
        val fake = FakeCardsLocalDataSource()
        fake.loveTypesReturn = listOf("A", "B")
        val repository = CardsRepositoryImpl(fake)

        assertEquals(listOf("A", "B"), repository.getLoveCardTypes())
        assertEquals(1, fake.getLoveCardTypesCalls)
    }

    /**
     * Verifies `getLoveIcon_delegatesToDataSource` behavior.
     */
    @Test
    fun getLoveIcon_delegatesToDataSource() {
        val fake = FakeCardsLocalDataSource()
        val love = LoveCard(CardTypeEnum.LOVE, LoveTypeEnum.ANIMAL)
        fake.loveIconReturn = 99
        val repository = CardsRepositoryImpl(fake)

        assertEquals(99, repository.getLoveIcon(love))
        assertSame(love, fake.lastLoveCardForIcon)
    }

    /**
     * Verifies `getLoveCardsWithIcon_delegatesToDataSource` behavior.
     */
    @Test
    fun getLoveCardsWithIcon_delegatesToDataSource() {
        val fake = FakeCardsLocalDataSource()
        val pairs = listOf("x" to 1)
        fake.lovePairsReturn = pairs
        val repository = CardsRepositoryImpl(fake)

        assertEquals(pairs, repository.getLoveCardsWithIcon())
        assertEquals(1, fake.getLoveCardsWithIconCalls)
    }

    private class FakeCardsLocalDataSource : CardsLocalDataSource {
        var cardsMapReturn: Map<String, ICard> = emptyMap()
        var getCardsMapCalls = 0
        var cardNameReturn = ""
        var lastCardForName: ICard? = null
        var carPointsReturn: List<Int> = emptyList()
        var lastCardForCarPoints: ICard? = null
        var loveTypesReturn: List<String> = emptyList()
        var getLoveCardTypesCalls = 0
        var loveIconReturn = 0
        var lastLoveCardForIcon: LoveCard? = null
        var lovePairsReturn: List<Pair<String, Int>> = emptyList()
        var getLoveCardsWithIconCalls = 0

        override fun getCardsMap(): Map<String, ICard> {
            getCardsMapCalls++
            return cardsMapReturn
        }

        override fun getCardName(card: ICard): String {
            lastCardForName = card
            return cardNameReturn
        }

        override fun getCarCardPoints(card: ICard): List<Int> {
            lastCardForCarPoints = card
            return carPointsReturn
        }

        override fun getLoveCardTypes(): List<String> {
            getLoveCardTypesCalls++
            return loveTypesReturn
        }

        override fun getLoveIcon(loveCard: LoveCard): Int {
            lastLoveCardForIcon = loveCard
            return loveIconReturn
        }

        override fun getLoveCardsWithIcon(): List<Pair<String, Int>> {
            getLoveCardsWithIconCalls++
            return lovePairsReturn
        }
    }
}
