package com.frosch2010.lifestyle_scoring_app.utils

import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [CardNameMatcher.cleanText] and fuzzy [CardNameMatcher.matchCleanedInput] / [CardNameMatcher.Match.isAcceptable].
 */
class CardNameMatcherTest {

    /**
     * [CardNameMatcher.cleanText] lowercases, applies the accent-stripping `String.normalize()` extension, then keeps letters only.
     */
    @Test
    fun cleanText_lowercases_removesAccents_andNonLetters() {
        val input = "  ÄÖÜ-ß 12!!  "
        val cleaned = CardNameMatcher.cleanText(input)
        // normalize() strips diacritics: ä->a, ö->o, ü->u; filter keeps only letters.
        assertEquals("aouß", cleaned)
    }

    /**
     * [CardNameMatcher.matchCleanedInput] picks the closest dictionary key and returns a strong fuzzy score for a near-typo.
     */
    @Test
    fun matchCleanedInput_returnsBestKeyAndCard() {
        val cards = mapOf(
            "kinder" to FakeCard(CardTypeEnum.LOVE),
            "auto" to FakeCard(CardTypeEnum.CAR),
            "haus" to FakeCard(CardTypeEnum.HOUSE)
        )

        val cleanedInput = "kindeer" // typo
        val match = CardNameMatcher.matchCleanedInput(cleanedInput, cards)

        assertEquals("kinder", match.matchingKey)
        assertNotNull(match.card)
        assertTrue(match.score > 75)
    }

    /**
     * [CardNameMatcher.Match.isAcceptable] requires a non-null card, score above 75, and length delta under 4 vs. the winning key.
     */
    @Test
    fun match_isAcceptable_requiresLengthClose_andScoreHigh_andCardNonNull() {
        val acceptable = CardNameMatcher.Match(
            card = FakeCard(CardTypeEnum.ANIMAL),
            score = 90,
            matchingKey = "kinder",
            cleanedInput = "kinder"
        )
        assertTrue(acceptable.isAcceptable())

        val tooLowScore = acceptable.copy(score = 75)
        assertFalse(tooLowScore.isAcceptable())

        val tooFarLength = acceptable.copy(cleanedInput = "kinderxxxx")
        assertFalse(tooFarLength.isAcceptable())

        val nullCard = acceptable.copy(card = null)
        assertFalse(nullCard.isAcceptable())
    }

    private data class FakeCard(
        override val cardType: CardTypeEnum
    ) : ICard
}

