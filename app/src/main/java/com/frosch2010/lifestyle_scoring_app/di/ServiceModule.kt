package com.frosch2010.lifestyle_scoring_app.di

import com.frosch2010.lifestyle_scoring_app.services.impl.CardRecognizerService
import com.frosch2010.lifestyle_scoring_app.services.impl.PlayerPointsCalculationService
import com.frosch2010.lifestyle_scoring_app.services.interfaces.ICardRecognizerService
import com.frosch2010.lifestyle_scoring_app.services.interfaces.IPlayerPointsCalculationService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt bindings for scoring and vision-sidecar services consumed outside the data layer.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {

    /**
     * @param impl Pure Kotlin rule engine for totals and breakdowns.
     * @return [IPlayerPointsCalculationService] injected into use cases and tests.
     */
    @Binds
    @Singleton
    abstract fun bindPlayerPointsCalculationService(
        impl: PlayerPointsCalculationService
    ): IPlayerPointsCalculationService

    /**
     * @param impl ML Kit-backed OCR matcher over the localized card dictionary.
     * @return [ICardRecognizerService] for camera capture flows.
     */
    @Binds
    @Singleton
    abstract fun bindCardRecognizerService(
        impl: CardRecognizerService
    ): ICardRecognizerService
}
