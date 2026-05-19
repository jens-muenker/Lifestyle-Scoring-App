package com.frosch2010.lifestyle_scoring_app.ui.util

import android.content.Intent
import android.os.Build
import java.io.Serializable

/**
 * Reads a typed serializable extra using the API 33+ overload when available, with a safe legacy fallback.
 *
 * @receiver Intent carrying the extra under [key].
 * @param key Extra key registered in [IntentExtras] or feature-specific contracts.
 * @return Cast payload, or `null` when missing or of an incompatible runtime type.
 */
inline fun <reified T : Serializable> Intent.getSerializableExtraCompat(key: String): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getSerializableExtra(key, T::class.java)
    } else {
        @Suppress("DEPRECATION")
        getSerializableExtra(key) as? T
    }
}
