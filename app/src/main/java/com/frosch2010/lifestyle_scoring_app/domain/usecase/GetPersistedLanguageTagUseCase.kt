package com.frosch2010.lifestyle_scoring_app.domain.usecase

import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.LanguageRepository
import javax.inject.Inject

/**
 * Reads the last user-selected locale tag without mutating application configuration.
 *
 * @param languageRepository Persistence layer backing language changes across process restarts.
 */
class GetPersistedLanguageTagUseCase @Inject constructor(
    private val languageRepository: LanguageRepository
) {
    /**
     * Returns the stored BCP-47 tag used to bootstrap locale on cold start.
     *
     * @return Persisted language tag string; empty values should be handled as "system default" upstream.
     */
    operator fun invoke(): String = languageRepository.getPersistedLanguageTag()
}
