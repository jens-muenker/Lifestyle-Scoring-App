package com.frosch2010.lifestyle_scoring_app.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * This class is used to define the modules for dependency injection with hilt.
 * @see <a href="https://developer.android.com/training/dependency-injection/hilt-android">Hilt Android</a>
 * @author Jens Münker
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
}