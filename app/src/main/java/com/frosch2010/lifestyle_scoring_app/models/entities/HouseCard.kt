package com.frosch2010.lifestyle_scoring_app.models.entities

import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.HouseTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import java.io.Serializable

/**
 * House card keyed by architectural subtype for quadratic set scoring algorithms.
 *
 * @property cardType Always [CardTypeEnum.HOUSE].
 * @property houseType Subtype bucket used when grouping cards for iterative scoring rounds.
 */
data class HouseCard(override val cardType: CardTypeEnum, val houseType: HouseTypeEnum) : ICard, Serializable
