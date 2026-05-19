package com.frosch2010.lifestyle_scoring_app.domain.model

/**
 * One third-party component entry for the open-source attribution screen.
 *
 * Bundles human-readable naming with deep links so legal views can stay declarative while still
 * offering optional navigation to upstream homepages and full license texts.
 *
 * @property projectName Display name of the dependency as shown to end users.
 * @property version Resolved module version string for reproducibility and support triage.
 * @property projectUrl Canonical homepage or repository URL for the upstream project.
 * @property licenseName Short license identifier (for example `Apache-2.0`).
 * @property licenseUrl Location of the full license text, or empty when not bundled remotely.
 */
data class LicenseEntry(
    val projectName: String,
    val version: String,
    val projectUrl: String,
    val licenseName: String,
    val licenseUrl: String
)
