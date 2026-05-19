package com.frosch2010.lifestyle_scoring_app.data.source.local.interfaces

import com.frosch2010.lifestyle_scoring_app.data.model.LicenseEntryData

/**
 * Loader port for dependency license rows parsed from packaged assets.
 */
interface LicensesLocalDataSource {
    /**
     * Reads and parses the bundled license inventory.
     *
     * @return Ordered DTO list prior to domain mapping; throws when IO or JSON structure is invalid.
     */
    fun getLicenses(): List<LicenseEntryData>
}
