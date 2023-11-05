package com.frosch2010.lifestyle_scoring_app.models.entities

import com.frosch2010.lifestyle_scoring_app.models.enums.CarTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import java.io.Serializable

/**
 * This class is used to define the car card model.
 * @author Jens Münker
 */
data class CarCard(override val cardType: CardTypeEnum, val carType: CarTypeEnum, val points: Int): ICard, Serializable
