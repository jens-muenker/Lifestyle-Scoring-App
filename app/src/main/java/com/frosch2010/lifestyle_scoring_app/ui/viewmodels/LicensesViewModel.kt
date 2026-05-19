package com.frosch2010.lifestyle_scoring_app.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.frosch2010.lifestyle_scoring_app.domain.model.LicenseEntry
import com.frosch2010.lifestyle_scoring_app.domain.usecase.GetLicensesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Maps legal OSS attribution data into a small finite UI state machine for the licenses screen.
 *
 * @param getLicensesUseCase Loads bundled dependency rows, surfacing soft-empty when assets are missing.
 */
@HiltViewModel
class LicensesViewModel @Inject constructor(
    private val getLicensesUseCase: GetLicensesUseCase
) : ViewModel() {

    /**
     * Screen phases from initial load through populated or failed lists.
     */
    sealed class UiState {
        /** Spinner or skeleton should be visible while IO runs. */
        data object Loading : UiState()

        /** Asset intentionally empty (for example generator skipped) — show friendly empty copy. */
        data object Empty : UiState()

        /**
         * Non-empty list ready for RecyclerView submission.
         *
         * @property entries Dependency rows sorted alphabetically for predictable scanning.
         */
        data class Content(val entries: List<LicenseEntry>) : UiState()

        /** Hard failure (parse error, corrupt asset) distinct from benign empty lists. */
        data object Error : UiState()
    }

    private val _uiState = MutableLiveData<UiState>(UiState.Loading)
    val uiState: LiveData<UiState> = _uiState

    /**
     * Triggers a fresh load and maps [Result] outcomes into [UiState] branches.
     */
    fun load() {
        _uiState.value = UiState.Loading
        val result = getLicensesUseCase()
        _uiState.value = result.fold(
            onSuccess = { entries ->
                if (entries.isEmpty()) {
                    UiState.Empty
                } else {
                    UiState.Content(entries.sortedBy { it.projectName.lowercase() })
                }
            },
            onFailure = { UiState.Error }
        )
    }
}
