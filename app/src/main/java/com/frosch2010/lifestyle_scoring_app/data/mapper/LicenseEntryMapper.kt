package com.frosch2010.lifestyle_scoring_app.data.mapper

import com.frosch2010.lifestyle_scoring_app.data.model.LicenseEntryData
import com.frosch2010.lifestyle_scoring_app.domain.model.LicenseEntry
import javax.inject.Inject

/**
 * Bridges JSON-shaped license rows to stable domain entities consumed by legal screens.
 */
class LicenseEntryMapper @Inject constructor() {
    /**
     * Performs a straight field projection from the asset DTO to the domain model without enrichment.
     *
     * @param data Parsed license row originating from `LicensesLocalDataSource`.
     * @return Domain [LicenseEntry] ready for adapter binding or sorting in repositories.
     */
    fun toDomain(data: LicenseEntryData): LicenseEntry {
        return LicenseEntry(
            projectName = data.projectName,
            version = data.version,
            projectUrl = data.projectUrl,
            licenseName = data.licenseName,
            licenseUrl = data.licenseUrl
        )
    }
}
