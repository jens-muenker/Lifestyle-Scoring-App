package com.frosch2010.lifestyle_scoring_app.domain.usecase

import com.frosch2010.lifestyle_scoring_app.domain.model.LicenseEntry
import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.LicensesRepository
import javax.inject.Inject

/**
 * Loads bundled OSS attribution rows for the legal licenses screen.
 *
 * @param licensesRepository Data access port abstracting JSON/asset parsing failures.
 */
class GetLicensesUseCase @Inject constructor(
    private val licensesRepository: LicensesRepository
) {
    /**
     * Retrieves the full license inventory suitable for adapter binding.
     *
     * @return Forwarded [Result] preserving success or parse/load errors for UI error states.
     */
    operator fun invoke(): Result<List<LicenseEntry>> = licensesRepository.getLicenses()
}
