package com.frosch2010.lifestyle_scoring_app.data.repository.impl

import com.frosch2010.lifestyle_scoring_app.data.mapper.AppBuildInfoMapper
import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.AppInfoRepository
import com.frosch2010.lifestyle_scoring_app.data.source.local.interfaces.AppInfoLocalDataSource
import com.frosch2010.lifestyle_scoring_app.domain.model.AppBuildInfo
import javax.inject.Inject

/**
 * Default [AppInfoRepository] wiring package metadata through a mapper for clean domain boundaries.
 *
 * @param appInfoLocalDataSource Platform reader producing raw `AppBuildInfoData` rows.
 * @param appBuildInfoMapper Converter lifting DTO fields into domain value objects.
 */
class AppInfoRepositoryImpl @Inject constructor(
    private val appInfoLocalDataSource: AppInfoLocalDataSource,
    private val appBuildInfoMapper: AppBuildInfoMapper
) : AppInfoRepository {

    /**
     * Wraps local retrieval and mapping in [runCatching] so `PackageManager` failures surface as [Result] failures.
     *
     * @return [Result.success] with mapped [AppBuildInfo], or [Result.failure] when metadata cannot be read.
     */
    override fun getBuildInfo(): Result<AppBuildInfo> = runCatching {
        appBuildInfoMapper.toDomain(appInfoLocalDataSource.getBuildInfo())
    }
}
