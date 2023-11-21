package com.frosch2010.lifestyle_scoring_app.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.frosch2010.lifestyle_scoring_app.models.entities.Player
import com.frosch2010.lifestyle_scoring_app.models.interfaces.IPlayerRepository
import com.frosch2010.lifestyle_scoring_app.services.interfaces.IPlayerPointsCalculationService
import com.frosch2010.lifestyle_scoring_app.ui.viewmodels.dto.PlayerDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * This class is used to manage the data for the MainActivity.
 * @author Jens Münker
 */
@HiltViewModel
class MainViewModel @Inject constructor(private val playerRepository: IPlayerRepository, private val scoreCalculationService: IPlayerPointsCalculationService): ViewModel() {
    private val _players = MutableLiveData<List<PlayerDTO>>()
    val players: LiveData<List<PlayerDTO>> = _players

    init {
        _players.value = playerRepository.getPlayers().map { PlayerDTO(it.name, scoreCalculationService.calculatePlayerPoints(it.cards)) }
    }

    fun addPlayer(player: PlayerDTO) {
        val currentPlayers = _players.value?.toMutableList() ?: mutableListOf()
        currentPlayers.add(player)
        playerRepository.addPlayer(Player(player.name, arrayListOf()))
        _players.value = currentPlayers
    }

    fun recalculatePlayerPoints() {
        _players.value = playerRepository.getPlayers().map { PlayerDTO(it.name, scoreCalculationService.calculatePlayerPoints(it.cards)) }
    }

    fun deletePlayer(playerIndex: Int) {
        val currentPlayers = _players.value?.toMutableList() ?: mutableListOf()
        currentPlayers.removeAt(playerIndex)
        playerRepository.deletePlayer(playerIndex)
        _players.value = currentPlayers
    }
}