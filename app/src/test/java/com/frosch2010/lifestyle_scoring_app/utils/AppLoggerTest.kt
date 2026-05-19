package com.frosch2010.lifestyle_scoring_app.utils

import android.util.Log
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog

/**
 * Robolectric-backed tests for [AppLogger] delegating to `android.util.Log` (observed via [ShadowLog]).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AppLoggerTest {

    /**
     * [AppLogger.e] without a throwable posts one ERROR line with the given tag and message.
     */
    @Test
    fun e_withoutThrowable_emitsErrorLog() {
        AppLogger.e("MyTag", "hello")

        val items = ShadowLog.getLogsForTag("MyTag")
        assertEquals(1, items.size)
        assertEquals(Log.ERROR, items[0].type)
        assertEquals("hello", items[0].msg)
    }

    /**
     * [AppLogger.e] with a throwable attaches that instance to the shadow log entry.
     */
    @Test
    fun e_withThrowable_emitsErrorLogWithThrowable() {
        val cause = IllegalStateException("x")
        AppLogger.e("T", "m", cause)

        val items = ShadowLog.getLogsForTag("T")
        assertEquals(1, items.size)
        assertEquals(Log.ERROR, items[0].type)
        assertTrue(items[0].throwable === cause)
    }

    /**
     * [AppLogger.w] posts one WARN line with the given tag and message.
     */
    @Test
    fun w_withoutThrowable_emitsWarningLog() {
        AppLogger.w("WTag", "warn")

        val items = ShadowLog.getLogsForTag("WTag")
        assertEquals(1, items.size)
        assertEquals(Log.WARN, items[0].type)
        assertEquals("warn", items[0].msg)
    }
}
