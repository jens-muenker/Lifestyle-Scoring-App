package com.frosch2010.lifestyle_scoring_app.models.interfaces

import com.frosch2010.lifestyle_scoring_app.models.entities.Player

/**
 * This interface is used for the player repository to manage the player data.
 * @author Jens Münker
 */
interface IPlayerRepository {

    /**
     * This method is used to add a player to the repository.
     * @param player The player to add.
     */
    fun addPlayer(player: Player)

    /**
     * This method is used to get all players from the repository.
     * @return The players.
     */
    fun getPlayers(): List<Player>

    /**
     * This method is used to remove a player from the repository.
     * @param index The index of the player to remove.
     */
    fun removePlayer(index: Int)

    /**
     * This method is used to update a player in the repository.
     * @param index The index of the player to update.
     * @param player The player to update.
     */
    fun updatePlayer(index: Int, player: Player)

    /**
     * This method is used to get a player from the repository.
     * @param index The index of the player to get.
     * @return The player.
     */
    fun getPlayer(index: Int): Player
}