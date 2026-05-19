package com.frosch2010.lifestyle_scoring_app.data.repository.interfaces

import com.frosch2010.lifestyle_scoring_app.models.entities.Player

/**
 * Domain port for ephemeral in-memory player state for the active scoring session.
 *
 * Indices mirror RecyclerView ordering; callers must keep UI and repository indices aligned.
 */
interface PlayerRepository {
    /**
     * Appends a player to the tail of the active roster.
     *
     * @param player Fully initialized player entity including name and starting card list.
     */
    fun addPlayer(player: Player)

    /**
     * Returns the authoritative ordered player list for the current game.
     *
     * @return Snapshot list; mutating returned instances does not update the repository.
     */
    fun getPlayers(): List<Player>

    /**
     * Removes the player at the given roster index, shifting trailing entries down.
     *
     * @param index Zero-based position matching list adapters.
     */
    fun deletePlayer(index: Int)

    /**
     * Replaces the player at [index] with updated domain state (for example after card edits).
     *
     * @param index Roster position to overwrite.
     * @param player New player value including any mutated card collection.
     */
    fun updatePlayer(index: Int, player: Player)

    /**
     * Retrieves the player currently stored at [index].
     *
     * @param index Zero-based roster position.
     * @return Player entity at that index; callers must validate bounds before invoking.
     */
    fun getPlayer(index: Int): Player
}
