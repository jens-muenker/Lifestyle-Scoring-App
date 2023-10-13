package com.frosch2010.lifestyle_scoring_app.models.entities

import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.HouseTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import java.io.Serializable

data class HouseCard(override val cardType: CardTypeEnum, val houseType: HouseTypeEnum) : ICard, Serializable