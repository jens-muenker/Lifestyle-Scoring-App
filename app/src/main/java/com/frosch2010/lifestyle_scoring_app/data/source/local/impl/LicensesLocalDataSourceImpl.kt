package com.frosch2010.lifestyle_scoring_app.data.source.local.impl

import android.content.Context
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.data.model.LicenseEntryData
import com.frosch2010.lifestyle_scoring_app.data.source.local.interfaces.LicensesLocalDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject

/**
 * Parses the Gradle-generated `licenses.json` asset into typed rows with defensive defaults.
 *
 * @param context Application [Context] for asset manager access and localized unknown placeholders.
 */
class LicensesLocalDataSourceImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : LicensesLocalDataSource {

    /**
     * Reads the JSON asset, walks the `dependencies` array, and materializes [LicenseEntryData] rows.
     *
     * @return Non-null list (possibly empty) of parsed license rows.
     */
    override fun getLicenses(): List<LicenseEntryData> {
        val jsonString = context.assets.open(LICENSES_ASSET_FILE).bufferedReader().use { it.readText() }
        val root = JSONObject(jsonString)
        val dependencies = root.optJSONArray("dependencies") ?: JSONArray()

        val unknown = context.getString(R.string.license_unknown)
        val items = mutableListOf<LicenseEntryData>()
        for (i in 0 until dependencies.length()) {
            val item = dependencies.getJSONObject(i)
            val projectName = item.optString("projectName", unknown)
            val version = item.optString("moduleVersion", "")
            val projectUrl = item.optString("projectUrl", "")
            val licenseObj = item.optJSONArray("licenses")?.optJSONObject(0)
            val licenseName = licenseObj?.optString("name", unknown).orEmpty().ifBlank { unknown }
            val licenseUrl = licenseObj?.optString("url", "").orEmpty()

            items.add(
                LicenseEntryData(
                    projectName = projectName,
                    version = version,
                    projectUrl = projectUrl,
                    licenseName = licenseName,
                    licenseUrl = licenseUrl
                )
            )
        }
        return items
    }

    private companion object {
        /**
         * Asset path for the generated OSS attribution file shipped inside `assets/`.
         */
        private const val LICENSES_ASSET_FILE = "licenses.json"
    }
}
