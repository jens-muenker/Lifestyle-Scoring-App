package com.frosch2010.lifestyle_scoring_app.data.repository.impl

import com.frosch2010.lifestyle_scoring_app.data.mapper.AppBuildInfoMapper
import com.frosch2010.lifestyle_scoring_app.data.model.AppBuildInfoData
import com.frosch2010.lifestyle_scoring_app.data.source.local.interfaces.AppInfoLocalDataSource
import com.frosch2010.lifestyle_scoring_app.domain.model.AppBuildInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [AppInfoRepositoryImpl]: maps local build DTOs and surfaces [Result] semantics.
 */
class AppInfoRepositoryImplTest {

    /**
     * Verifies `getBuildInfo_returnsSuccessWithMappedDomainModel` behavior.
     */
    @Test
    fun getBuildInfo_returnsSuccessWithMappedDomainModel() {
        // Arrange
        val data = AppBuildInfoData(versionName = "1.2.3", versionCode = 42L)
        val dataSource = FakeAppInfoLocalDataSource(data)
        val repository = AppInfoRepositoryImpl(dataSource, AppBuildInfoMapper())

        // Act
        val result = repository.getBuildInfo()

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(AppBuildInfo(versionName = "1.2.3", versionCode = 42L), result.getOrThrow())
    }

    /**
     * Verifies `getBuildInfo_returnsFailure_whenLocalSourceThrows` behavior.
     */
    @Test
    fun getBuildInfo_returnsFailure_whenLocalSourceThrows() {
        // Arrange
        val dataSource = FakeAppInfoLocalDataSource(error = IllegalStateException("pm"))
        val repository = AppInfoRepositoryImpl(dataSource, AppBuildInfoMapper())

        // Act
        val result = repository.getBuildInfo()

        // Assert
        assertTrue(result.isFailure)
    }

    private class FakeAppInfoLocalDataSource(
        private val data: AppBuildInfoData? = null,
        private val error: Throwable? = null
    ) : AppInfoLocalDataSource {
        override fun getBuildInfo(): AppBuildInfoData {
            error?.let { throw it }
            return requireNotNull(data)
        }
    }
}
