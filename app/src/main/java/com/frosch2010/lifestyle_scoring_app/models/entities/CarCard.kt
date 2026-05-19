package com.frosch2010.lifestyle_scoring_app.models.entities

import com.frosch2010.lifestyle_scoring_app.models.enums.CarTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import java.io.Serializable

/**
 * Car card carrying both subtype metadata and the printed point value selected during manual entry.
 *
 * @property cardType Always [CardTypeEnum.CAR].
 * @property carType Subtype influencing allowed manual point ladders in the data layer.
 * @property points Face value contributing directly to car pillar scoring before love multipliers.
 */
data class CarCard(override val cardType: CardTypeEnum, val carType: CarTypeEnum, val points: Int) : ICard, Serializable
