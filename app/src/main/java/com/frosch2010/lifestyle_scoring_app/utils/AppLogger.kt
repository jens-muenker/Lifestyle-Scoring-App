package com.frosch2010.lifestyle_scoring_app.utils

import android.util.Log

/**
 * Thin wrapper around `android.util.Log` so call sites stay uniform and ProGuard can strip noise.
 *
 * Release builds rely on `-assumenosideeffects` rules to elide logging without scattering `#if DEBUG`.
 */
object AppLogger {
    /**
     * Emits an error-level diagnostic, optionally including a causal stack trace.
     *
     * @param tag Short component identifier (typically the simple class name).
     * @param message Human-readable explanation of the failure context.
     * @param throwable Optional root cause attached to the log entry.
     */
    fun e(tag: String, message: String, throwable: Throwable? = null) {
        if (throwable == null) {
            Log.e(tag, message)
        } else {
            Log.e(tag, message, throwable)
        }
    }

    /**
     * Emits a warning-level diagnostic for recoverable anomalies or degraded behavior.
     *
     * @param tag Short component identifier (typically the simple class name).
     * @param message Human-readable warning text.
     * @param throwable Optional supplemental exception for stack context.
     */
    fun w(tag: String, message: String, throwable: Throwable? = null) {
        if (throwable == null) {
            Log.w(tag, message)
        } else {
            Log.w(tag, message, throwable)
        }
    }
}
