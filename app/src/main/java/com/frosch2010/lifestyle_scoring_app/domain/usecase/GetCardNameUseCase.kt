package com.frosch2010.lifestyle_scoring_app.domain.usecase

import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.CardsRepository
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import javax.inject.Inject

/**
 * Localized title resolver bridging card instances to human-readable list labels.
 *
 * @param cardsRepository Catalog owning string resources and mapping logic per card type.
 */
class GetCardNameUseCase @Inject constructor(
    private val cardsRepository: CardsRepository
) {
    /**
     * Produces the display title for [card] in the active locale context.
     *
     * @param card Domain card whose name should be resolved.
     * @return Localized non-empty title suitable for RecyclerView rows and dialog entries.
     */
    operator fun invoke(card: ICard): String = cardsRepository.getCardName(card)
}
