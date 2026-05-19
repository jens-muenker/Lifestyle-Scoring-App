package com.frosch2010.lifestyle_scoring_app.data.source.local.impl

import android.content.Context
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.data.source.local.interfaces.PrivacyPolicyLocalDataSource
import com.frosch2010.lifestyle_scoring_app.utils.LanguageTags
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Reads the privacy policy from `res/raw` Markdown files, picking German or English from the
 * primary entry of [android.content.res.Configuration.getLocales] on [Context.getResources].
 *
 * If that language tag equals [LanguageTags.DE], [R.raw.privacy_policy_de] is used; every other
 * value (including empty when no locale is present) uses [R.raw.privacy_policy_en].
 *
 * @param context Application [Context], injected with Hilt `@ApplicationContext`, used for configuration and raw-resource IO.
 */
class PrivacyPolicyLocalDataSourceImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : PrivacyPolicyLocalDataSource {

    /**
     * Opens the selected raw asset and returns its entire text as UTF-8 (default platform decoding).
     *
     * @return Markdown content of `privacy_policy_de` or `privacy_policy_en`.
     * @throws android.content.res.Resources.NotFoundException if the resolved raw id is invalid (unexpected at runtime for packaged `R.raw` ids).
     * @throws java.io.IOException if reading the stream fails.
     */
    override fun getMarkdown(): String {
        val language = context.resources.configuration.locales[0]?.language.orEmpty()
        val rawId = if (language == LanguageTags.DE) R.raw.privacy_policy_de else R.raw.privacy_policy_en
        return context.resources.openRawResource(rawId).bufferedReader().use { it.readText() }
    }
}
