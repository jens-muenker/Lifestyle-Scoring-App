package com.frosch2010.lifestyle_scoring_app.di

import android.app.Application
import com.frosch2010.lifestyle_scoring_app.data.datastore.AppPreferencesDataStore
import com.frosch2010.lifestyle_scoring_app.utils.AppLocale
import dagger.hilt.android.HiltAndroidApp

/**
 * Application entry point enabling Hilt component generation across the process.
 *
 * Re-applies the persisted locale before any activity starts so the first frame already reflects
 * the user's language override rather than default resources briefly flashing on cold start.
 *
 * @see <a href="https://developer.android.com/training/dependency-injection/hilt-android">Hilt Android</a>
 */
@HiltAndroidApp
class ApplicationClass : Application() {
    /**
     * Bootstraps locale configuration from preferences, then delegates to the framework `Application` chain.
     */
    override fun onCreate() {
        super.onCreate()
        val persistedLanguageTag = AppPreferencesDataStore(this).getLanguageTagSync()
        AppLocale.applyLanguageTag(persistedLanguageTag)
    }
}
