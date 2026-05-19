package com.frosch2010.lifestyle_scoring_app.ui.viewmodels.common

/**
 * One-shot payload wrapper for [androidx.lifecycle.LiveData] emissions that must not replay on rotation.
 *
 * @param T Type of the transient UI signal (snackbar text, navigation request, etc.).
 */
class Event<out T>(private val value: T) {
    /**
     * Whether [consume] has already handed the payload to the UI layer.
     */
    private var consumed = false

    /**
     * Returns the payload exactly once so observers can safely re-run without duplicate side effects.
     *
     * @return [value] on first call, then `null` on subsequent invocations.
     */
    fun consume(): T? {
        if (consumed) return null
        consumed = true
        return value
    }

}
