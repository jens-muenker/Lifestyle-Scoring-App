package com.frosch2010.lifestyle_scoring_app.ui.viewmodels.dto

import android.graphics.drawable.Drawable
import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum

/**
 * This data class is used to pass the card data to the UI.
 * @author Jens Münker
 */
data class CardDTO(val cardType: CardTypeEnum, val name: String, val points: Int? = null, val icon: Int? = null)
