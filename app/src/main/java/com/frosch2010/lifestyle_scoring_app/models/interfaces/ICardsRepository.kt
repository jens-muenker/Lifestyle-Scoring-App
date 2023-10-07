package com.frosch2010.lifestyle_scoring_app.models.interfaces

interface ICardsRepository {
    fun getCardsMap(): Map<String, ICard>
}