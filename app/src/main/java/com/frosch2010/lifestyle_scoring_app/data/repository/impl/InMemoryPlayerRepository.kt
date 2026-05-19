package com.frosch2010.lifestyle_scoring_app.data.repository.impl

import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.PlayerRepository
import com.frosch2010.lifestyle_scoring_app.data.source.local.interfaces.PlayerLocalDataSource
import com.frosch2010.lifestyle_scoring_app.models.entities.Player
import javax.inject.Inject

/**
 * [PlayerRepository] backed exclusively by an in-memory [PlayerLocalDataSource] for session scope.
 *
 * @param playerLocalDataSource Mutable ordered store shared with other repositories in the graph.
 */
open class InMemoryPlayerRepository @Inject constructor(
    private val playerLocalDataSource: PlayerLocalDataSource
) : PlayerRepository {

    /**
     * @param player Entity to append at the roster tail.
     */
    override fun addPlayer(player: Player) {
        playerLocalDataSource.addPlayer(player)
    }

    /**
     * @return Live-backed list snapshot; treat as read-only unless intentionally mutating for tests.
     */
    override fun getPlayers(): List<Player> {
        return playerLocalDataSource.getPlayers()
    }

    /**
     * @param index Adapter-aligned roster slot to remove.
     */
    override fun deletePlayer(index: Int) {
        playerLocalDataSource.deletePlayer(index)
    }

    /**
     * @param index Roster slot to overwrite.
     * @param player Replacement graph including updated cards or renamed fields.
     */
    override fun updatePlayer(index: Int, player: Player) {
        playerLocalDataSource.updatePlayer(index, player)
    }

    /**
     * @param index Zero-based roster position.
     * @return Player currently stored at [index]; may throw if out of bounds.
     */
    override fun getPlayer(index: Int): Player {
        return playerLocalDataSource.getPlayer(index)
    }
}
