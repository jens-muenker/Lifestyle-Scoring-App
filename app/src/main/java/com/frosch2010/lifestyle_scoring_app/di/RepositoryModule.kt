package com.frosch2010.lifestyle_scoring_app.di

import com.frosch2010.lifestyle_scoring_app.data.repository.impl.AppInfoRepositoryImpl
import com.frosch2010.lifestyle_scoring_app.data.repository.impl.CardsRepositoryImpl
import com.frosch2010.lifestyle_scoring_app.data.repository.impl.InMemoryPlayerRepository
import com.frosch2010.lifestyle_scoring_app.data.repository.impl.LanguageRepositoryImpl
import com.frosch2010.lifestyle_scoring_app.data.repository.impl.LicensesRepositoryImpl
import com.frosch2010.lifestyle_scoring_app.data.repository.impl.PrivacyPolicyRepositoryImpl
import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.AppInfoRepository
import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.CardsRepository
import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.LanguageRepository
import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.LicensesRepository
import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.PlayerRepository
import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.PrivacyPolicyRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt bindings mapping domain repository ports to their default data-layer implementations.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * @param impl Resource-delegating card catalog repository.
     * @return [CardsRepository] exposed to use cases and ViewModels.
     */
    @Binds
    @Singleton
    abstract fun bindCardsRepository(
        impl: CardsRepositoryImpl
    ): CardsRepository

    /**
     * @param impl In-memory player roster repository.
     * @return [PlayerRepository] for session-scoped player mutations.
     */
    @Binds
    @Singleton
    abstract fun bindPlayerRepository(
        impl: InMemoryPlayerRepository
    ): PlayerRepository

    /**
     * @param impl Package-manager-backed build metadata repository.
     * @return [AppInfoRepository] for version surfaces.
     */
    @Binds
    @Singleton
    abstract fun bindAppInfoRepository(
        impl: AppInfoRepositoryImpl
    ): AppInfoRepository

    /**
     * @param impl Language persistence plus immediate locale application.
     * @return [LanguageRepository] for settings flows.
     */
    @Binds
    @Singleton
    abstract fun bindLanguageRepository(
        impl: LanguageRepositoryImpl
    ): LanguageRepository

    /**
     * @param impl Raw-resource Markdown loader repository.
     * @return [PrivacyPolicyRepository] for legal screens.
     */
    @Binds
    @Singleton
    abstract fun bindPrivacyPolicyRepository(
        impl: PrivacyPolicyRepositoryImpl
    ): PrivacyPolicyRepository

    /**
     * @param impl Asset-backed OSS attribution repository with soft-degradation rules.
     * @return [LicensesRepository] for licenses activity adapters.
     */
    @Binds
    @Singleton
    abstract fun bindLicensesRepository(
        impl: LicensesRepositoryImpl
    ): LicensesRepository
}
