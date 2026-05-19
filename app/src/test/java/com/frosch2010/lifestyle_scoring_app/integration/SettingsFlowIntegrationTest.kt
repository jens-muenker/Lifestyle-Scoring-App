package com.frosch2010.lifestyle_scoring_app.integration

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.frosch2010.lifestyle_scoring_app.data.datastore.AppPreferencesDataStore
import com.frosch2010.lifestyle_scoring_app.data.mapper.AppBuildInfoMapper
import com.frosch2010.lifestyle_scoring_app.data.repository.impl.AppInfoRepositoryImpl
import com.frosch2010.lifestyle_scoring_app.data.repository.impl.LanguageRepositoryImpl
import com.frosch2010.lifestyle_scoring_app.data.source.local.impl.AppInfoLocalDataSourceImpl
import com.frosch2010.lifestyle_scoring_app.data.source.local.impl.LanguageLocalDataSourceImpl
import com.frosch2010.lifestyle_scoring_app.domain.usecase.GetAppBuildInfoUseCase
import com.frosch2010.lifestyle_scoring_app.domain.usecase.GetPersistedLanguageTagUseCase
import com.frosch2010.lifestyle_scoring_app.domain.usecase.SetAppLanguageUseCase
import com.frosch2010.lifestyle_scoring_app.ui.viewmodels.common.Event
import com.frosch2010.lifestyle_scoring_app.ui.viewmodels.SettingsViewModel
import com.frosch2010.lifestyle_scoring_app.utils.LanguageTags
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Robolectric-backed integration test wiring [SettingsViewModel] to real [LanguageRepositoryImpl] and
 * [AppInfoRepositoryImpl] (in-memory preferences, packaged build metadata) without Hilt.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SettingsFlowIntegrationTest {

    /**
     * Runs Architecture Components executors synchronously so `LiveData` from [SettingsViewModel] is delivered
     * on the test thread before assertions.
     */
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    /**
     * After [SettingsViewModel.load], build info is present; selecting the other of [LanguageTags.DE] / [LanguageTags.EN]
     * than the loaded tag persists via the repository, updates [SettingsViewModel.persistedLanguageTag], and posts
     * [SettingsViewModel.UiEvent.RestartActivity] wrapped in [Event].
     */
    @Test
    fun settingsFlow_loadAndChangeLanguage_worksWithRealRepositories() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val languageRepo = LanguageRepositoryImpl(
            LanguageLocalDataSourceImpl(AppPreferencesDataStore(context))
        )
        val appInfoRepo = AppInfoRepositoryImpl(
            appInfoLocalDataSource = AppInfoLocalDataSourceImpl(context),
            appBuildInfoMapper = AppBuildInfoMapper()
        )

        val vm = SettingsViewModel(
            getAppBuildInfoUseCase = GetAppBuildInfoUseCase(appInfoRepo),
            getPersistedLanguageTagUseCase = GetPersistedLanguageTagUseCase(languageRepo),
            setAppLanguageUseCase = SetAppLanguageUseCase(languageRepo)
        )

        vm.load()
        assertNotNull(vm.buildInfo.value)

        val initial = vm.persistedLanguageTag.value ?: ""
        val target = if (initial == LanguageTags.DE) LanguageTags.EN else LanguageTags.DE
        vm.onLanguageSelected(target)

        assertEquals(target, vm.persistedLanguageTag.value)
        assertEquals(target, languageRepo.getPersistedLanguageTag())
        val event = vm.events.value
        assertNotNull(event)
        assertNotNull((event as Event<SettingsViewModel.UiEvent>).consume())
    }
}

