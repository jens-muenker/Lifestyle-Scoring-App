package com.frosch2010.lifestyle_scoring_app.services.interfaces

import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard

/**
 * This interface is used to access the player points calculation service.
 * @author Jens Münker
 */
interface IPlayerPointsCalculationService {

    /**
     * This method is used to calculate the points of a player based on the cards he has.
     * @param cards The cards of the player.
     * @return The points of the player.
     */
    fun calculatePlayerPoints(cards: List<ICard>): Int
}