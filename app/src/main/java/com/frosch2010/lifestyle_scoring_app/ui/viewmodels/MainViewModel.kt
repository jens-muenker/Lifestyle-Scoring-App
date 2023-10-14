package com.frosch2010.lifestyle_scoring_app.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.frosch2010.lifestyle_scoring_app.models.entities.Player
import com.frosch2010.lifestyle_scoring_app.models.interfaces.IPlayerRepository
import com.frosch2010.lifestyle_scoring_app.ui.viewmodels.dto.PlayerDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val playerRepository: IPlayerRepository): ViewModel() {
    private val _players = MutableLiveData<List<PlayerDTO>>()
    val players: LiveData<List<PlayerDTO>> = _players

    init {
        // TODO: Calculate scores
        _players.value = playerRepository.getPlayers().map { PlayerDTO(it.name, 0) }
    }

    fun addPlayer(player: PlayerDTO) {
        val currentPlayers = _players.value?.toMutableList() ?: mutableListOf()
        currentPlayers.add(player)
        playerRepository.addPlayer(Player(player.name, arrayListOf()))
        _players.value = currentPlayers
    }

}