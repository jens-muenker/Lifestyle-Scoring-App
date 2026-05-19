package com.frosch2010.lifestyle_scoring_app.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.frosch2010.lifestyle_scoring_app.domain.model.PlayerWithScore
import com.frosch2010.lifestyle_scoring_app.domain.usecase.AddPlayerUseCase
import com.frosch2010.lifestyle_scoring_app.domain.usecase.DeletePlayerUseCase
import com.frosch2010.lifestyle_scoring_app.domain.usecase.GetPlayerUseCase
import com.frosch2010.lifestyle_scoring_app.domain.usecase.GetPlayersWithScoreUseCase
import com.frosch2010.lifestyle_scoring_app.domain.usecase.UpdatePlayerUseCase
import com.frosch2010.lifestyle_scoring_app.models.entities.Player
import com.frosch2010.lifestyle_scoring_app.ui.viewmodels.common.Event
import com.frosch2010.lifestyle_scoring_app.ui.viewmodels.dto.PlayerDTO
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Orchestrates the pre-game roster: scores, CRUD, and one-shot prompts when the table is still empty.
 *
 * @param getPlayersWithScoreUseCase Projects repository players into sorted score rows for the leaderboard.
 * @param addPlayerUseCase Appends a new participant with an empty starting hand.
 * @param deletePlayerUseCase Removes a player by stable list index after confirmation in the UI.
 * @param getPlayerUseCase Reads the mutable entity graph for rename operations.
 * @param updatePlayerUseCase Writes back renamed players while preserving assigned cards.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val getPlayersWithScoreUseCase: GetPlayersWithScoreUseCase,
    private val addPlayerUseCase: AddPlayerUseCase,
    private val deletePlayerUseCase: DeletePlayerUseCase,
    private val getPlayerUseCase: GetPlayerUseCase,
    private val updatePlayerUseCase: UpdatePlayerUseCase
) : ViewModel() {

    /**
     * Immutable snapshot bound to the main RecyclerView and empty-state container.
     *
     * @property players Adapter models derived from the latest domain query.
     * @property showEmptyState `true` when the roster is empty and onboarding visuals should appear.
     */
    data class UiState(
        val players: List<PlayerDTO> = emptyList(),
        val showEmptyState: Boolean = true
    )

    /**
     * Ephemeral UI signals that must not replay after configuration changes.
     */
    sealed class UiEvent {
        /**
         * Fired once on cold start when no players exist so the activity can open the name dialog.
         */
        data object ShowInitialAddPlayerDialog : UiEvent()
    }

    /**
     * Guards against emitting [UiEvent.ShowInitialAddPlayerDialog] on every empty refresh.
     */
    private var hasEmittedInitialAddDialog = false

    /** Backing store for [uiState]. */
    private val _uiState = MutableLiveData(UiState())

    /** Observed immutable roster state for the main screen. */
    val uiState: LiveData<UiState> = _uiState

    /** Backing store for one-shot [events]. */
    private val _events = MutableLiveData<Event<UiEvent>>()

    /** Side-effect stream (dialogs) consumed with [Event.consume]. */
    val events: LiveData<Event<UiEvent>> = _events

    init {
        refreshPlayers(emitInitialDialog = true)
    }

    /**
     * Persists a freshly validated player and rebuilds observable roster rows.
     *
     * @param player UI row payload containing at minimum the display name.
     */
    fun addPlayer(player: PlayerDTO) {
        addPlayerUseCase(Player(player.name, arrayListOf()))
        refreshPlayers()
    }

    /**
     * Re-runs scoring after returning from downstream screens that mutate hands.
     */
    fun recalculatePlayerPoints() {
        refreshPlayers()
    }

    /**
     * Removes the participant at [playerIndex] and refreshes ordering-dependent UI state.
     *
     * @param playerIndex Roster index previously agreed with adapters and intent extras.
     */
    fun deletePlayer(playerIndex: Int) {
        deletePlayerUseCase(playerIndex)
        refreshPlayers()
    }

    /**
     * Renames a player while keeping their [com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard] list intact.
     *
     * @param playerIndex Target roster slot.
     * @param newName Trimmed name produced by validation layers in the dialog.
     */
    fun editPlayerName(playerIndex: Int, newName: String) {
        val playerEntity = getPlayerUseCase(playerIndex)
        updatePlayerUseCase(playerIndex, Player(newName, playerEntity.cards))
        refreshPlayers()
    }

    /**
     * Re-queries the domain roster, updates [UiState], and optionally emits the first-run empty-roster dialog.
     *
     * @param emitInitialDialog When `true` and the roster is still empty, emits [UiEvent.ShowInitialAddPlayerDialog] once.
     */
    private fun refreshPlayers(emitInitialDialog: Boolean = false) {
        val players = getPlayersWithScoreUseCase().toPlayerDtoList()
        _uiState.value = UiState(
            players = players,
            showEmptyState = players.isEmpty()
        )
        if (emitInitialDialog && players.isEmpty() && !hasEmittedInitialAddDialog) {
            hasEmittedInitialAddDialog = true
            _events.value = Event(UiEvent.ShowInitialAddPlayerDialog)
        }
    }

    /**
     * @return DTO list mirroring domain ordering for stable adapter diffing.
     */
    private fun List<PlayerWithScore>.toPlayerDtoList(): List<PlayerDTO> {
        return map { domainModel ->
            PlayerDTO(
                name = domainModel.name,
                score = domainModel.score
            )
        }
    }
}
