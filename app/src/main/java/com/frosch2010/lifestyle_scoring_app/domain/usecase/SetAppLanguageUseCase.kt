package com.frosch2010.lifestyle_scoring_app.domain.usecase

import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.LanguageRepository
import javax.inject.Inject

/**
 * Command use case persisting a locale choice and forcing immediate configuration refresh.
 *
 * @param languageRepository Coordinates DataStore writes and `Context` locale application.
 */
class SetAppLanguageUseCase @Inject constructor(
    private val languageRepository: LanguageRepository
) {
    /**
     * Persists [tag] then reapplies it so visible activities pick up new resources without restart.
     *
     * @param tag BCP-47 language tag chosen from the settings language list.
     */
    operator fun invoke(tag: String) {
        languageRepository.persistAndApplyLanguageTag(tag)
    }
}
