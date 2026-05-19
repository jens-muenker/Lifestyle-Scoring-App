package com.frosch2010.lifestyle_scoring_app.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.frosch2010.lifestyle_scoring_app.domain.usecase.GetPrivacyPolicyMarkdownUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Supplies localized Markdown for the privacy policy viewer with explicit loading and error gates.
 *
 * @param getPrivacyPolicyMarkdownUseCase Reads raw policy text for the active locale configuration.
 */
@HiltViewModel
class PrivacyPolicyViewModel @Inject constructor(
    private val getPrivacyPolicyMarkdownUseCase: GetPrivacyPolicyMarkdownUseCase
) : ViewModel() {

    /**
     * UI phases while Markdown is resolved or rendered.
     */
    sealed class UiState {
        /** Indicates the Markdown pipeline is still running. */
        data object Loading : UiState()

        /**
         * Successful payload for the Markdown renderer.
         *
         * @property markdown UTF-8 Markdown string including headings and links.
         */
        data class Content(val markdown: String) : UiState()

        /** IO or missing-resource failure path. */
        data object Error : UiState()
    }

    private val _uiState = MutableLiveData<UiState>(UiState.Loading)
    val uiState: LiveData<UiState> = _uiState

    /**
     * Invokes the use case and folds the [Result] into [UiState.Content] or [UiState.Error].
     */
    fun load() {
        _uiState.value = UiState.Loading
        val result = getPrivacyPolicyMarkdownUseCase()
        _uiState.value = result.fold(
            onSuccess = { UiState.Content(it) },
            onFailure = { UiState.Error }
        )
    }
}
