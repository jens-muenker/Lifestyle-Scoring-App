package com.frosch2010.lifestyle_scoring_app.data.source.local.interfaces

import com.frosch2010.lifestyle_scoring_app.data.model.AppBuildInfoData

/**
 * Port for reading raw build metadata without exposing Android types to repositories.
 */
interface AppInfoLocalDataSource {
    /**
     * Resolves the hosting package's version fields for the current installation.
     *
     * @return DTO containing version name and long version code suitable for mapper conversion.
     */
    fun getBuildInfo(): AppBuildInfoData
}
