package com.frosch2010.lifestyle_scoring_app.models.enums

/**
 * Top-level physical card families referenced by scoring, adapters, and resource mapping tables.
 */
enum class CardTypeEnum {
    /** Pets and wildlife cards contributing to animal set scoring. */
    ANIMAL,

    /** Vehicles with explicit printed point values and subtype ladders. */
    CAR,

    /** Real-estate archetypes feeding quadratic house scoring. */
    HOUSE,

    /** Profession cards unlocking cross-category synergies. */
    JOB,

    /** Discipline cards counted for trainer bonuses without direct pillar points. */
    SPORT,

    /** Multiplier cards targeting another lifestyle pillar via [LoveTypeEnum]. */
    LOVE
}
