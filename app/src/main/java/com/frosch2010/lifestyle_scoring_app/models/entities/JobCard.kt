package com.frosch2010.lifestyle_scoring_app.models.entities

import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.JobTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import java.io.Serializable

/**
 * Profession card whose subtype unlocks stack-based synergies with other lifestyle categories.
 *
 * @property cardType Always [CardTypeEnum.JOB].
 * @property jobType Role identifier compared structurally when awarding conditional bonuses.
 */
data class JobCard(override val cardType: CardTypeEnum, val jobType: JobTypeEnum) : ICard, Serializable
