package com.frosch2010.lifestyle_scoring_app.utils

import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import java.io.Serializable

data class ScanResult(
    val success: Boolean,
    val cards: ICard? = null,
): Serializable
