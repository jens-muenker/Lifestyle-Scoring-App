package com.frosch2010.lifestyle_scoring_app.data.mapper

import com.frosch2010.lifestyle_scoring_app.data.model.LicenseEntryData
import com.frosch2010.lifestyle_scoring_app.domain.model.LicenseEntry
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for [LicenseEntryMapper] (DTO → Domain).
 */
class LicenseEntryMapperTest {

    private val mapper = LicenseEntryMapper()

    /**
     * Verifies `toDomain_copiesAllFields` behavior.
     */
    @Test
    fun toDomain_copiesAllFields() {
        val data = LicenseEntryData(
            projectName = "ExampleLib",
            version = "3.2.1",
            projectUrl = "https://example.org/lib",
            licenseName = "MIT",
            licenseUrl = "https://opensource.org/licenses/MIT"
        )

        val domain = mapper.toDomain(data)

        assertEquals(
            LicenseEntry(
                projectName = "ExampleLib",
                version = "3.2.1",
                projectUrl = "https://example.org/lib",
                licenseName = "MIT",
                licenseUrl = "https://opensource.org/licenses/MIT"
            ),
            domain
        )
    }

    /**
     * Verifies `toDomain_preservesEmptyStrings` behavior.
     */
    @Test
    fun toDomain_preservesEmptyStrings() {
        val data = LicenseEntryData(
            projectName = "",
            version = "",
            projectUrl = "",
            licenseName = "",
            licenseUrl = ""
        )

        val domain = mapper.toDomain(data)

        assertEquals("", domain.projectName)
        assertEquals("", domain.version)
        assertEquals("", domain.projectUrl)
        assertEquals("", domain.licenseName)
        assertEquals("", domain.licenseUrl)
    }
}
