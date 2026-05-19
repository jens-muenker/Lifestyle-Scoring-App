package com.frosch2010.lifestyle_scoring_app.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.PlayerRepository
import com.frosch2010.lifestyle_scoring_app.domain.usecase.AddPlayerUseCase
import com.frosch2010.lifestyle_scoring_app.domain.usecase.DeletePlayerUseCase
import com.frosch2010.lifestyle_scoring_app.domain.usecase.GetPlayerUseCase
import com.frosch2010.lifestyle_scoring_app.domain.usecase.GetPlayersWithScoreUseCase
import com.frosch2010.lifestyle_scoring_app.domain.usecase.UpdatePlayerUseCase
import com.frosch2010.lifestyle_scoring_app.models.entities.Player
import com.frosch2010.lifestyle_scoring_app.services.impl.PlayerPointsCalculationService
import com.frosch2010.lifestyle_scoring_app.ui.viewmodels.dto.PlayerDTO
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/**
 * Tests for [MainViewModel]: roster mutations and startup events (AAA, fakes + echte Use Cases).
 */
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    /**
     * Verifies `addPlayer_updatesRepositoryAndUiState` behavior.
     */
    @Test
    fun addPlayer_updatesRepositoryAndUiState() {
        val repository = FakePlayerRepository()
        val pointsService = PlayerPointsCalculationService()
        val getPlayersWithScoreUseCase = GetPlayersWithScoreUseCase(repository, pointsService)
        val addPlayerUseCase = AddPlayerUseCase(repository)
        val deletePlayerUseCase = DeletePlayerUseCase(repository)
        val getPlayerUseCase = GetPlayerUseCase(repository)
        val updatePlayerUseCase = UpdatePlayerUseCase(repository)
        val viewModel = MainViewModel(
            getPlayersWithScoreUseCase,
            addPlayerUseCase,
            deletePlayerUseCase,
            getPlayerUseCase,
            updatePlayerUseCase
        )

        viewModel.addPlayer(PlayerDTO("Alice", 0))

        assertEquals(1, repository.getPlayers().size)
        assertEquals("Alice", repository.getPlayer(0).name)
        assertEquals(1, viewModel.uiState.value?.players?.size)
        assertEquals("Alice", viewModel.uiState.value?.players?.first()?.name)
    }

    /**
     * Verifies `init_withEmptyPlayers_emitsInitialAddDialogEvent` behavior.
     */
    @Test
    fun init_withEmptyPlayers_emitsInitialAddDialogEvent() {
        val repository = FakePlayerRepository()
        val pointsService = PlayerPointsCalculationService()
        val viewModel = MainViewModel(
            getPlayersWithScoreUseCase = GetPlayersWithScoreUseCase(repository, pointsService),
            addPlayerUseCase = AddPlayerUseCase(repository),
            deletePlayerUseCase = DeletePlayerUseCase(repository),
            getPlayerUseCase = GetPlayerUseCase(repository),
            updatePlayerUseCase = UpdatePlayerUseCase(repository)
        )

        val event = viewModel.events.value?.consume()
        assertEquals(MainViewModel.UiEvent.ShowInitialAddPlayerDialog, event)
    }

    private class FakePlayerRepository : PlayerRepository {
        private val players = mutableListOf<Player>()

        override fun addPlayer(player: Player) {
            players.add(player)
        }

        override fun getPlayers(): List<Player> = players

        override fun deletePlayer(index: Int) {
            players.removeAt(index)
        }

        override fun updatePlayer(index: Int, player: Player) {
            players[index] = player
        }

        override fun getPlayer(index: Int): Player = players[index]
    }
}
