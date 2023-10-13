package com.frosch2010.lifestyle_scoring_app.di

import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICardsRepository
import com.frosch2010.lifestyle_scoring_app.models.repositories.CardsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ModulesClass {

    @Binds
    @Singleton
    abstract fun bindICardsRepository(
        cardsRepository: CardsRepository
    ): ICardsRepository


}