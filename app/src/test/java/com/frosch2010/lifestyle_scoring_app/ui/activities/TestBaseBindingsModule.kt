package com.frosch2010.lifestyle_scoring_app.ui.activities

import com.frosch2010.lifestyle_scoring_app.data.repository.impl.AppInfoRepositoryImpl
import com.frosch2010.lifestyle_scoring_app.data.repository.impl.CardsRepositoryImpl
import com.frosch2010.lifestyle_scoring_app.data.repository.impl.InMemoryPlayerRepository
import com.frosch2010.lifestyle_scoring_app.data.repository.impl.LanguageRepositoryImpl
import com.frosch2010.lifestyle_scoring_app.data.repository.impl.PrivacyPolicyRepositoryImpl
import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.AppInfoRepository
import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.CardsRepository
import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.LanguageRepository
import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.PlayerRepository
import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.PrivacyPolicyRepository
import com.frosch2010.lifestyle_scoring_app.data.source.local.impl.AppInfoLocalDataSourceImpl
import com.frosch2010.lifestyle_scoring_app.data.source.local.impl.CardsResourceLocalDataSource
import com.frosch2010.lifestyle_scoring_app.data.source.local.impl.InMemoryPlayerLocalDataSource
import com.frosch2010.lifestyle_scoring_app.data.source.local.impl.LanguageLocalDataSourceImpl
import com.frosch2010.lifestyle_scoring_app.data.source.local.impl.PrivacyPolicyLocalDataSourceImpl
import com.frosch2010.lifestyle_scoring_app.data.source.local.interfaces.AppInfoLocalDataSource
import com.frosch2010.lifestyle_scoring_app.data.source.local.interfaces.CardsLocalDataSource
import com.frosch2010.lifestyle_scoring_app.data.source.local.interfaces.LanguageLocalDataSource
import com.frosch2010.lifestyle_scoring_app.data.source.local.interfaces.PlayerLocalDataSource
import com.frosch2010.lifestyle_scoring_app.data.source.local.interfaces.PrivacyPolicyLocalDataSource
import com.frosch2010.lifestyle_scoring_app.services.impl.PlayerPointsCalculationService
import com.frosch2010.lifestyle_scoring_app.services.interfaces.ICardRecognizerService
import com.frosch2010.lifestyle_scoring_app.services.interfaces.IPlayerPointsCalculationService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt test bindings wiring default in-memory and resource-backed data sources plus [FakeCardRecognizerService] for
 * Robolectric activity tests under `ui.activities`.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class TestBaseBindingsModule {
    @Binds
    @Singleton
    abstract fun bindCardsLocalDataSource(impl: CardsResourceLocalDataSource): CardsLocalDataSource

    @Binds
    @Singleton
    abstract fun bindPlayerLocalDataSource(impl: InMemoryPlayerLocalDataSource): PlayerLocalDataSource

    @Binds
    @Singleton
    abstract fun bindAppInfoLocalDataSource(impl: AppInfoLocalDataSourceImpl): AppInfoLocalDataSource

    @Binds
    @Singleton
    abstract fun bindLanguageLocalDataSource(impl: LanguageLocalDataSourceImpl): LanguageLocalDataSource

    @Binds
    @Singleton
    abstract fun bindPrivacyPolicyLocalDataSource(impl: PrivacyPolicyLocalDataSourceImpl): PrivacyPolicyLocalDataSource

    @Binds
    @Singleton
    abstract fun bindCardsRepository(impl: CardsRepositoryImpl): CardsRepository

    @Binds
    @Singleton
    abstract fun bindPlayerRepository(impl: InMemoryPlayerRepository): PlayerRepository

    @Binds
    @Singleton
    abstract fun bindIPlayerPointsCalculationService(impl: PlayerPointsCalculationService): IPlayerPointsCalculationService

    @Binds
    @Singleton
    abstract fun bindICardRecognizerService(impl: FakeCardRecognizerService): ICardRecognizerService

    @Binds
    @Singleton
    abstract fun bindAppInfoRepository(impl: AppInfoRepositoryImpl): AppInfoRepository

    @Binds
    @Singleton
    abstract fun bindLanguageRepository(impl: LanguageRepositoryImpl): LanguageRepository

    @Binds
    @Singleton
    abstract fun bindPrivacyPolicyRepository(impl: PrivacyPolicyRepositoryImpl): PrivacyPolicyRepository
}
