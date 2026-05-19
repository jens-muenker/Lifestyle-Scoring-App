package com.frosch2010.lifestyle_scoring_app.domain.usecase

import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.CardsRepository
import com.frosch2010.lifestyle_scoring_app.models.entities.LoveCard
import javax.inject.Inject

/**
 * Resolves preview imagery for a concrete love card based on its subtype state.
 *
 * @param cardsRepository Authority mapping love subtypes to `R.drawable` identifiers.
 */
class GetLoveIconUseCase @Inject constructor(
    private val cardsRepository: CardsRepository
) {
    /**
     * Looks up the drawable resource for [card]'s current specialization.
     *
     * @param card Typed love card carrying subtype discriminator fields.
     * @return Android drawable resource id suitable for `ImageView.setImageResource`.
     */
    operator fun invoke(card: LoveCard): Int = cardsRepository.getLoveIcon(card)
}
