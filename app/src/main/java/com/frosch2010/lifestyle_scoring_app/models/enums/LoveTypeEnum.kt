package com.frosch2010.lifestyle_scoring_app.models.enums

/**
 * Love card specialization determining which pillar receives the love multiplier.
 */
enum class LoveTypeEnum {
    /** Placeholder before the user picks a specialization in manual flows. */
    UNKNOWN,

    /** Targets animal pillar scoring. */
    ANIMAL,

    /** Targets house pillar scoring. */
    HOUSE,

    /** Targets job pillar scoring. */
    JOB,

    /** Targets car pillar scoring. */
    CAR
}
