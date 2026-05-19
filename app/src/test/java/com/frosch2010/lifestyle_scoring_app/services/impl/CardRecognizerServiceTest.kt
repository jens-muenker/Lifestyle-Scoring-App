package com.frosch2010.lifestyle_scoring_app.services.impl

import com.frosch2010.lifestyle_scoring_app.models.entities.HouseCard
import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.HouseTypeEnum
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Unit tests for [matchCardFromOcrBlockLines], the label-to-card matcher also invoked from [CardRecognizerService.process].
 */
class CardRecognizerServiceTest {

    private val villa = HouseCard(CardTypeEnum.HOUSE, HouseTypeEnum.VILLA)
    private val beach = HouseCard(CardTypeEnum.HOUSE, HouseTypeEnum.BEACHHOUSE)

    /**
     * No OCR lines after cleaning yields no acceptable fuzzy hit, so the result is `null`.
     */
    @Test
    fun matchCardFromOcrBlockLines_emptyInput_returnsNull() {
        val cards = mapOf("Sommerhaus" to villa)

        val result = matchCardFromOcrBlockLines(emptySequence(), cards)

        assertNull(result)
    }

    /**
     * Fragments shorter than the minimum length filter are skipped, so nothing matches and the result is `null`.
     */
    @Test
    fun matchCardFromOcrBlockLines_onlyShortFragments_returnsNull() {
        val cards = mapOf("Sommerhaus" to villa)

        val result = matchCardFromOcrBlockLines(sequenceOf("ab", " x "), cards)

        assertNull(result)
    }

    /**
     * Text that does not fuzzy-match any catalog key within the acceptable threshold yields `null`.
     */
    @Test
    fun matchCardFromOcrBlockLines_noAcceptableFuzzyHit_returnsNull() {
        val cards = mapOf("Sommerhaus" to villa)

        val result = matchCardFromOcrBlockLines(sequenceOf("zzzzzzzz"), cards)

        assertNull(result)
    }

    /**
     * A single block whose cleaned text matches a dictionary key returns the associated [HouseCard].
     */
    @Test
    fun matchCardFromOcrBlockLines_singleStrongMatch_returnsCard() {
        val cards = mapOf("Sommerhaus" to villa)

        val result = matchCardFromOcrBlockLines(sequenceOf("  Sommerhaus  "), cards)

        assertEquals(villa, result)
    }

    /**
     * When several blocks match, the implementation keeps unique cards in encounter order and returns the first.
     */
    @Test
    fun matchCardFromOcrBlockLines_prefersFirstDistinctMatchInBlockOrder() {
        val cards = mapOf(
            "Ersteshaus" to villa,
            "Zweiteshaus" to beach
        )

        val result = matchCardFromOcrBlockLines(
            sequenceOf("Ersteshaus", "Zweiteshaus"),
            cards
        )

        assertEquals(villa, result)
    }

    /**
     * Repeated matches for the same catalog card collapse to one entry before taking the first, so the same [HouseCard] is returned once.
     */
    @Test
    fun matchCardFromOcrBlockLines_duplicateAcceptableHitsDedupesToOneCard() {
        val cards = mapOf("Sommerhaus" to villa)

        val result = matchCardFromOcrBlockLines(
            sequenceOf("Sommerhaus", "Sommerhaus"),
            cards
        )

        assertEquals(villa, result)
    }

    /**
     * A leading block with no acceptable match is ignored; the later matching block still resolves the card.
     */
    @Test
    fun matchCardFromOcrBlockLines_ignoresLeadingNoiseBlock() {
        val cards = mapOf("Sommerhaus" to villa)

        val result = matchCardFromOcrBlockLines(
            sequenceOf("zzzzzzzz", "Sommerhaus"),
            cards
        )

        assertEquals(villa, result)
    }
}
