package com.frosch2010.lifestyle_scoring_app.data.repository.impl

import com.frosch2010.lifestyle_scoring_app.data.mapper.LicenseEntryMapper
import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.LicensesRepository
import com.frosch2010.lifestyle_scoring_app.data.source.local.interfaces.LicensesLocalDataSource
import com.frosch2010.lifestyle_scoring_app.domain.model.LicenseEntry
import java.io.FileNotFoundException
import javax.inject.Inject

/**
 * Maps asset-backed license rows to domain models while degrading missing generator output gracefully.
 *
 * @param licensesLocalDataSource Loader that reads and parses `licenses.json` from assets.
 * @param licenseEntryMapper Row-level mapper from DTO to domain entity.
 */
class LicensesRepositoryImpl @Inject constructor(
    private val licensesLocalDataSource: LicensesLocalDataSource,
    private val licenseEntryMapper: LicenseEntryMapper
) : LicensesRepository {

    /**
     * Converts each parsed row and collapses a missing asset file into an empty success for soft UX.
     *
     * @return [Result.success] with mapped entries, empty list when the asset is absent, or failure on parse errors.
     */
    override fun getLicenses(): Result<List<LicenseEntry>> {
        return runCatching {
            licensesLocalDataSource.getLicenses().map { licenseEntryMapper.toDomain(it) }
        }.recoverCatching { throwable ->
            if (throwable is FileNotFoundException) {
                emptyList()
            } else {
                throw throwable
            }
        }
    }
}
