package com.frosch2010.lifestyle_scoring_app.data.source.local.interfaces

import com.frosch2010.lifestyle_scoring_app.models.entities.Player

/**
 * Mutable ordered store for session-scoped player entities backing repositories.
 */
interface PlayerLocalDataSource {
    /**
     * @param player Player graph appended to the tail of the in-memory list.
     */
    fun addPlayer(player: Player)

    /**
     * @return Ordered players mirroring UI roster positions.
     */
    fun getPlayers(): List<Player>

    /**
     * @param index Zero-based index removed with subsequent compaction.
     */
    fun deletePlayer(index: Int)

    /**
     * @param index Slot whose value is replaced wholesale.
     * @param player Updated player snapshot including cards and metadata.
     */
    fun updatePlayer(index: Int, player: Player)

    /**
     * @param index Roster slot to read.
     * @return Player at [index]; out-of-range indices throw.
     */
    fun getPlayer(index: Int): Player
}
