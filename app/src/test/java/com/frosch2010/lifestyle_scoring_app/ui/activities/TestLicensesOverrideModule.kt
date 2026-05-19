package com.frosch2010.lifestyle_scoring_app.ui.activities

import com.frosch2010.lifestyle_scoring_app.domain.model.LicenseEntry
import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.LicensesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt test module that replaces [LicensesRepository] with an in-memory implementation returning an empty license list
 * (empty-state UI coverage in activity Robolectric tests).
 */
@Module
@InstallIn(SingletonComponent::class)
object TestLicensesOverrideModule {
    @Provides
    @Singleton
    fun provideLicensesRepository(): LicensesRepository {
        return object : LicensesRepository {
            override fun getLicenses(): Result<List<LicenseEntry>> = Result.success(emptyList())
        }
    }
}
