package com.frosch2010.lifestyle_scoring_app.data.repository.impl

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.frosch2010.lifestyle_scoring_app.data.mapper.AppBuildInfoMapper
import com.frosch2010.lifestyle_scoring_app.data.mapper.LicenseEntryMapper
import com.frosch2010.lifestyle_scoring_app.data.source.local.impl.AppInfoLocalDataSourceImpl
import com.frosch2010.lifestyle_scoring_app.data.source.local.impl.LicensesLocalDataSourceImpl
import com.frosch2010.lifestyle_scoring_app.data.source.local.impl.PrivacyPolicyLocalDataSourceImpl
import com.frosch2010.lifestyle_scoring_app.domain.model.LicenseEntry
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Locale

/**
 * Robolectric-backed smoke tests for [AppInfoRepositoryImpl], [LicensesRepositoryImpl], and
 * [PrivacyPolicyRepositoryImpl] using real [Context], packaged assets, and default mappers.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RepositoriesRobolectricTest {

    /**
     * Verifies `appInfoRepository_returnsBuildInfo()` behavior.
     */
    @Test
    fun appInfoRepository_returnsBuildInfo() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = AppInfoRepositoryImpl(
            appInfoLocalDataSource = AppInfoLocalDataSourceImpl(context),
            appBuildInfoMapper = AppBuildInfoMapper()
        )

        val result = repo.getBuildInfo()

        assertTrue(result.isSuccess)
        val info = result.getOrNull()
        assertNotNull(info)
        assertFalse(info?.versionName.isNullOrBlank())
        assertTrue((info?.versionCode ?: 0L) >= 0L)
    }

    /**
     * Verifies `licensesRepository_readsLicensesFromAssets()` behavior.
     */
    @Test
    fun licensesRepository_readsLicensesFromAssets() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = LicensesRepositoryImpl(
            licensesLocalDataSource = LicensesLocalDataSourceImpl(context),
            licenseEntryMapper = LicenseEntryMapper()
        )

        val result: Result<List<LicenseEntry>> = repo.getLicenses()

        assertTrue(result.isSuccess)
        val licenses = result.getOrThrow()
        // The asset is generated; in CI/local this should exist and usually be non-empty.
        assertTrue(licenses.isNotEmpty())
        assertFalse(licenses.first().projectName.isBlank())
    }

    /**
     * Verifies `privacyPolicyRepository_readsMarkdownForGermanLocale()` behavior.
     */
    @Test
    fun privacyPolicyRepository_readsMarkdownForGermanLocale() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = PrivacyPolicyRepositoryImpl(PrivacyPolicyLocalDataSourceImpl(context))

        // Set default locale for the JVM side; Robolectric picks it up for resources configuration.
        Locale.setDefault(Locale.GERMAN)

        val result = repo.getMarkdown()

        assertTrue(result.isSuccess)
        val md = result.getOrThrow()
        assertTrue(md.contains("##", ignoreCase = false))
    }

    /**
     * Verifies `privacyPolicyRepository_readsMarkdownForEnglishLocale()` behavior.
     */
    @Test
    fun privacyPolicyRepository_readsMarkdownForEnglishLocale() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = PrivacyPolicyRepositoryImpl(PrivacyPolicyLocalDataSourceImpl(context))

        Locale.setDefault(Locale.ENGLISH)

        val result = repo.getMarkdown()

        assertTrue(result.isSuccess)
        val md = result.getOrThrow()
        // English file begins with a heading.
        assertTrue(md.contains("Privacy", ignoreCase = true))
    }
}
