package com.frosch2010.lifestyle_scoring_app.data.repository.interfaces

/**
 * Domain port for localized privacy policy Markdown consumed by the legal screen renderer.
 */
interface PrivacyPolicyRepository {
    /**
     * Loads Markdown suitable for the privacy policy viewer component.
     *
     * @return [Result] containing localized Markdown on success, or a failure when assets are missing.
     */
    fun getMarkdown(): Result<String>
}
