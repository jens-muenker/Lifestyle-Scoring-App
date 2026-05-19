package com.frosch2010.lifestyle_scoring_app.domain.usecase

import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.CardsRepository
import javax.inject.Inject

/**
 * Exposes localized love-card subtype labels for selection dialogs.
 *
 * @param cardsRepository Source of love-card taxonomy strings aligned with drawable resources.
 */
class GetLoveCardTypesUseCase @Inject constructor(
    private val cardsRepository: CardsRepository
) {
    /**
     * Returns every selectable love specialization name in presentation order.
     *
     * @return Immutable list of localized subtype labels.
     */
    operator fun invoke(): List<String> = cardsRepository.getLoveCardTypes()
}
