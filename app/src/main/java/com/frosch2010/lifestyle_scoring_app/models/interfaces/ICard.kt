package com.frosch2010.lifestyle_scoring_app.models.interfaces

import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum

/**
 * This interface is used to access the card model.
 * @author Jens Münker
 */
interface ICard {
    val cardType: CardTypeEnum
}