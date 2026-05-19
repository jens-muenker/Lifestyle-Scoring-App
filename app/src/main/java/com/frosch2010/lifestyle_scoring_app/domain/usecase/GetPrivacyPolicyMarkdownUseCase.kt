package com.frosch2010.lifestyle_scoring_app.domain.usecase

import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.PrivacyPolicyRepository
import javax.inject.Inject

/**
 * Retrieves localized privacy policy prose for Markdown rendering components.
 *
 * @param privacyPolicyRepository Asset-backed provider of policy Markdown per locale.
 */
class GetPrivacyPolicyMarkdownUseCase @Inject constructor(
    private val privacyPolicyRepository: PrivacyPolicyRepository
) {
    /**
     * Loads Markdown text for the privacy screen, surfacing repository failures verbatim.
     *
     * @return [Result] containing Markdown on success, otherwise structured load/parse errors.
     */
    operator fun invoke(): Result<String> = privacyPolicyRepository.getMarkdown()
}
