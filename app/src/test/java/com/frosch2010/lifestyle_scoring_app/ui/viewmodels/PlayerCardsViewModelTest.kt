package com.frosch2010.lifestyle_scoring_app.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.CardsRepository
import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.PlayerRepository
import com.frosch2010.lifestyle_scoring_app.domain.model.CategoryPointsBreakdown
import com.frosch2010.lifestyle_scoring_app.domain.model.CategoryPointsEntry
import com.frosch2010.lifestyle_scoring_app.domain.usecase.AddCardUseCase
import com.frosch2010.lifestyle_scoring_app.domain.usecase.CalculateCategoryPointsBreakdownUseCase
import com.frosch2010.lifestyle_scoring_app.domain.usecase.DeletePlayerUseCase
import com.frosch2010.lifestyle_scoring_app.domain.usecase.GetCarCardPointsUseCase
import com.frosch2010.lifestyle_scoring_app.domain.usecase.GetCardNameUseCase
import com.frosch2010.lifestyle_scoring_app.domain.usecase.GetLoveCardTypesUseCase
import com.frosch2010.lifestyle_scoring_app.domain.usecase.GetLoveCardsWithIconUseCase
import com.frosch2010.lifestyle_scoring_app.domain.usecase.GetLoveIconUseCase
import com.frosch2010.lifestyle_scoring_app.domain.usecase.GetPlayerUseCase
import com.frosch2010.lifestyle_scoring_app.domain.usecase.UpdatePlayerUseCase
import com.frosch2010.lifestyle_scoring_app.models.entities.AnimalCard
import com.frosch2010.lifestyle_scoring_app.models.entities.CarCard
import com.frosch2010.lifestyle_scoring_app.models.entities.LoveCard
import com.frosch2010.lifestyle_scoring_app.models.entities.Player
import com.frosch2010.lifestyle_scoring_app.models.enums.AnimalTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.CarTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import com.frosch2010.lifestyle_scoring_app.services.interfaces.IPlayerPointsCalculationService
import com.frosch2010.lifestyle_scoring_app.utils.ScanResult
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for [PlayerCardsViewModel] scan results (duplicate vs new card), repository updates, and category breakdown refresh.
 */
class PlayerCardsViewModelTest {

    /**
     * Runs Architecture Components executors synchronously so `LiveData` from [PlayerCardsViewModel] is delivered on the test thread.
     */
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    /**
     * Verifies that duplicate scan results emit a user message and keep card count unchanged.
     */
    @Test
    fun gotScanResult_duplicateCard_emitsShowMessageEvent() {
        // Arrange
        val duplicateCard = AnimalCard(CardTypeEnum.ANIMAL, AnimalTypeEnum.CAT)
        val playerRepo = FakePlayerRepository(
            mutableListOf(Player("Bob", arrayListOf(duplicateCard)))
        )
        val vm = newVm(playerRepo, FakeCardsRepository(), FakePointsService())
        vm.loadCardsForPlayer(0)

        // Act
        vm.gotScanResult(ScanResult(success = true, cards = duplicateCard))

        // Assert
        val event = vm.events.value?.consume() as PlayerCardsViewModel.UiEvent.ShowMessage
        assertEquals(R.string.duplicate_card_message, event.resId)
        assertEquals(1, vm.uiState.value?.cards?.size)
    }

    /**
     * Verifies that a valid scan result is added, persisted, and reflected in points breakdown.
     */
    @Test
    fun gotScanResult_addsCard_andUpdatesRepository_andRefreshesBreakdown_whenReady() {
        // Arrange
        val playerRepo = FakePlayerRepository(mutableListOf(Player("Carl", arrayListOf())))
        val vm = newVm(playerRepo, FakeCardsRepository(), FakePointsService())
        vm.loadCardsForPlayer(0)

        // Act
        val newCard = CarCard(CardTypeEnum.CAR, CarTypeEnum.SUV, 5)
        vm.gotScanResult(ScanResult(success = true, cards = newCard))

        // Assert
        assertEquals(1, vm.uiState.value?.cards?.size)
        assertEquals(1, playerRepo.getPlayer(0).cards.size)
        assertEquals(5, vm.uiState.value?.categoryPointsBreakdown?.totalPoints)
    }

