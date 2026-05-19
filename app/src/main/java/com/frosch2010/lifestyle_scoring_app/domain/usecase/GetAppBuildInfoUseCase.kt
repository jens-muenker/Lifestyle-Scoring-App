package com.frosch2010.lifestyle_scoring_app.domain.usecase

import com.frosch2010.lifestyle_scoring_app.domain.model.AppBuildInfo
import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.AppInfoRepository
import javax.inject.Inject

/**
 * Thin read use case surfacing build metadata for settings/about presentation layers.
 *
 * @param appInfoRepository Source of truth for version strings and codes abstracted from Android APIs.
 */
class GetAppBuildInfoUseCase @Inject constructor(
    private val appInfoRepository: AppInfoRepository
) {
    /**
     * Fetches the latest resolved build information snapshot.
     *
     * @return Propagated [Result] from the repository layer without additional transformation.
     */
    operator fun invoke(): Result<AppBuildInfo> = appInfoRepository.getBuildInfo()
}
