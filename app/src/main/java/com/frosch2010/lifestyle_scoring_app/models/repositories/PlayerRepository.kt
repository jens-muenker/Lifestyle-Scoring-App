package com.frosch2010.lifestyle_scoring_app.models.repositories

import com.frosch2010.lifestyle_scoring_app.models.entities.Player
import com.frosch2010.lifestyle_scoring_app.models.interfaces.IPlayerRepository
import javax.inject.Inject

class PlayerRepository @Inject constructor(): IPlayerRepository {

    private val players: ArrayList<Player> = ArrayList()

    override fun addPlayer(player: Player) {
        players.add(player)
    }

    override fun getPlayers(): List<Player> {
        return players
    }

    override fun removePlayer(index: Int) {
        players.removeAt(index)
    }

    override fun updatePlayer(index: Int, player: Player) {
        players[index] = player
    }

    override fun getPlayer(index: Int): Player {
        return players[index]
    }
}