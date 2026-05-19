package com.frosch2010.lifestyle_scoring_app.data.source.local.impl

import com.frosch2010.lifestyle_scoring_app.data.source.local.interfaces.PlayerLocalDataSource
import com.frosch2010.lifestyle_scoring_app.models.entities.Player
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Process-wide singleton backing player mutations for the active scoring session.
 *
 * The list mirrors UI ordering; concurrent mutation is not synchronized because gameplay is single-threaded UI driven.
 */
@Singleton
class InMemoryPlayerLocalDataSource @Inject constructor() : PlayerLocalDataSource {

    /**
     * Mutable ordered roster storing live [Player] graphs including card collections for the session.
     */
    private val players: ArrayList<Player> = ArrayList()

    /**
     * @param player Entity appended to [players] without duplicate checks.
     */
    override fun addPlayer(player: Player) {
        players.add(player)
    }

    /**
     * @return Direct reference to the backing list; callers must avoid structural edits outside this class.
     */
    override fun getPlayers(): List<Player> {
        return players
    }

    /**
     * @param index Valid roster index removed in place with list compaction.
     */
    override fun deletePlayer(index: Int) {
        players.removeAt(index)
    }

    /**
     * @param index Valid roster index whose element is overwritten.
     * @param player Replacement value stored at [index].
     */
    override fun updatePlayer(index: Int, player: Player) {
        players[index] = player
    }

    /**
     * @param index Roster slot to fetch.
     * @return Player currently held at [index].
     */
    override fun getPlayer(index: Int): Player {
        return players[index]
    }
}
