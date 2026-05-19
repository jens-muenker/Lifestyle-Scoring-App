package com.frosch2010.lifestyle_scoring_app.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.PrivacyPolicyRepository
import com.frosch2010.lifestyle_scoring_app.domain.usecase.GetPrivacyPolicyMarkdownUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Tests for [PrivacyPolicyViewModel] loading pipeline (AAA, Repository-Fake).
 */
class PrivacyPolicyViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    /**
     * Verifies `load_onSuccess_setsContentWithMarkdown` behavior.
     */
    @Test
    fun load_onSuccess_setsContentWithMarkdown() {
        val repo = object : PrivacyPolicyRepository {
            override fun getMarkdown() = Result.success("# Policy\nBody")
        }
        val vm = PrivacyPolicyViewModel(GetPrivacyPolicyMarkdownUseCase(repo))

        vm.load()

        val state = vm.uiState.value
        assertTrue(state is PrivacyPolicyViewModel.UiState.Content)
        assertEquals("# Policy\nBody", (state as PrivacyPolicyViewModel.UiState.Content).markdown)
    }

    /**
     * Verifies `load_onFailure_setsError` behavior.
     */
    @Test
    fun load_onFailure_setsError() {
        val repo = object : PrivacyPolicyRepository {
            override fun getMarkdown(): Result<String> =
                Result.failure(IllegalStateException("missing"))
        }
        val vm = PrivacyPolicyViewModel(GetPrivacyPolicyMarkdownUseCase(repo))

        vm.load()

        assertEquals(PrivacyPolicyViewModel.UiState.Error::class, vm.uiState.value!!::class)
    }
}
