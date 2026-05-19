package com.frosch2010.lifestyle_scoring_app.domain.usecase

import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import javax.inject.Inject

/**
 * Stateless guard validating whether a resolved card may be appended to a player's hand.
 *
 * Encapsulates duplicate detection so presentation code stays declarative and unit-test friendly.
 */
class AddCardUseCase @Inject constructor() {

    /**
     * Evaluates add readiness by checking structural equality against cards already owned.
     *
     * @param card Candidate card produced by scan or manual selection flows.
     * @param existingCards Current immutable snapshot of the player's assigned cards.
     * @return [AddCardUseCaseResult.Ready] when unique, otherwise [AddCardUseCaseResult.Duplicate].
     */
    operator fun invoke(card: ICard, existingCards: List<ICard>): AddCardUseCaseResult {
        return if (existingCards.any { it == card }) {
            AddCardUseCaseResult.Duplicate
        } else {
            AddCardUseCaseResult.Ready
        }
    }
}
