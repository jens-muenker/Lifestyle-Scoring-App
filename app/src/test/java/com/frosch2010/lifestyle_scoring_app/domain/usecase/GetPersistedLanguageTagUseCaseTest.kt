package com.frosch2010.lifestyle_scoring_app.domain.usecase

import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.LanguageRepository
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Unit tests for [GetPersistedLanguageTagUseCase].
 */

class GetPersistedLanguageTagUseCaseTest {

    /**
     * Verifies `invoke_returnsValueFromRepository` behavior.
     */
    @Test
    fun invoke_returnsValueFromRepository() {
        val repo = mock<LanguageRepository>()
        whenever(repo.getPersistedLanguageTag()).thenReturn("de")
        val useCase = GetPersistedLanguageTagUseCase(repo)

        assertEquals("de", useCase())
        verify(repo).getPersistedLanguageTag()
    }
}
