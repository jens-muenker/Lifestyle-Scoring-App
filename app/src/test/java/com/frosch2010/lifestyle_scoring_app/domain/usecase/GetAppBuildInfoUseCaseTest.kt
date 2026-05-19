package com.frosch2010.lifestyle_scoring_app.domain.usecase

import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.AppInfoRepository
import com.frosch2010.lifestyle_scoring_app.domain.model.AppBuildInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Unit tests for [GetAppBuildInfoUseCase].
 */

class GetAppBuildInfoUseCaseTest {

    /**
     * Verifies `invoke_returnsRepositoryResult` behavior.
     */
    @Test
    fun invoke_returnsRepositoryResult() {
        val expected = Result.success(AppBuildInfo(versionName = "1.0", versionCode = 1L))
        val repo = mock<AppInfoRepository>()
        whenever(repo.getBuildInfo()).thenReturn(expected)
        val useCase = GetAppBuildInfoUseCase(repo)

        val result = useCase()

        assertTrue(result.isSuccess)
        assertEquals(expected.getOrThrow(), result.getOrThrow())
        verify(repo).getBuildInfo()
    }
}
