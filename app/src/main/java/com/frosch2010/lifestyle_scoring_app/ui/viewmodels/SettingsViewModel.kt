package com.frosch2010.lifestyle_scoring_app.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.frosch2010.lifestyle_scoring_app.domain.usecase.GetAppBuildInfoUseCase
import com.frosch2010.lifestyle_scoring_app.domain.usecase.GetPersistedLanguageTagUseCase
import com.frosch2010.lifestyle_scoring_app.domain.usecase.SetAppLanguageUseCase
import com.frosch2010.lifestyle_scoring_app.ui.viewmodels.common.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Drives the settings footer (build info), language selection, and process restart after locale changes.
 *
 * @param getAppBuildInfoUseCase Resolves marketing version and version code for the about section.
 * @param getPersistedLanguageTagUseCase Reads the last stored BCP-47 tag for radio preselection.
 * @param setAppLanguageUseCase Persists and applies a new tag through the language repository.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getAppBuildInfoUseCase: GetAppBuildInfoUseCase,
    private val getPersistedLanguageTagUseCase: GetPersistedLanguageTagUseCase,
    private val setAppLanguageUseCase: SetAppLanguageUseCase
) : ViewModel() {

    /**
     * Normalized build metadata row for the settings list footer.
     *
     * @property versionName User-visible semantic version.
     * @property versionCode Monotonic version code stringified in the UI if needed.
     */
    data class BuildInfoUi(
        val versionName: String,
        val versionCode: Long
    )

    /**
     * One-shot signals such as activity recreation after switching locales.
     */
    sealed class UiEvent {
        /**
         * Instructs the activity to recreate so every `Activity` reloads localized resources deterministically.
         */
        data object RestartActivity : UiEvent()
    }

    /**
     * Aggregate surface combining build info and the persisted language tag for single-source rendering.
     *
     * @property buildInfo Resolved footer payload, or `null` while unavailable.
     * @property persistedLanguageTag Tag backing radio selection; empty means system default semantics upstream.
     */
    data class UiState(
        val buildInfo: BuildInfoUi? = null,
        val persistedLanguageTag: String = ""
    )

    private val _buildInfo = MutableLiveData<BuildInfoUi?>()
    val buildInfo: LiveData<BuildInfoUi?> = _buildInfo

    private val _persistedLanguageTag = MutableLiveData<String>()
    val persistedLanguageTag: LiveData<String> = _persistedLanguageTag

    private val _uiState = MutableLiveData(UiState())
    val uiState: LiveData<UiState> = _uiState

    private val _events = MutableLiveData<Event<UiEvent>>()
    val events: LiveData<Event<UiEvent>> = _events

    /**
     * Hydrates language selection and build metadata from use cases, then publishes [UiState].
     */
    fun load() {
        _persistedLanguageTag.value = getPersistedLanguageTagUseCase()
        _buildInfo.value = getAppBuildInfoUseCase()
            .getOrNull()
            ?.let { BuildInfoUi(it.versionName, it.versionCode) }
        updateUiState()
    }

    /**
     * Persists [tag] when it differs from the current snapshot and requests an activity restart for locale refresh.
     *
     * @param tag BCP-47 tag chosen in the language list, or empty for system default.
     */
    fun onLanguageSelected(tag: String) {
        if (tag == (_persistedLanguageTag.value ?: "")) return
        setAppLanguageUseCase(tag)
        _persistedLanguageTag.value = tag
        updateUiState()
        _events.value = Event(UiEvent.RestartActivity)
    }

    /**
     * Flattens granular LiveData fields into the single [UiState] stream used by modern collectors.
     */
    private fun updateUiState() {
        _uiState.value = UiState(
            buildInfo = _buildInfo.value,
            persistedLanguageTag = _persistedLanguageTag.value.orEmpty()
        )
    }
}
