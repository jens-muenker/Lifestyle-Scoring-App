package com.frosch2010.lifestyle_scoring_app.domain.usecase

import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.PrivacyPolicyRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [GetPrivacyPolicyMarkdownUseCase].
 */

class GetPrivacyPolicyMarkdownUseCaseTest {

    /**
     * Verifies `invoke_returnsRepositoryResult` behavior.
     */
    @Test
    fun invoke_returnsRepositoryResult() {
        val repo = FakePrivacyPolicyRepository(Result.success("# Privacy"))
        val useCase = GetPrivacyPolicyMarkdownUseCase(repo)

        val result = useCase()

        assertTrue(result.isSuccess)
        assertEquals("# Privacy", result.getOrThrow())
    }

    private class FakePrivacyPolicyRepository(
        private val result: Result<String>
    ) : PrivacyPolicyRepository {
        override fun getMarkdown(): Result<String> = result
    }
}
