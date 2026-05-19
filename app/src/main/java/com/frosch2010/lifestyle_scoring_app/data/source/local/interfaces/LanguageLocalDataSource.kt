package com.frosch2010.lifestyle_scoring_app.data.source.local.interfaces

/**
 * Persistence port for the user-selected locale tag independent of UI application hooks.
 */
interface LanguageLocalDataSource {
    /**
     * @return Last stored BCP-47 tag, or empty when unset.
     */
    fun getPersistedLanguageTag(): String

    /**
     * Persists [tag] for subsequent cold starts without mutating runtime configuration itself.
     *
     * @param tag Language identifier written to durable storage.
     */
    fun persistLanguageTag(tag: String)
}
