package com.frosch2010.lifestyle_scoring_app.models.entities

import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard

/**
 * Mutable in-session participant binding a display label to an ordered hand of [ICard] instances.
 *
 * @property name Editable player label shown across leaderboards; may be trimmed by validation layers.
 * @property cards Ordered collection reflecting acquisition sequence for UI diffing and scoring walks.
 */
data class Player(var name: String, val cards: ArrayList<ICard>)
