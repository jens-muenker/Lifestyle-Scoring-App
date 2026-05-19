package com.frosch2010.lifestyle_scoring_app.ui.util

import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for [CardCategoryPointsLabels].
 */
class CardCategoryPointsLabelsTest {

    /**
     * Verifies `pointsCategoryLabelResId_mapsAllCardTypes()` behavior.
     */
    @Test
    fun pointsCategoryLabelResId_mapsAllCardTypes() {
        assertEquals(R.string.points_category_animal, CardTypeEnum.ANIMAL.pointsCategoryLabelResId())
        assertEquals(R.string.points_category_car, CardTypeEnum.CAR.pointsCategoryLabelResId())
        assertEquals(R.string.points_category_house, CardTypeEnum.HOUSE.pointsCategoryLabelResId())
        assertEquals(R.string.points_category_job, CardTypeEnum.JOB.pointsCategoryLabelResId())
        assertEquals(R.string.points_category_sport, CardTypeEnum.SPORT.pointsCategoryLabelResId())
        assertEquals(R.string.points_category_love, CardTypeEnum.LOVE.pointsCategoryLabelResId())
    }

    /**
     * Verifies `pointsCategoryIconResId_mapsAllCardTypes()` behavior.
     */
    @Test
    fun pointsCategoryIconResId_mapsAllCardTypes() {
        assertEquals(R.drawable.ic_google_pets, CardTypeEnum.ANIMAL.pointsCategoryIconResId())
        assertEquals(R.drawable.ic_google_directions_car, CardTypeEnum.CAR.pointsCategoryIconResId())
        assertEquals(R.drawable.ic_google_home, CardTypeEnum.HOUSE.pointsCategoryIconResId())
        assertEquals(R.drawable.ic_google_work, CardTypeEnum.JOB.pointsCategoryIconResId())
        assertEquals(R.drawable.ic_google_fitness_center, CardTypeEnum.SPORT.pointsCategoryIconResId())
        assertEquals(R.drawable.ic_google_favorite, CardTypeEnum.LOVE.pointsCategoryIconResId())
    }

    /**
     * Verifies `pointsCategoryTintResId_mapsAllCardTypes()` behavior.
     */
    @Test
    fun pointsCategoryTintResId_mapsAllCardTypes() {
        assertEquals(R.color.card_animal, CardTypeEnum.ANIMAL.pointsCategoryTintResId())
        assertEquals(R.color.card_car, CardTypeEnum.CAR.pointsCategoryTintResId())
        assertEquals(R.color.card_house, CardTypeEnum.HOUSE.pointsCategoryTintResId())
        assertEquals(R.color.card_job, CardTypeEnum.JOB.pointsCategoryTintResId())
        assertEquals(R.color.card_sport, CardTypeEnum.SPORT.pointsCategoryTintResId())
        assertEquals(R.color.card_love, CardTypeEnum.LOVE.pointsCategoryTintResId())
    }
}

