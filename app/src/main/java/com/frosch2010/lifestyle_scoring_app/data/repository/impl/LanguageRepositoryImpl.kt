package com.frosch2010.lifestyle_scoring_app.data.repository.impl

import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.LanguageRepository
import com.frosch2010.lifestyle_scoring_app.data.source.local.interfaces.LanguageLocalDataSource
import com.frosch2010.lifestyle_scoring_app.utils.AppLocale
import javax.inject.Inject

/**
 * Coordinates persistence with immediate process configuration updates for responsive locale switching.
 *
 * @param languageLocalDataSource Blocking store for the selected BCP-47 tag.
 */
class LanguageRepositoryImpl @Inject constructor(
    private val languageLocalDataSource: LanguageLocalDataSource
) : LanguageRepository {

    /**
     * @return Last persisted language tag, or empty when the user has never overridden locale.
     */
    override fun getPersistedLanguageTag(): String =
        languageLocalDataSource.getPersistedLanguageTag()

    /**
     * Persists [tag] then reapplies it through [AppLocale] so active activities reload resources.
     *
     * @param tag Language identifier written to DataStore and forwarded to configuration updates.
     */
    override fun persistAndApplyLanguageTag(tag: String) {
        languageLocalDataSource.persistLanguageTag(tag)
        AppLocale.applyLanguageTag(tag)
    }
}
