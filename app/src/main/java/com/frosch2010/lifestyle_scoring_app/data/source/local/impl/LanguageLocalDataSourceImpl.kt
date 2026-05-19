package com.frosch2010.lifestyle_scoring_app.data.source.local.impl

import com.frosch2010.lifestyle_scoring_app.data.datastore.AppPreferencesDataStore
import com.frosch2010.lifestyle_scoring_app.data.source.local.interfaces.LanguageLocalDataSource
import javax.inject.Inject

/**
 * DataStore-backed [LanguageLocalDataSource] using blocking IO for legacy synchronous repository APIs.
 *
 * @param appPreferencesDataStore Facade around the app preferences DataStore graph.
 */
class LanguageLocalDataSourceImpl @Inject constructor(
    private val appPreferencesDataStore: AppPreferencesDataStore
) : LanguageLocalDataSource {
    /**
     * @return Current persisted language tag via synchronous DataStore read.
     */
    override fun getPersistedLanguageTag(): String = appPreferencesDataStore.getLanguageTagSync()

    /**
     * @param tag Language tag string persisted for later retrieval.
     */
    override fun persistLanguageTag(tag: String) {
        appPreferencesDataStore.setLanguageTagSync(tag)
    }
}
