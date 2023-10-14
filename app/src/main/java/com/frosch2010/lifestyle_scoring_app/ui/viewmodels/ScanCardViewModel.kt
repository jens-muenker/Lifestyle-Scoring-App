package com.frosch2010.lifestyle_scoring_app.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.frosch2010.lifestyle_scoring_app.models.entities.CarCard
import com.frosch2010.lifestyle_scoring_app.models.enums.CarTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICardsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ScanCardViewModel @Inject constructor(private val cardsRepository: ICardsRepository): ViewModel() {
    fun getCardMap(): Map<String, ICard> {
        return cardsRepository.getCardsMap()
    }

    fun showDialogNoCardRecognized(cards: ICard?): Boolean {
        return cards == null
    }

    fun shouldAskForCarPoints(card: ICard): Boolean {
        return card.cardType == CardTypeEnum.CAR
    }

    fun getCarCardPoints(card: ICard): List<Int> {
        return cardsRepository.getCarCardPoints(card)
    }

    fun shouldAskForLoveType(card: ICard): Boolean {
        return card.cardType == CardTypeEnum.LOVE
    }

    fun getLoveCardTypes(): List<String> {
        return cardsRepository.getLoveCardTypes()
    }
}