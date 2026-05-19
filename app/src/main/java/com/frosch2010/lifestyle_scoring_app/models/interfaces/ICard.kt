package com.frosch2010.lifestyle_scoring_app.models.interfaces

import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import java.io.Serializable

/**
 * Serializable polymorphic root for every physical card represented in scoring and UI adapters.
 *
 * @property cardType Top-level category discriminant routing instances through rule engines and list renderers.
 */
interface ICard : Serializable {
    val cardType: CardTypeEnum
}
