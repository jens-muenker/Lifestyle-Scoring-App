package com.frosch2010.lifestyle_scoring_app.ui.viewmodels.dto

import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum

data class CardDTO(val cardType: CardTypeEnum, val name: String, val points: Int? = null)
