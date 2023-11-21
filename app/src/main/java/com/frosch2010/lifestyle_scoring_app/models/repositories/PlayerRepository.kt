package com.frosch2010.lifestyle_scoring_app.models.repositories

import com.frosch2010.lifestyle_scoring_app.models.entities.Player
import com.frosch2010.lifestyle_scoring_app.models.interfaces.IPlayerRepository
import javax.inject.Inject

/**
 * This class is used to manage the player data.
 * @author Jens Münker
 */
class PlayerRepository @Inject constructor(): IPlayerRepository {

    private val players: ArrayList<Player> = ArrayList()

    override fun addPlayer(player: Player) {
        players.add(player)
    }

    override fun getPlayers(): List<Player> {
        return players
    }

    override fun deletePlayer(index: Int) {
        players.removeAt(index)
    }

    override fun updatePlayer(index: Int, player: Player) {
        players[index] = player
    }

    override fun getPlayer(index: Int): Player {
        return players[index]
    }
}