    /**
     * Constructs [PlayerCardsViewModel] with use cases backed by the supplied fakes ([PlayerRepository], [CardsRepository], [IPlayerPointsCalculationService]).
     */
    private fun newVm(
        playerRepo: PlayerRepository,
        cardsRepo: CardsRepository,
        pointsService: IPlayerPointsCalculationService
    ): PlayerCardsViewModel {
        return PlayerCardsViewModel(
            addCardUseCase = AddCardUseCase(),
            getCardNameUseCase = GetCardNameUseCase(cardsRepo),
            getCarCardPointsUseCase = GetCarCardPointsUseCase(cardsRepo),
            getLoveCardTypesUseCase = GetLoveCardTypesUseCase(cardsRepo),
            getLoveCardsWithIconUseCase = GetLoveCardsWithIconUseCase(cardsRepo),
            getLoveIconUseCase = GetLoveIconUseCase(cardsRepo),
            getPlayerUseCase = GetPlayerUseCase(playerRepo),
            updatePlayerUseCase = UpdatePlayerUseCase(playerRepo),
            deletePlayerUseCase = DeletePlayerUseCase(playerRepo),
            calculateCategoryPointsBreakdownUseCase = CalculateCategoryPointsBreakdownUseCase(pointsService)
        )
    }

    /**
     * In-memory fake player repository for deterministic state assertions.
     */
    private class FakePlayerRepository(
        private val players: MutableList<Player>
    ) : PlayerRepository {
        /**
         * Appends a player to the fake repository.
         */
        override fun addPlayer(player: Player) {
            players.add(player)
        }
        /**
         * Returns all stored players.
         */
        override fun getPlayers(): List<Player> = players
        /**
         * Removes the player at the requested index.
         */
        override fun deletePlayer(index: Int) {
            players.removeAt(index)
        }
        /**
         * Replaces the player at the requested index.
         */
        override fun updatePlayer(index: Int, player: Player) {
            players[index] = player
        }
        /**
         * Returns the player at the requested index.
         */
        override fun getPlayer(index: Int): Player = players[index]
    }

    /**
     * Fake cards repository with stable values required by view-model use cases.
     */
    private class FakeCardsRepository : CardsRepository {
        /**
         * Returns no card map entries because tests inject cards directly.
         */
        override fun getCardsMap(): Map<String, ICard> = emptyMap()
        /**
         * Returns deterministic labels by card category.
         */
        override fun getCardName(card: ICard): String = when (card.cardType) {
            CardTypeEnum.CAR -> "Car"
            CardTypeEnum.ANIMAL -> "Animal"
            CardTypeEnum.SPORT -> "Sport"
            CardTypeEnum.HOUSE -> "House"
            CardTypeEnum.JOB -> "Job"
            CardTypeEnum.LOVE -> "Love"
        }
        /**
         * Returns stable car-point choices for dialog requests.
         */
        override fun getCarCardPoints(card: ICard): List<Int> = listOf(1, 2, 3)
        /**
         * Returns fixed love-type labels for mapping assertions.
         */
        override fun getLoveCardTypes(): List<String> = listOf("Jobs", "House")
        /**
         * Returns a constant icon identifier used in tests.
         */
        override fun getLoveIcon(loveCard: LoveCard): Int = 42
        /**
         * Returns fixed love cards with icon IDs.
         */
        override fun getLoveCardsWithIcon(): List<Pair<String, Int>> = listOf("Jobs" to 1)
    }

    /**
     * Fake scoring service that scores only car points for predictable expectations.
     */
    private class FakePointsService : IPlayerPointsCalculationService {
        /**
         * Calculates total score as the sum of car-card point values.
         */
        override fun calculatePlayerPoints(cards: List<ICard>): Int = cards.sumOf { (it as? CarCard)?.points ?: 0 }
        /**
         * Returns a single-category breakdown mirroring the calculated total.
         */
        override fun calculateCategoryPointsBreakdown(cards: List<ICard>): CategoryPointsBreakdown {
            val total = calculatePlayerPoints(cards)
            return CategoryPointsBreakdown(totalPoints = total, entries = listOf(CategoryPointsEntry(CardTypeEnum.CAR, total)))
        }
    }
}

