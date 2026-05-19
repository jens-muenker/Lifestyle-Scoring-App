package com.frosch2010.lifestyle_scoring_app.domain.model

import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum

/**
 * Single row in a category breakdown list: which card family contributed how many direct points.
 *
 * @property category Card family enum key used for stable ordering and localized labels in the UI.
 * @property points Points attributed to that category before indirect modifiers from other families.
 */
data class CategoryPointsEntry(
    val category: CardTypeEnum,
    val points: Int
)
