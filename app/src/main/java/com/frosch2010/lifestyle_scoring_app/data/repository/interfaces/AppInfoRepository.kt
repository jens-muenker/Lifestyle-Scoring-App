package com.frosch2010.lifestyle_scoring_app.data.repository.interfaces

import com.frosch2010.lifestyle_scoring_app.domain.model.AppBuildInfo

/**
 * Domain port for reading build-time metadata without reaching into Android framework types.
 *
 * Implementations may source values from `PackageManager`, static build config, or tests doubles.
 */
interface AppInfoRepository {
    /**
     * Resolves the current application's version name and code for about/settings surfaces.
     *
     * @return [Result] containing [AppBuildInfo] on success, or failure when package metadata is unavailable.
     */
    fun getBuildInfo(): Result<AppBuildInfo>
}
