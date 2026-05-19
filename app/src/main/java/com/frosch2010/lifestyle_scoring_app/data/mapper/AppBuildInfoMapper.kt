package com.frosch2010.lifestyle_scoring_app.data.mapper

import com.frosch2010.lifestyle_scoring_app.data.model.AppBuildInfoData
import com.frosch2010.lifestyle_scoring_app.domain.model.AppBuildInfo
import javax.inject.Inject

/**
 * Translates Android-sourced build DTOs into framework-agnostic domain records.
 */
class AppBuildInfoMapper @Inject constructor() {
    /**
     * Copies primitive fields from the data transfer object into an immutable domain value object.
     *
     * @param data Raw build metadata produced by local data sources.
     * @return Domain [AppBuildInfo] safe for use cases and UI view models.
     */
    fun toDomain(data: AppBuildInfoData): AppBuildInfo {
        return AppBuildInfo(
            versionName = data.versionName,
            versionCode = data.versionCode
        )
    }
}
