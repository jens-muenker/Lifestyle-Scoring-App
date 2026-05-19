package com.frosch2010.lifestyle_scoring_app.data.repository.interfaces

/**
 * Domain port for persisting the user's locale choice and applying it to the running process.
 *
 * Hides DataStore vs. legacy preferences so use cases stay Android-framework agnostic.
 */
interface LanguageRepository {
    /**
     * Reads the last successfully persisted BCP-47 language tag.
     *
     * @return Language tag string (for example `de-DE`); never null when defaults are seeded.
     */
    fun getPersistedLanguageTag(): String

    /**
     * Atomically stores a language tag and updates `Context` configuration for immediate UI refresh.
     *
     * @param tag Well-formed BCP-47 tag to persist and apply.
     */
    fun persistAndApplyLanguageTag(tag: String)
}
