package com.frosch2010.lifestyle_scoring_app.data.source.local.interfaces

/**
 * Supplies the in-app privacy policy text as a single Markdown document bundled with the app.
 *
 * Which language variant is returned is defined by the implementation (typically by reading the
 * primary locale from the [android.content.res.Resources] configuration of the injected
 * application [android.content.Context]).
 */
interface PrivacyPolicyLocalDataSource {
    /**
     * @return Complete UTF-8 Markdown body for display (for example on the privacy-policy screen).
     */
    fun getMarkdown(): String
}
