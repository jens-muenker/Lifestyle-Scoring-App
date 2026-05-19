package com.frosch2010.lifestyle_scoring_app.domain.usecase

import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.CardsRepository
import javax.inject.Inject

/**
 * Provides enriched love-card rows pairing labels with drawable identifiers for icon list UIs.
 *
 * @param cardsRepository Registry that keeps label and icon mappings internally consistent.
 */
class GetLoveCardsWithIconUseCase @Inject constructor(
    private val cardsRepository: CardsRepository
) {
    /**
     * Builds adapter-ready tuples for every supported love subtype.
     *
     * @return Ordered `(label, drawableResId)` pairs mirroring repository-defined ordering.
     */
    operator fun invoke(): List<Pair<String, Int>> = cardsRepository.getLoveCardsWithIcon()
}
