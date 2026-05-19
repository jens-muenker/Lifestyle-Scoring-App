package com.frosch2010.lifestyle_scoring_app.domain.usecase

import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.PlayerRepository
import com.frosch2010.lifestyle_scoring_app.models.entities.Player
import javax.inject.Inject

/**
 * Random-access reader for a single roster slot without exposing the entire player list.
 *
 * @param playerRepository Ordered in-memory player backing store.
 */
class GetPlayerUseCase @Inject constructor(
    private val playerRepository: PlayerRepository
) {
    /**
     * Fetches the player entity bound to [index] for detail screens and dialogs.
     *
     * @param index Zero-based roster index previously supplied by navigation extras or adapters.
     * @return Live [Player] snapshot; callers must validate bounds to avoid exceptions.
     */
    operator fun invoke(index: Int): Player = playerRepository.getPlayer(index)
}
