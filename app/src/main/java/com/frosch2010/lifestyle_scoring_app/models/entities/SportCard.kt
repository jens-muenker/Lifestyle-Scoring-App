package com.frosch2010.lifestyle_scoring_app.models.entities

import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.SportTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import java.io.Serializable

/**
 * This class is used to define the sport card model.
 * @author Jens Münker
 */
data class SportCard(override val cardType: CardTypeEnum, val sportType: SportTypeEnum): ICard, Serializable
