package com.frosch2010.lifestyle_scoring_app.domain.usecase

import com.frosch2010.lifestyle_scoring_app.domain.model.PlayerWithScore
import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.PlayerRepository
import com.frosch2010.lifestyle_scoring_app.services.interfaces.IPlayerPointsCalculationService
import javax.inject.Inject

/**
 * Projects the full roster into lightweight score rows for leaderboards and summaries.
 *
 * @param playerRepository Source of player entities including mutable card collections.
 * @param pointsCalculationService Rule engine turning each hand into a single integer total.
 */
class GetPlayersWithScoreUseCase @Inject constructor(
    private val playerRepository: PlayerRepository,
    private val pointsCalculationService: IPlayerPointsCalculationService
) {
    /**
     * Maps every stored player to a [PlayerWithScore] using the shared scoring service.
     *
     * @return Ordered list aligned with repository player order for stable UI diffing.
     */
    operator fun invoke(): List<PlayerWithScore> {
        return playerRepository.getPlayers().map { player ->
            PlayerWithScore(
                name = player.name,
                score = pointsCalculationService.calculatePlayerPoints(player.cards)
            )
        }
    }
}
