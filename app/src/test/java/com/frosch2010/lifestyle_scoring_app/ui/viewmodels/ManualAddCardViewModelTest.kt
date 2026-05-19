package com.frosch2010.lifestyle_scoring_app.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.CardsRepository
import com.frosch2010.lifestyle_scoring_app.domain.usecase.GetCardNameUseCase
import com.frosch2010.lifestyle_scoring_app.domain.usecase.GetCardsMapUseCase
import com.frosch2010.lifestyle_scoring_app.models.entities.AnimalCard
import com.frosch2010.lifestyle_scoring_app.models.entities.CarCard
import com.frosch2010.lifestyle_scoring_app.models.entities.HouseCard
import com.frosch2010.lifestyle_scoring_app.models.entities.JobCard
import com.frosch2010.lifestyle_scoring_app.models.entities.LoveCard
import com.frosch2010.lifestyle_scoring_app.models.entities.SportCard
import com.frosch2010.lifestyle_scoring_app.models.enums.AnimalTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.CarTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.HouseTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.JobTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.LoveTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.SportTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for [ManualAddCardViewModel] search filtering, empty-state handling, and love-card icon mapping.
 */
class ManualAddCardViewModelTest {

    /**
     * Runs Architecture Components executors synchronously so `LiveData` from [ManualAddCardViewModel] is delivered on the test thread.
     */
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    /**
     * Verifies that loading and searching produces filtered, sorted, and non-empty card options.
     */
    @Test
    fun load_andSearchQuery_updatesUiStateWithFilteredAndSortedOptions() {
        // Arrange
        val animal = AnimalCard(CardTypeEnum.ANIMAL, AnimalTypeEnum.DOG)
        val car = CarCard(CardTypeEnum.CAR, CarTypeEnum.SUV, 3)
        val love = LoveCard(CardTypeEnum.LOVE, LoveTypeEnum.CAR)
        val repo = FakeCardsRepository(
            mapOf(
                "zebra" to animal,
                "auto" to car,
                "liebe" to love
            ),
            mapOf(love to "Liebe Auto")
        )
        val vm = ManualAddCardViewModel(
            getCardsMapUseCase = GetCardsMapUseCase(repo),
            getCardNameUseCase = GetCardNameUseCase(repo)
        )

        // Act
        vm.load(excludedCardNames = setOf("zebra"))
        vm.onSearchQueryChanged("auto")

        // Assert
        val uiState = vm.uiState.value!!
        assertFalse(uiState.isEmptyStateVisible)
        assertEquals(2, uiState.visibleCards.size)
        assertTrue(uiState.visibleCards.any { it.name == "auto" })
        assertTrue(uiState.visibleCards.any { it.name == "Liebe Auto" })
    }

    /**
     * Verifies that each card category is exposed with its expected icon resource.
     */
    @Test
    fun load_setsExpectedIconsForCardTypes() {
        // Arrange
        val animal = AnimalCard(CardTypeEnum.ANIMAL, AnimalTypeEnum.DOG)
        val car = CarCard(CardTypeEnum.CAR, CarTypeEnum.SUV, 1)
        val house = HouseCard(CardTypeEnum.HOUSE, HouseTypeEnum.VILLA)
        val job = JobCard(CardTypeEnum.JOB, JobTypeEnum.MANAGER)
        val sport = SportCard(CardTypeEnum.SPORT, SportTypeEnum.RUNNING)
        val love = LoveCard(CardTypeEnum.LOVE, LoveTypeEnum.HOUSE)

        val repo = FakeCardsRepository(
            mapOf(
                "animal" to animal,
                "car" to car,
                "house" to house,
                "job" to job,
                "sport" to sport,
                "love" to love
            ),
            mapOf(love to "Love House")
        )
        val vm = ManualAddCardViewModel(
            getCardsMapUseCase = GetCardsMapUseCase(repo),
            getCardNameUseCase = GetCardNameUseCase(repo)
        )

        // Act
        vm.load(emptySet())
        val byType = vm.uiState.value!!.visibleCards.associateBy { it.cardType }

        // Assert
        assertEquals(R.drawable.ic_google_pets, byType[CardTypeEnum.ANIMAL]?.iconRes)
        assertEquals(R.drawable.ic_google_directions_car, byType[CardTypeEnum.CAR]?.iconRes)
        assertEquals(R.drawable.ic_google_home, byType[CardTypeEnum.HOUSE]?.iconRes)
        assertEquals(R.drawable.ic_google_work, byType[CardTypeEnum.JOB]?.iconRes)
        assertEquals(R.drawable.ic_google_fitness_center, byType[CardTypeEnum.SPORT]?.iconRes)
        assertEquals(R.drawable.ic_google_favorite, byType[CardTypeEnum.LOVE]?.iconRes)
    }

    /**
     * Fake cards repository that provides deterministic labels and card sets for view-model tests.
     */
    private class FakeCardsRepository(
        private val cards: Map<String, ICard>,
        private val names: Map<ICard, String>
    ) : CardsRepository {
        /**
         * Deterministic catalog map for manual-add search tests.
         *
         * @return configured [cards] map from the fake constructor
         */
        override fun getCardsMap(): Map<String, ICard> = cards

        /**
         * Resolves display names from the configured name map.
         *
         * @param card key used for lookup in [names]
         * @return configured title, or `"unknown"` when absent
         */
        override fun getCardName(card: ICard): String = names[card] ?: "unknown"

        /**
         * Returns a fixed ladder of car point options.
         *
         * @param card ignored by this fake
         * @return `[1, 2, 3]`
         */
        override fun getCarCardPoints(card: ICard): List<Int> = listOf(1, 2, 3)

        /**
         * Returns static love-type labels for UI mapping assertions.
         *
         * @return two placeholder labels
         */
        override fun getLoveCardTypes(): List<String> = listOf("Jobs", "House")

        /**
         * Returns a placeholder drawable id for any love card.
         *
         * @param loveCard ignored by this fake
         * @return `0` as a sentinel resource id
         */
        override fun getLoveIcon(loveCard: LoveCard): Int = 0

        /**
         * Exposes no bundled love rows for this fake.
         *
         * @return an empty list
         */
        override fun getLoveCardsWithIcon(): List<Pair<String, Int>> = emptyList()
    }
}

