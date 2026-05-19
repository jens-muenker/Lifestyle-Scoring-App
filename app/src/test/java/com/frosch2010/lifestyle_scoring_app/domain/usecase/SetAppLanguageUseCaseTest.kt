package com.frosch2010.lifestyle_scoring_app.domain.usecase

import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.LanguageRepository
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

/**
 * Unit tests for [SetAppLanguageUseCase].
 */

class SetAppLanguageUseCaseTest {

    /**
     * Verifies `invoke_delegatesToRepository` behavior.
     */
    @Test
    fun invoke_delegatesToRepository() {
        val repo = mock<LanguageRepository>()
        val useCase = SetAppLanguageUseCase(repo)

        useCase("en")

        verify(repo).persistAndApplyLanguageTag("en")
    }
}
