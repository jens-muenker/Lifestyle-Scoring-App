package com.frosch2010.lifestyle_scoring_app.domain.usecase

import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.CardsRepository
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import javax.inject.Inject

/**
 * Resolves allowed manual point increments for car cards based on repository-defined subtype tables.
 *
 * @param cardsRepository Provider of card metadata including car-specific scoring ladders.
 */
class GetCarCardPointsUseCase @Inject constructor(
    private val cardsRepository: CardsRepository
) {
    /**
     * Returns discrete selectable point values for [card] when it represents a car category instance.
     *
     * @param card Concrete card whose subtype governs the returned ladder.
     * @return Non-null list (possibly empty if mistyped) of valid point choices for UI pickers.
     */
    operator fun invoke(card: ICard): List<Int> = cardsRepository.getCarCardPoints(card)
}
