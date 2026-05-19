package com.frosch2010.lifestyle_scoring_app.domain.usecase

import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.CardsRepository
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Unit tests for [GetLoveCardsWithIconUseCase].
 */

class GetLoveCardsWithIconUseCaseTest {

    /**
     * Verifies `invoke_delegatesToRepository` behavior.
     */
    @Test
    fun invoke_delegatesToRepository() {
        val repo = mock<CardsRepository>()
        val useCase = GetLoveCardsWithIconUseCase(repo)
        whenever(repo.getLoveCardsWithIcon()).thenReturn(listOf("Jobs" to 1, "House" to 2))

        val result = useCase()

        assertEquals(listOf("Jobs" to 1, "House" to 2), result)
        verify(repo).getLoveCardsWithIcon()
    }
}
