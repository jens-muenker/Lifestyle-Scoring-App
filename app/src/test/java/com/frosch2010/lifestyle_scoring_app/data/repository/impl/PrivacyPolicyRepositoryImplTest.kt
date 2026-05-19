package com.frosch2010.lifestyle_scoring_app.data.repository.impl

import com.frosch2010.lifestyle_scoring_app.data.source.local.interfaces.PrivacyPolicyLocalDataSource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit tests for [PrivacyPolicyRepositoryImpl]: wraps Markdown IO in [Result].
 */
class PrivacyPolicyRepositoryImplTest {

    /**
     * Verifies `getMarkdown_returnsSuccessWhenDataSourceReturns` behavior.
     */
    @Test
    fun getMarkdown_returnsSuccessWhenDataSourceReturns() {
        val dataSource = FakePrivacyPolicyLocalDataSource(markdown = "# Policy\n")
        val repository = PrivacyPolicyRepositoryImpl(dataSource)

        val result = repository.getMarkdown()

        assertTrue(result.isSuccess)
        assertEquals("# Policy\n", result.getOrThrow())
    }

    /**
     * Verifies `getMarkdown_returnsFailureWhenDataSourceThrows` behavior.
     */
    @Test
    fun getMarkdown_returnsFailureWhenDataSourceThrows() {
        val dataSource = FakePrivacyPolicyLocalDataSource(error = RuntimeException("io"))
        val repository = PrivacyPolicyRepositoryImpl(dataSource)

        val result = repository.getMarkdown()

        assertTrue(result.isFailure)
    }

    private class FakePrivacyPolicyLocalDataSource(
        private val markdown: String = "",
        private val error: Throwable? = null
    ) : PrivacyPolicyLocalDataSource {
        override fun getMarkdown(): String {
            error?.let { throw it }
            return markdown
        }
    }
}
