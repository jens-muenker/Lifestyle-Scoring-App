package com.frosch2010.lifestyle_scoring_app.utils

import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import java.io.Serializable

/**
 * Serializable payload crossing activity boundaries when a scan or manual capture completes.
 *
 * @property success `true` when a confident card match was produced; `false` for failures or cancel paths.
 * @property cards Recognized card instance when successful; otherwise `null`.
 */
data class ScanResult(
    val success: Boolean,
    val cards: ICard? = null,
) : Serializable
