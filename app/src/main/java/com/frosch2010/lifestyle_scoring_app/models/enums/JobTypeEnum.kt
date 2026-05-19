package com.frosch2010.lifestyle_scoring_app.models.enums

/**
 * Profession roles on job cards; each value unlocks different synergy math with other categories.
 */
enum class JobTypeEnum {
    /** Synergizes with animal stacks. */
    VET,

    /** Synergizes with house stacks. */
    REALESTATEAGENT,

    /** Synergizes with car stacks. */
    CARSALESMAN,

    /** Synergizes with sport stacks. */
    PERSONALTRAINER,

    /** Balanced-portfolio bonus variant (masculine label). */
    MANAGER,

    /** Balanced-portfolio bonus variant (feminine label). */
    MANAGERIN
}
