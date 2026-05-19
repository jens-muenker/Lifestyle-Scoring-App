package com.frosch2010.lifestyle_scoring_app.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

/**
 * On-disk filename for the app-scoped Jetpack DataStore preferences graph.
 */
private const val APP_PREFERENCES_NAME = "lifestyle_scoring_prefs"

/**
 * Typed preference key backing the persisted BCP-47 language tag string.
 */
private val KEY_LANGUAGE_TAG = stringPreferencesKey("app_language_tag")

/**
 * Lazily constructs the application-wide [DataStore] bound to this [Context].
 *
 * @receiver Any [Context] in the app process; typically the long-lived application context.
 * @return Shared preferences DataStore instance keyed by [APP_PREFERENCES_NAME].
 */
val Context.appPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = APP_PREFERENCES_NAME
)

/**
 * Blocking façade over asynchronous DataStore IO for language reads/writes on main-thread callers.
 *
 * Prefer migrating call sites to suspending APIs over time; this wrapper exists because legacy
 * repository paths still execute synchronously during startup and settings commits.
 *
 * @param context Application-scoped [Context] used to resolve the singleton DataStore delegate.
 */
class AppPreferencesDataStore @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    /**
     * Observes the stored language tag once and returns the first emitted value.
     *
     * @return Persisted tag or empty string when unset; never throws under normal DataStore operation.
     */
    fun getLanguageTagSync(): String = runBlocking {
        context.appPreferencesDataStore.data
            .map { prefs -> prefs[KEY_LANGUAGE_TAG].orEmpty() }
            .first()
    }

    /**
     * Atomically writes [languageTag] and waits until the edit transaction completes.
     *
     * @param languageTag BCP-47 tag chosen by the user; empty clears explicit override semantics upstream.
     */
    fun setLanguageTagSync(languageTag: String) {
        runBlocking {
            context.appPreferencesDataStore.edit { prefs ->
                prefs[KEY_LANGUAGE_TAG] = languageTag
            }
        }
    }
}
