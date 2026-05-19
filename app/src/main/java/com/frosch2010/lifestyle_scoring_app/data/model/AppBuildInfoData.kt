package com.frosch2010.lifestyle_scoring_app.data.model

/**
 * DTO mirroring `PackageManager` fields before mapping into domain [AppBuildInfo][com.frosch2010.lifestyle_scoring_app.domain.model.AppBuildInfo].
 *
 * @property versionName Marketing version string as returned by the platform, possibly empty in edge builds.
 * @property versionCode Long version code normalized via `PackageInfoCompat` for API compatibility.
 */
data class AppBuildInfoData(
    val versionName: String,
    val versionCode: Long
)
