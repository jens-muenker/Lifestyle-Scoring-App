package com.frosch2010.lifestyle_scoring_app.data.repository.interfaces

import com.frosch2010.lifestyle_scoring_app.domain.model.LicenseEntry

/**
 * Domain port for loading bundled third-party license attribution data.
 */
interface LicensesRepository {
    /**
     * Loads the complete license inventory required for regulatory disclosure screens.
     *
     * @return [Result] wrapping the ordered list of [LicenseEntry], or an error when assets are corrupt.
     */
    fun getLicenses(): Result<List<LicenseEntry>>
}
