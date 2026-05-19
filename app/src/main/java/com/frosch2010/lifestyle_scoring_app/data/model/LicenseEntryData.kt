package com.frosch2010.lifestyle_scoring_app.data.model

/**
 * Parsed row shape for one dependency entry straight from `licenses.json` generation output.
 *
 * @property projectName Dependency display name after JSON coercion with unknown fallbacks applied upstream.
 * @property version Module version string when present in the asset payload.
 * @property projectUrl Homepage or SCM URL extracted from license metadata.
 * @property licenseName Primary license identifier resolved from the nested licenses array.
 * @property licenseUrl Link to full license text when the generator supplied one.
 */
data class LicenseEntryData(
    val projectName: String,
    val version: String,
    val projectUrl: String,
    val licenseName: String,
    val licenseUrl: String
)
