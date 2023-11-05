package com.frosch2010.lifestyle_scoring_app.di

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * This class is used to define the application class for dependency injection with hilt.
 * @see <a href="https://developer.android.com/training/dependency-injection/hilt-android">Hilt Android</a>
 * @author Jens Münker
 */
@HiltAndroidApp
class ApplicationClass(): Application() {
}