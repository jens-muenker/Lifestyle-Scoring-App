package com.frosch2010.lifestyle_scoring_app.domain.usecase

import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.PlayerRepository
import com.frosch2010.lifestyle_scoring_app.models.entities.Player
import javax.inject.Inject

/**
 * Overwrites an existing roster entry after card mutations or rename operations.
 *
 * @param playerRepository Mutable ordered store shared across feature modules.
 */
class UpdatePlayerUseCase @Inject constructor(
    private val playerRepository: PlayerRepository
) {
    /**
     * Replaces the player at [index] with [player], keeping adapter positions stable.
     *
     * @param index Target roster slot previously identifying the player being edited.
     * @param player Fully updated entity graph including revised card list and metadata.
     */
    operator fun invoke(index: Int, player: Player) {
        playerRepository.updatePlayer(index, player)
    }
}
