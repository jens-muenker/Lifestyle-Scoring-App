package com.frosch2010.lifestyle_scoring_app.ui.viewmodels.dto

import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard

/**
 * Row model backing the searchable manual card picker: display metadata plus the domain card to commit.
 *
 * @property name Primary list label shown to the user.
 * @property cardType Category used for iconography and flow branching when a row is chosen.
 * @property iconRes Leading drawable resource id for the row.
 * @property searchableText Normalized substring material used by the filter predicate.
 * @property card Concrete [ICard] returned when this row is selected.
 */
data class ManualCardOptionDTO(
    val name: String,
    val cardType: CardTypeEnum,
    val iconRes: Int,
    val searchableText: String,
    val card: ICard
)
