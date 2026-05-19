package com.frosch2010.lifestyle_scoring_app.models.entities

import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.LoveTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import java.io.Serializable

/**
 * Love multiplier card targeting a lifestyle pillar; subtype determines which category doubles.
 *
 * @property cardType Always [CardTypeEnum.LOVE].
 * @property loveType Axis selector for love synergies and iconography in manual flows.
 */
data class LoveCard(override val cardType: CardTypeEnum, val loveType: LoveTypeEnum) : ICard, Serializable
