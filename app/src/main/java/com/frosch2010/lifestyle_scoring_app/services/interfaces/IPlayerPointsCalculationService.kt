package com.frosch2010.lifestyle_scoring_app.services.interfaces

import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard

interface IPlayerPointsCalculationService {
    fun calculatePlayerPoints(cards: List<ICard>): Int
}