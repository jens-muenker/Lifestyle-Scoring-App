package com.frosch2010.lifestyle_scoring_app.models.entities

import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.SportTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import java.io.Serializable

/**
 * Sport discipline card counted for trainer synergies while contributing zero direct pillar points.
 *
 * @property cardType Always [CardTypeEnum.SPORT].
 * @property sportType Subtype label driving localized strings and manager balance heuristics.
 */
data class SportCard(override val cardType: CardTypeEnum, val sportType: SportTypeEnum) : ICard, Serializable
