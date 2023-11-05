package com.frosch2010.lifestyle_scoring_app.models.entities

import com.frosch2010.lifestyle_scoring_app.models.enums.AnimalTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import java.io.Serializable

/**
 * This class is used to define the animal card model.
 * @author Jens Münker
 */
data class AnimalCard(override val cardType: CardTypeEnum, val animalType: AnimalTypeEnum) : ICard, Serializable
