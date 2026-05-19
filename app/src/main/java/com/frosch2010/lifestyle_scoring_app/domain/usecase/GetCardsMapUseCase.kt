package com.frosch2010.lifestyle_scoring_app.domain.usecase

import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.CardsRepository
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import javax.inject.Inject

/**
 * Supplies the master selectable card map backing manual add and search experiences.
 *
 * @param cardsRepository Authoritative registry of all constructible [ICard] instances.
 */
class GetCardsMapUseCase @Inject constructor(
    private val cardsRepository: CardsRepository
) {
    /**
     * Returns the immutable label-to-card association table for picker UIs.
     *
     * @return Map whose keys are localized labels and values are stable card instances for persistence.
     */
    operator fun invoke(): Map<String, ICard> = cardsRepository.getCardsMap()
}
