package com.frosch2010.lifestyle_scoring_app.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.AppInfoRepository
import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.LanguageRepository
import com.frosch2010.lifestyle_scoring_app.domain.model.AppBuildInfo
import com.frosch2010.lifestyle_scoring_app.domain.usecase.GetAppBuildInfoUseCase
import com.frosch2010.lifestyle_scoring_app.domain.usecase.GetPersistedLanguageTagUseCase
import com.frosch2010.lifestyle_scoring_app.domain.usecase.SetAppLanguageUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Unit tests for [SettingsViewModel].
 */
class SettingsViewModelTest {

    /**
     * JUnit rule that runs Architecture Components tasks synchronously on the main thread.
     */
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    /**
     * Verifies `load_setsPersistedLanguageTag_andBuildInfo_whenAvailable()` behavior.
     */
    @Test
    fun load_setsPersistedLanguageTag_andBuildInfo_whenAvailable() {
        val languageRepo = FakeLanguageRepository(persisted = "de")
        val appInfoRepo = FakeAppInfoRepository(Result.success(AppBuildInfo("1.2.3", 123)))

        val vm = SettingsViewModel(
            getAppBuildInfoUseCase = GetAppBuildInfoUseCase(appInfoRepo),
            getPersistedLanguageTagUseCase = GetPersistedLanguageTagUseCase(languageRepo),
            setAppLanguageUseCase = SetAppLanguageUseCase(languageRepo)
        )

        vm.load()

        assertEquals("de", vm.persistedLanguageTag.getOrAwaitValue())
        val buildInfo = vm.buildInfo.getOrAwaitValue()
        assertNotNull(buildInfo)
        assertEquals("1.2.3", buildInfo?.versionName)
        assertEquals(123L, buildInfo?.versionCode)
    }

    /**
     * Verifies `load_setsBuildInfoNull_whenRepositoryFails()` behavior.
     */
    @Test
    fun load_setsBuildInfoNull_whenRepositoryFails() {
        val languageRepo = FakeLanguageRepository(persisted = "")
        val appInfoRepo = FakeAppInfoRepository(Result.failure(IllegalStateException("boom")))

        val vm = SettingsViewModel(
            getAppBuildInfoUseCase = GetAppBuildInfoUseCase(appInfoRepo),
            getPersistedLanguageTagUseCase = GetPersistedLanguageTagUseCase(languageRepo),
            setAppLanguageUseCase = SetAppLanguageUseCase(languageRepo)
        )

        vm.load()

        assertEquals("", vm.persistedLanguageTag.getOrAwaitValue())
        assertNull(vm.buildInfo.getOrAwaitValue())
    }

    /**
     * Verifies `onLanguageSelected_doesNothing_whenSameTagSelected()` behavior.
     */
    @Test
    fun onLanguageSelected_doesNothing_whenSameTagSelected() {
        val languageRepo = FakeLanguageRepository(persisted = "en")
        val appInfoRepo = FakeAppInfoRepository(Result.success(AppBuildInfo("1.0", 1)))

        val vm = SettingsViewModel(
            getAppBuildInfoUseCase = GetAppBuildInfoUseCase(appInfoRepo),
            getPersistedLanguageTagUseCase = GetPersistedLanguageTagUseCase(languageRepo),
            setAppLanguageUseCase = SetAppLanguageUseCase(languageRepo)
        )

        vm.load()
        vm.onLanguageSelected("en")

        assertEquals(0, languageRepo.persistCalls)
    }

    /**
     * Verifies `onLanguageSelected_persistsAndEmitsRestartEvent_whenTagChanges()` behavior.
     */
    @Test
    fun onLanguageSelected_persistsAndEmitsRestartEvent_whenTagChanges() {
        val languageRepo = FakeLanguageRepository(persisted = "en")
        val appInfoRepo = FakeAppInfoRepository(Result.success(AppBuildInfo("1.0", 1)))

        val vm = SettingsViewModel(
            getAppBuildInfoUseCase = GetAppBuildInfoUseCase(appInfoRepo),
            getPersistedLanguageTagUseCase = GetPersistedLanguageTagUseCase(languageRepo),
            setAppLanguageUseCase = SetAppLanguageUseCase(languageRepo)
        )

        vm.load()
        vm.onLanguageSelected("de")

        assertEquals(1, languageRepo.persistCalls)
        assertEquals("de", languageRepo.lastPersisted)
        val event = vm.events.getOrAwaitValue()
        assertEquals(SettingsViewModel.UiEvent.RestartActivity, event.consume())
    }

    private class FakeLanguageRepository(
        private val persisted: String
    ) : LanguageRepository {
        var persistCalls: Int = 0
        var lastPersisted: String? = null

        /**
         * Returns the language tag configured for this fake instance.
         *
         * @return [persisted] value supplied to the fake constructor
         */
        override fun getPersistedLanguageTag(): String = persisted

        /**
         * Records how often persistence ran and stores the last tag for assertions.
         *
         * @param tag BCP-47 language tag to persist
         */
        override fun persistAndApplyLanguageTag(tag: String) {
            persistCalls++
            lastPersisted = tag
        }
    }

    private class FakeAppInfoRepository(
        private val result: Result<AppBuildInfo>
    ) : AppInfoRepository {
        /**
         * Returns the fixed build-info result supplied to this fake.
         *
         * @return [result] from the fake constructor
         */
        override fun getBuildInfo(): Result<AppBuildInfo> = result
    }
}

/**
 * Observes this [LiveData] until the first value is emitted or the wait times out.
 *
 * @param time maximum wait duration expressed in [unit]
 * @param unit unit for the [time] argument
 * @return first value delivered to the temporary observer
 * @throws AssertionError when no value arrives before the timeout
 */
private fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    unit: TimeUnit = TimeUnit.SECONDS
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer = object : Observer<T> {
        /**
         * Captures the emitted value and releases the waiting latch.
         *
         * @param value latest non-null payload from LiveData
         */
        override fun onChanged(value: T) {
            data = value
            latch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }
    observeForever(observer)
    if (!latch.await(time, unit)) {
        removeObserver(observer)
        throw AssertionError("LiveData value was never set.")
    }
    @Suppress("UNCHECKED_CAST")
    return data as T
}

