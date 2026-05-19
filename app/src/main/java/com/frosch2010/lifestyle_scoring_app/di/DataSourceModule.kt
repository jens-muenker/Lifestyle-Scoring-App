package com.frosch2010.lifestyle_scoring_app.di

import com.frosch2010.lifestyle_scoring_app.data.source.local.interfaces.AppInfoLocalDataSource
import com.frosch2010.lifestyle_scoring_app.data.source.local.interfaces.CardsLocalDataSource
import com.frosch2010.lifestyle_scoring_app.data.source.local.interfaces.LanguageLocalDataSource
import com.frosch2010.lifestyle_scoring_app.data.source.local.interfaces.LicensesLocalDataSource
import com.frosch2010.lifestyle_scoring_app.data.source.local.interfaces.PlayerLocalDataSource
import com.frosch2010.lifestyle_scoring_app.data.source.local.interfaces.PrivacyPolicyLocalDataSource
import com.frosch2010.lifestyle_scoring_app.data.source.local.impl.AppInfoLocalDataSourceImpl
import com.frosch2010.lifestyle_scoring_app.data.source.local.impl.CardsResourceLocalDataSource
import com.frosch2010.lifestyle_scoring_app.data.source.local.impl.InMemoryPlayerLocalDataSource
import com.frosch2010.lifestyle_scoring_app.data.source.local.impl.LanguageLocalDataSourceImpl
import com.frosch2010.lifestyle_scoring_app.data.source.local.impl.LicensesLocalDataSourceImpl
import com.frosch2010.lifestyle_scoring_app.data.source.local.impl.PrivacyPolicyLocalDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt bindings for local data sources that back repositories with platform or asset IO.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    /**
     * @param impl Default package metadata reader.
     * @return [AppInfoLocalDataSource] port used by build-info repositories.
     */
    @Binds
    @Singleton
    abstract fun bindAppInfoLocalDataSource(
        impl: AppInfoLocalDataSourceImpl
    ): AppInfoLocalDataSource

    /**
     * @param impl JSON asset parser for OSS attribution rows.
     * @return [LicensesLocalDataSource] port feeding license repositories.
     */
    @Binds
    @Singleton
    abstract fun bindLicensesLocalDataSource(
        impl: LicensesLocalDataSourceImpl
    ): LicensesLocalDataSource

    /**
     * @param impl Locale-aware raw Markdown loader.
     * @return [PrivacyPolicyLocalDataSource] port for legal content repositories.
     */
    @Binds
    @Singleton
    abstract fun bindPrivacyPolicyLocalDataSource(
        impl: PrivacyPolicyLocalDataSourceImpl
    ): PrivacyPolicyLocalDataSource

    /**
     * @param impl DataStore-backed synchronous language tag persistence.
     * @return [LanguageLocalDataSource] port abstracting preference IO.
     */
    @Binds
    @Singleton
    abstract fun bindLanguageLocalDataSource(
        impl: LanguageLocalDataSourceImpl
    ): LanguageLocalDataSource

    /**
     * @param impl Process-scoped in-memory roster implementation.
     * @return [PlayerLocalDataSource] port for session player state.
     */
    @Binds
    @Singleton
    abstract fun bindPlayerLocalDataSource(
        impl: InMemoryPlayerLocalDataSource
    ): PlayerLocalDataSource

    /**
     * @param impl Resource-backed static card catalog provider.
     * @return [CardsLocalDataSource] port supplying card metadata to repositories.
     */
    @Binds
    @Singleton
    abstract fun bindCardsLocalDataSource(
        impl: CardsResourceLocalDataSource
    ): CardsLocalDataSource
}
