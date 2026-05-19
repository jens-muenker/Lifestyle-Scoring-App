package com.frosch2010.lifestyle_scoring_app.domain.usecase

import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.PlayerRepository
import com.frosch2010.lifestyle_scoring_app.models.entities.Player
import javax.inject.Inject

/**
 * Application service mutating the in-memory roster when a new participant joins the session.
 *
 * @param playerRepository Authoritative store for ordered player entities during gameplay.
 */
class AddPlayerUseCase @Inject constructor(
    private val playerRepository: PlayerRepository
) {
    /**
     * Appends [player] to the repository tail, preserving existing indices for stable UI bindings.
     *
     * @param player Domain player including chosen display name and initial card list (possibly empty).
     */
    operator fun invoke(player: Player) {
        playerRepository.addPlayer(player)
    }
}
