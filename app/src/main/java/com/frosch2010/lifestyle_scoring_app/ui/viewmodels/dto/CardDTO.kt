package com.frosch2010.lifestyle_scoring_app.ui.viewmodels.dto

import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum

/**
 * Read-only projection of a player's card for RecyclerView rows in the player cards screen.
 *
 * @property cardType Card family driving icons and swipe/delete affordances.
 * @property name Localized title shown as the row headline.
 * @property points Optional face value for car rows; `null` when the category does not surface numeric chips.
 * @property icon Optional drawable resource id for category-specific glyphs when not inferred from [cardType].
 */
data class CardDTO(
    val cardType: CardTypeEnum,
    val name: String,
    val points: Int? = null,
    val icon: Int? = null
)
