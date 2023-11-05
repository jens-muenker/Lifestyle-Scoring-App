package com.frosch2010.lifestyle_scoring_app.di

import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICardsRepository
import com.frosch2010.lifestyle_scoring_app.models.interfaces.IPlayerRepository
import com.frosch2010.lifestyle_scoring_app.models.repositories.CardsRepository
import com.frosch2010.lifestyle_scoring_app.models.repositories.PlayerRepository
import com.frosch2010.lifestyle_scoring_app.services.impl.PlayerPointsCalculationService
import com.frosch2010.lifestyle_scoring_app.services.interfaces.IPlayerPointsCalculationService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * This class is used to define the modules for dependency injection with hilt.
 * @see <a href="https://developer.android.com/training/dependency-injection/hilt-android">Hilt Android</a>
 * @author Jens Münker
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class ModulesClass {

    @Binds
    @Singleton
    abstract fun bindICardsRepository(
        cardsRepository: CardsRepository
    ): ICardsRepository

    @Binds
    @Singleton
    abstract fun bindIPlayerRepository(
        playerRepository: PlayerRepository
    ): IPlayerRepository

    @Binds
    @Singleton
    abstract fun bindIPlayerPointsCalculationService(
        playerPointsCalculationService: PlayerPointsCalculationService
    ): IPlayerPointsCalculationService
}