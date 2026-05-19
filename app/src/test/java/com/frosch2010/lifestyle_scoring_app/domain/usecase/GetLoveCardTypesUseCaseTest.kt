package com.frosch2010.lifestyle_scoring_app.domain.usecase

import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.CardsRepository
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Unit tests for [GetLoveCardTypesUseCase].
 */

class GetLoveCardTypesUseCaseTest {

    /**
     * Verifies `invoke_delegatesToRepository` behavior.
     */
    @Test
    fun invoke_delegatesToRepository() {
        val repo = mock<CardsRepository>()
        val useCase = GetLoveCardTypesUseCase(repo)
        whenever(repo.getLoveCardTypes()).thenReturn(listOf("Jobs", "House"))

        val types = useCase()

        assertEquals(listOf("Jobs", "House"), types)
        verify(repo).getLoveCardTypes()
    }
}
