package com.frosch2010.lifestyle_scoring_app.models.entities

import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.LoveTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import java.io.Serializable

data class LoveCard(override val cardType: CardTypeEnum, val loveType: LoveTypeEnum) : ICard, Serializable
