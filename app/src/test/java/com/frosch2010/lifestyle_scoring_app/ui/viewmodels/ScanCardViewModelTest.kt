package com.frosch2010.lifestyle_scoring_app.ui.viewmodels

import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICardsRepository
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.verify

/**
 * This class tests the ScanCardViewModel class.
 * @autor Jens Münker
 */
@RunWith(MockitoJUnitRunner::class)
class ScanCardViewModelTest {

    @Mock
    lateinit var cardsRepository: ICardsRepository

    @InjectMocks
    lateinit var scanCardViewModel: ScanCardViewModel

    @Test
    fun testGetCardMap() {
        // Arrange
        `when`(cardsRepository.getCardsMap()).thenReturn(mapOf("card1" to mock(ICard::class.java)))

        // Act
        val cardMap = scanCardViewModel.getCardMap()

        // Assert
        assert(cardMap.isNotEmpty())
        verify(cardsRepository).getCardsMap()
    }

    @Test
    fun testShowDialogNoCardRecognizedWithNullCard() {
        // Arrange

        // Act
        val result = scanCardViewModel.showDialogNoCardRecognized(null)

        // Assert
        assert(result)
    }

    @Test
    fun testShowDialogNoCardRecognizedWithNonNullCard() {
        // Arrange
        val card = mock(ICard::class.java)

        // Act
        val result = scanCardViewModel.showDialogNoCardRecognized(card)

        // Assert
        assert(!result)
    }

    @Test
    fun testShouldAskForCarPointsWithCarCardType() {
        // Arrange
        val carCard = mock(ICard::class.java)
        `when`(carCard.cardType).thenReturn(CardTypeEnum.CAR)

        // Act
        val result = scanCardViewModel.shouldAskForCarPoints(carCard)

        // Assert
        assert(result)
    }

    @Test
    fun testShouldAskForCarPointsWithNonCarCardType() {
        // Arrange
        val nonCarCard = mock(ICard::class.java)
        `when`(nonCarCard.cardType).thenReturn(CardTypeEnum.LOVE)

        // Act
        val result = scanCardViewModel.shouldAskForCarPoints(nonCarCard)

        // Assert
        assert(!result)
    }

    @Test
    fun testGetCarCardPoints() {
        // Arrange
        val carCard = mock(ICard::class.java)
        `when`(cardsRepository.getCarCardPoints(carCard)).thenReturn(listOf(1, 2, 3))

        // Act
        val carCardPoints = scanCardViewModel.getCarCardPoints(carCard)

        // Assert
        assert(carCardPoints == listOf(1, 2, 3))
        verify(cardsRepository).getCarCardPoints(carCard)
    }

    @Test
    fun testShouldAskForLoveTypeWithLoveCardType() {
        // Arrange
        val loveCard = mock(ICard::class.java)
        `when`(loveCard.cardType).thenReturn(CardTypeEnum.LOVE)

        // Act
        val result = scanCardViewModel.shouldAskForLoveType(loveCard)

        // Assert
        assert(result)
    }

    @Test
    fun testShouldAskForLoveTypeWithNonLoveCardType() {
        // Arrange
        val nonLoveCard = mock(ICard::class.java)
        `when`(nonLoveCard.cardType).thenReturn(CardTypeEnum.CAR)

        // Act
        val result = scanCardViewModel.shouldAskForLoveType(nonLoveCard)

        // Assert
        assert(!result)
    }

    @Test
    fun testGetLoveCardTypes() {
        // Arrange
        `when`(cardsRepository.getLoveCardTypes()).thenReturn(listOf("Jobs", "House"))

        // Act
        val loveCardTypes = scanCardViewModel.getLoveCardTypes()

        // Assert
        assert(loveCardTypes == listOf("Jobs", "House"))
        verify(cardsRepository).getLoveCardTypes()
    }
}