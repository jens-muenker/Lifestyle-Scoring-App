package com.frosch2010.lifestyle_scoring_app.models.entities

import com.frosch2010.lifestyle_scoring_app.models.enums.CarTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard

data class CarCard(override val cardType: CardTypeEnum, val carType: CarTypeEnum, val points: Int): ICard
