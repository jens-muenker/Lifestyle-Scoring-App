package com.frosch2010.lifestyle_scoring_app.utils

import com.frosch2010.lifestyle_scoring_app.models.entities.HouseCard
import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.HouseTypeEnum
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Unit tests for the [ScanResult] scan pipeline payload (success flag and optional recognized card).
 */
class ScanResultTest {

    /**
     * Two [ScanResult] values with the same `success` and `cards` compare equal and share the same [hashCode].
     */
    @Test
    fun dataClass_equalityUsesSuccessAndCards() {
        val card = HouseCard(CardTypeEnum.HOUSE, HouseTypeEnum.VILLA)
        val a = ScanResult(success = true, cards = card)
        val b = ScanResult(success = true, cards = card)

        assertEquals(a, b)
        assertEquals(a.hashCode(), b.hashCode())
    }

    /**
     * Failure results default `cards` to `null` while exposing `success == false`.
     */
    @Test
    fun failurePath_defaultsCardsToNull() {
        val r = ScanResult(success = false)

        assertNull(r.cards)
        assertEquals(false, r.success)
    }
}
