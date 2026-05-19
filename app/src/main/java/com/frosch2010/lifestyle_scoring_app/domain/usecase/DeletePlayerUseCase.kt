package com.frosch2010.lifestyle_scoring_app.domain.usecase

import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.PlayerRepository
import javax.inject.Inject

/**
 * Removes a participant from the active roster by stable list index.
 *
 * @param playerRepository Mutable ordered backing store shared with UI adapters.
 */
class DeletePlayerUseCase @Inject constructor(
    private val playerRepository: PlayerRepository
) {
    /**
     * Deletes the roster entry at [index], compacting trailing players to preserve contiguous indices.
     *
     * @param index Zero-based position previously bound to RecyclerView items.
     */
    operator fun invoke(index: Int) {
        playerRepository.deletePlayer(index)
    }
}
