package com.frosch2010.lifestyle_scoring_app.ui.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.LicensesRepository
import com.frosch2010.lifestyle_scoring_app.domain.model.LicenseEntry
import com.frosch2010.lifestyle_scoring_app.domain.usecase.GetLicensesUseCase
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/**
 * Unit tests for [LicensesViewModel].
 */
class LicensesViewModelTest {

    /**
     * JUnit rule that runs Architecture Components tasks synchronously on the main thread.
     */
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    /**
     * Verifies `load_setsEmptyState_whenRepositoryReturnsEmptyList()` behavior.
     */
    @Test
    fun load_setsEmptyState_whenRepositoryReturnsEmptyList() {
        val repo = object : LicensesRepository {
            /**
             * Test stub returning an empty successful license list.
             *
             * @return [Result.success] with an empty list
             */
            override fun getLicenses(): Result<List<LicenseEntry>> = Result.success(emptyList())
        }
        val vm = LicensesViewModel(GetLicensesUseCase(repo))

        vm.load()

        assertEquals(LicensesViewModel.UiState.Empty::class, vm.uiState.value!!::class)
    }

    /**
     * Verifies `load_setsContentSortedByProjectName_whenRepositoryReturnsEntries()` behavior.
     */
    @Test
    fun load_setsContentSortedByProjectName_whenRepositoryReturnsEntries() {
        val repo = object : LicensesRepository {
            /**
             * Test stub returning two licenses with project names used for sorting assertions.
             *
             * @return [Result.success] with a fixed pair of [LicenseEntry] rows
             */
            override fun getLicenses(): Result<List<LicenseEntry>> = Result.success(
                listOf(
                    LicenseEntry("ZooLib", "1", "", "MIT", ""),
                    LicenseEntry("alphaLib", "1", "", "MIT", "")
                )
            )
        }
        val vm = LicensesViewModel(GetLicensesUseCase(repo))

        vm.load()

        val state = vm.uiState.value as LicensesViewModel.UiState.Content
        assertEquals(listOf("alphaLib", "ZooLib"), state.entries.map { it.projectName })
    }
}

