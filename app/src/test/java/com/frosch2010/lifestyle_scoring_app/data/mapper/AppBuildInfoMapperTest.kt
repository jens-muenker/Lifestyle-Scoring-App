package com.frosch2010.lifestyle_scoring_app.data.mapper

import com.frosch2010.lifestyle_scoring_app.data.model.AppBuildInfoData
import com.frosch2010.lifestyle_scoring_app.domain.model.AppBuildInfo
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for [AppBuildInfoMapper] (DTO → Domain).
 */
class AppBuildInfoMapperTest {

    private val mapper = AppBuildInfoMapper()

    /**
     * Verifies `toDomain_copiesVersionNameAndVersionCode` behavior.
     */
    @Test
    fun toDomain_copiesVersionNameAndVersionCode() {
        val data = AppBuildInfoData(versionName = "2.0.1", versionCode = 99L)

        val domain = mapper.toDomain(data)

        assertEquals(AppBuildInfo(versionName = "2.0.1", versionCode = 99L), domain)
    }

    /**
     * Verifies `toDomain_preservesEmptyVersionName` behavior.
     */
    @Test
    fun toDomain_preservesEmptyVersionName() {
        val data = AppBuildInfoData(versionName = "", versionCode = 0L)

        val domain = mapper.toDomain(data)

        assertEquals("", domain.versionName)
        assertEquals(0L, domain.versionCode)
    }
}
