package com.frosch2010.lifestyle_scoring_app.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.frosch2010.lifestyle_scoring_app.models.entities.AnimalCard
import com.frosch2010.lifestyle_scoring_app.models.entities.CarCard
import com.frosch2010.lifestyle_scoring_app.models.entities.HouseCard
import com.frosch2010.lifestyle_scoring_app.models.entities.JobCard
import com.frosch2010.lifestyle_scoring_app.models.entities.LoveCard
import com.frosch2010.lifestyle_scoring_app.models.entities.SportCard
import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICardsRepository
import com.frosch2010.lifestyle_scoring_app.ui.viewmodels.dto.CardDTO
import com.frosch2010.lifestyle_scoring_app.utils.ScanResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlayerCardsViewModel @Inject constructor(private val cardsRepository: ICardsRepository): ViewModel() {
    private val _playerCards = MutableLiveData<List<CardDTO>>()
    val playerCards: LiveData<List<CardDTO>> = _playerCards

    private fun addCard(card: ICard) {
        val currentCards = _playerCards.value?.toMutableList() ?: mutableListOf()
        when(card.cardType){
            CardTypeEnum.CAR -> currentCards.add(CardDTO(CardTypeEnum.CAR, (card as CarCard).carType.name, card.points))
            CardTypeEnum.ANIMAL -> currentCards.add(CardDTO(CardTypeEnum.ANIMAL, (card as AnimalCard).animalType.name))
            CardTypeEnum.SPORT -> currentCards.add(CardDTO(CardTypeEnum.SPORT, (card as SportCard).sportType.name))
            CardTypeEnum.HOUSE -> currentCards.add(CardDTO(CardTypeEnum.HOUSE, (card as HouseCard).houseType.name))
            CardTypeEnum.JOB -> currentCards.add(CardDTO(CardTypeEnum.JOB, (card as JobCard).jobType.name))
            CardTypeEnum.LOVE -> currentCards.add(CardDTO(CardTypeEnum.LOVE, (card as LoveCard).loveType.name))
        }
        _playerCards.value = currentCards
    }


    fun gotScanResult(result: ScanResult) {
        addCard(result.cards!!)
    }
}