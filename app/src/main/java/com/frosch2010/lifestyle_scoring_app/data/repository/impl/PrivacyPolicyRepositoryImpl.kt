package com.frosch2010.lifestyle_scoring_app.data.repository.impl

import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.PrivacyPolicyRepository
import com.frosch2010.lifestyle_scoring_app.data.source.local.interfaces.PrivacyPolicyLocalDataSource
import javax.inject.Inject

/**
 * Loads Markdown policy text from raw resources via a focused local data source.
 *
 * @param privacyPolicyLocalDataSource Locale-aware reader selecting the correct `R.raw` asset.
 */
class PrivacyPolicyRepositoryImpl @Inject constructor(
    private val privacyPolicyLocalDataSource: PrivacyPolicyLocalDataSource
) : PrivacyPolicyRepository {

    /**
     * @return [Result.success] containing Markdown, or [Result.failure] when raw resources cannot be opened.
     */
    override fun getMarkdown(): Result<String> = runCatching {
        privacyPolicyLocalDataSource.getMarkdown()
    }
}
