package com.frosch2010.lifestyle_scoring_app.domain.model

/**
 * Immutable snapshot of application build metadata surfaced in settings or about flows.
 *
 * Keeps marketing-facing version strings separate from the monotonic version code stores use
 * for upgrade ordering, so UI layers never infer semantics from raw integers alone.
 *
 * @property versionName User-visible semantic version label (for example `1.4.0`).
 * @property versionCode Monotonic integer assigned by the build system for release ordering.
 */
data class AppBuildInfo(
    val versionName: String,
    val versionCode: Long
)
