package com.frosch2010.lifestyle_scoring_app.domain.usecase

import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.LicensesRepository
import com.frosch2010.lifestyle_scoring_app.domain.model.LicenseEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [GetLicensesUseCase].
 */

class GetLicensesUseCaseTest {

    /**
     * Verifies `invoke_returnsRepositoryResult` behavior.
     */
    @Test
    fun invoke_returnsRepositoryResult() {
        val repo = FakeLicensesRepository(
            Result.success(
                listOf(
                    LicenseEntry(
                        projectName = "lib",
                        version = "1.0",
                        projectUrl = "https://x",
                        licenseName = "Apache-2.0",
                        licenseUrl = "https://www.apache.org/licenses/LICENSE-2.0"
                    )
                )
            )
        )
        val useCase = GetLicensesUseCase(repo)

        val result = useCase()

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrThrow().size)
    }

    private class FakeLicensesRepository(
        private val result: Result<List<LicenseEntry>>
    ) : LicensesRepository {
        override fun getLicenses(): Result<List<LicenseEntry>> = result
    }
}
