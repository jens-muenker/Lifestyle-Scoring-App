package com.frosch2010.lifestyle_scoring_app.models.entities

import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.JobTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import java.io.Serializable

/**
 * This class is used to define the job card model.
 * @author Jens Münker
 */
data class JobCard (override val cardType: CardTypeEnum, val jobType: JobTypeEnum): ICard, Serializable