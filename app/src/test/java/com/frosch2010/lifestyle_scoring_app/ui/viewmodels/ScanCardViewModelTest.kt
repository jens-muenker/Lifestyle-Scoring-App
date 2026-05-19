package com.frosch2010.lifestyle_scoring_app.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.CardsRepository
import com.frosch2010.lifestyle_scoring_app.domain.usecase.GetCarCardPointsUseCase
import com.frosch2010.lifestyle_scoring_app.domain.usecase.GetLoveCardTypesUseCase
import com.frosch2010.lifestyle_scoring_app.domain.usecase.GetLoveCardsWithIconUseCase
import com.frosch2010.lifestyle_scoring_app.models.entities.CarCard
import com.frosch2010.lifestyle_scoring_app.models.entities.LoveCard
import com.frosch2010.lifestyle_scoring_app.models.enums.CarTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.LoveTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import com.frosch2010.lifestyle_scoring_app.utils.ScanResult
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for [ScanCardViewModel] scan completion handling, love-card dialog branching, and related [LiveData] events.
 */
class ScanCardViewModelTest {

    /**
     * Runs Architecture Components executors synchronously so `LiveData` from [ScanCardViewModel] is delivered on the test thread.
     */
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    /**
     * Verifies that unsuccessful scans emit a delivery event with an empty card result.
     */
    @Test
    fun onScanCompleted_withoutCard_emitsNoCardRecognizedEvent() {
        // Arrange
        val viewModel = newVm()

        // Act
        viewModel.onScanCompleted(ScanResult(success = false, cards = null))

        // Assert
        val event = viewModel.events.value?.consume() as ScanCardViewModel.UiEvent.DeliverResult
        assertEquals(false, event.result.success)
        assertEquals(null, event.result.cards)
    }

    /**
     * Verifies that selecting a car card asks the UI to choose a point value.
     */
    @Test
    fun onCardSelected_withCar_emitsAskForCarPointsEvent() {
        // Arrange
        val viewModel = newVm()
        val card = CarCard(CardTypeEnum.CAR, CarTypeEnum.SUV, 0)

        // Act
        viewModel.onCardSelected(card, success = true)

        // Assert
        val event = viewModel.events.value?.consume() as ScanCardViewModel.UiEvent.AskForCarPoints
        assertEquals(listOf(1, 2, 3), event.choices)
        assertEquals(true, event.success)
    }

    /**
     * Verifies that a selected love label is mapped to the typed love card variant.
     */
    @Test
    fun onLoveTypeSelected_mapsLabelToTypedLoveCard() {
        // Arrange
        val viewModel = newVm()
        val rawLoveCard = LoveCard(CardTypeEnum.LOVE, LoveTypeEnum.UNKNOWN)

        // Act
        viewModel.onLoveTypeSelected(rawLoveCard, selectedLabel = "House", success = true)

        // Assert
        val event = viewModel.events.value?.consume() as ScanCardViewModel.UiEvent.DeliverResult
        val card = event.result.cards as LoveCard
        assertEquals(LoveTypeEnum.HOUSE, card.loveType)
    }

    /**
     * Creates the view model with fake card metadata providers.
     */
    private fun newVm(): ScanCardViewModel {
        val cardsRepository = FakeCardsRepository()
        return ScanCardViewModel(
            getCarCardPointsUseCase = GetCarCardPointsUseCase(cardsRepository),
            getLoveCardTypesUseCase = GetLoveCardTypesUseCase(cardsRepository),
            getLoveCardsWithIconUseCase = GetLoveCardsWithIconUseCase(cardsRepository)
        )
    }

    /**
     * Fake cards repository with deterministic mapping data for scan tests.
     */
    private class FakeCardsRepository : CardsRepository {
        /**
         * Returns an empty card map because this test suite bypasses OCR mapping.
         */
        override fun getCardsMap(): Map<String, ICard> = emptyMap()
        /**
         * Returns no label; not used in this test suite.
         */
        override fun getCardName(card: ICard): String = ""
        /**
         * Provides fixed car point options for dialog assertions.
         */
        override fun getCarCardPoints(card: ICard): List<Int> = listOf(1, 2, 3)
        /**
         * Provides fixed love-type labels used by mapping logic.
         */
        override fun getLoveCardTypes(): List<String> = listOf("Jobs", "House")
        /**
         * Returns a placeholder icon resource ID.
         */
        override fun getLoveIcon(loveCard: LoveCard): Int = 0
        /**
         * Returns deterministic love-label/icon pairs.
         */
        override fun getLoveCardsWithIcon(): List<Pair<String, Int>> = listOf("Jobs" to 1, "House" to 2)
    }
}
