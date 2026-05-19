package com.frosch2010.lifestyle_scoring_app.models.entities

import com.frosch2010.lifestyle_scoring_app.models.enums.AnimalTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import java.io.Serializable

/**
 * Concrete animal card identified solely by its species discriminator for set-scoring logic.
 *
 * @property cardType Always [CardTypeEnum.ANIMAL] for this value object.
 * @property animalType Species used when grouping duplicates for animal scoring passes.
 */
data class AnimalCard(override val cardType: CardTypeEnum, val animalType: AnimalTypeEnum) : ICard, Serializable
