package com.frosch2010.lifestyle_scoring_app.models.entities

import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard

data class Player(var name: String, val cards: ArrayList<ICard>)
