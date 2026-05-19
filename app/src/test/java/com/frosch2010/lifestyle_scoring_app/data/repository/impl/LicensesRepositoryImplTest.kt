package com.frosch2010.lifestyle_scoring_app.data.repository.impl

import com.frosch2010.lifestyle_scoring_app.data.mapper.LicenseEntryMapper
import com.frosch2010.lifestyle_scoring_app.data.model.LicenseEntryData
import com.frosch2010.lifestyle_scoring_app.data.source.local.interfaces.LicensesLocalDataSource
import com.frosch2010.lifestyle_scoring_app.domain.model.LicenseEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.FileNotFoundException

/**
 * Unit tests for [LicensesRepositoryImpl].
 */
class LicensesRepositoryImplTest {

    /**
     * Verifies `getLicenses_mapsDataSourceRowsToDomain()` behavior.
     */
    @Test
    fun getLicenses_mapsDataSourceRowsToDomain() {
        val row = LicenseEntryData(
            projectName = "LibA",
            version = "1.0",
            projectUrl = "https://a.example",
            licenseName = "Apache-2.0",
            licenseUrl = "https://spdx.org/licenses/Apache-2.0.html"
        )
        val dataSource = object : LicensesLocalDataSource {
            override fun getLicenses(): List<LicenseEntryData> = listOf(row)
        }
        val repository = LicensesRepositoryImpl(dataSource, LicenseEntryMapper())

        val result = repository.getLicenses()

        assertTrue(result.isSuccess)
        val entries = result.getOrThrow()
        assertEquals(1, entries.size)
        assertEquals(
            LicenseEntry(
                projectName = "LibA",
                version = "1.0",
                projectUrl = "https://a.example",
                licenseName = "Apache-2.0",
                licenseUrl = "https://spdx.org/licenses/Apache-2.0.html"
            ),
            entries[0]
        )
    }

    /**
     * Verifies `getLicenses_returnsEmptySuccess_whenLicensesFileIsMissing()` behavior.
     */
    @Test
    fun getLicenses_returnsEmptySuccess_whenLicensesFileIsMissing() {
        // Arrange
        val dataSource = object : LicensesLocalDataSource {
            /**
             * Simulates a missing licenses asset so the repository can map to an empty success.
             *
             * @throws FileNotFoundException always
             */
            override fun getLicenses(): List<LicenseEntryData> {
                throw FileNotFoundException("licenses.json missing")
            }
        }
        val repository = LicensesRepositoryImpl(dataSource, LicenseEntryMapper())

        // Act
        val result = repository.getLicenses()

        // Assert
        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow().isEmpty())
    }

    /**
     * Verifies `getLicenses_returnsFailure_forNonFileNotFoundExceptions()` behavior.
     */
    @Test
    fun getLicenses_returnsFailure_forNonFileNotFoundExceptions() {
        // Arrange
        val dataSource = object : LicensesLocalDataSource {
            /**
             * Simulates a non-IO failure when reading license rows.
             *
             * @throws IllegalStateException always
             */
            override fun getLicenses(): List<LicenseEntryData> {
                throw IllegalStateException("broken json")
            }
        }
        val repository = LicensesRepositoryImpl(dataSource, LicenseEntryMapper())

        // Act
        val result = repository.getLicenses()

        // Assert
        assertTrue(result.isFailure)
    }
}
