package com.frosch2010.lifestyle_scoring_app.models.interfaces

import com.frosch2010.lifestyle_scoring_app.models.entities.Player

interface IPlayerRepository {
    fun addPlayer(player: Player)
    fun getPlayers(): List<Player>
    fun removePlayer(index: Int)
    fun updatePlayer(index: Int, player: Player)
    fun getPlayer(index: Int): Player
}