package com.frosch2010.lifestyle_scoring_app.models.entities

import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard

/**
 * This class is used to define the player model.
 * @author Jens Münker
 */
data class Player(var name: String, val cards: ArrayList<ICard>)
