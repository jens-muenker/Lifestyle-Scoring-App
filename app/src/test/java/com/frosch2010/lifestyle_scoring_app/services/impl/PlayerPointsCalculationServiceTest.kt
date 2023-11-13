package com.frosch2010.lifestyle_scoring_app.services.impl

import com.frosch2010.lifestyle_scoring_app.models.entities.AnimalCard
import com.frosch2010.lifestyle_scoring_app.models.entities.CarCard
import com.frosch2010.lifestyle_scoring_app.models.entities.HouseCard
import com.frosch2010.lifestyle_scoring_app.models.entities.JobCard
import com.frosch2010.lifestyle_scoring_app.models.entities.LoveCard
import com.frosch2010.lifestyle_scoring_app.models.enums.AnimalTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.CarTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.HouseTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.JobTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.LoveTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import com.frosch2010.lifestyle_scoring_app.services.interfaces.IPlayerPointsCalculationService
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class PlayerPointsCalculationServiceTest(private val cards: List<ICard>, private val expectedPoints: Int) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> {
            return listOf(
                // Test Case 1: Cards with different types and love cards
                arrayOf(
                    listOf(
                        AnimalCard(CardTypeEnum.ANIMAL, AnimalTypeEnum.CAT),
                        CarCard(CardTypeEnum.CAR, CarTypeEnum.MOTORCYCLE, 5),
                        HouseCard(CardTypeEnum.HOUSE, HouseTypeEnum.VILLA),
                        JobCard(CardTypeEnum.JOB, JobTypeEnum.MANAGER),
                        LoveCard(CardTypeEnum.LOVE, LoveTypeEnum.ANIMAL),
                        LoveCard(CardTypeEnum.LOVE, LoveTypeEnum.HOUSE)
                    ),
                    7
                ),

                // Test Case 2: Empty cards list
                arrayOf(emptyList<ICard>(), 0),

                // Test Case 3: Only animal cards
                arrayOf(
                    listOf(
                        AnimalCard(CardTypeEnum.ANIMAL, AnimalTypeEnum.CAT),
                        AnimalCard(CardTypeEnum.ANIMAL, AnimalTypeEnum.DOG),
                        AnimalCard(CardTypeEnum.ANIMAL, AnimalTypeEnum.PONY)
                    ),
                    10
                ),

                // Test Case 4: Only love cards
                arrayOf(
                    listOf(
                        LoveCard(CardTypeEnum.LOVE, LoveTypeEnum.ANIMAL),
                        LoveCard(CardTypeEnum.LOVE, LoveTypeEnum.HOUSE),
                        LoveCard(CardTypeEnum.LOVE, LoveTypeEnum.JOB),
                        LoveCard(CardTypeEnum.LOVE, LoveTypeEnum.CAR)
                    ),
                    0
                ),

                // Test Case 5: No love cards
                arrayOf(
                    listOf(
                        AnimalCard(CardTypeEnum.ANIMAL, AnimalTypeEnum.CAT),
                        CarCard(CardTypeEnum.CAR, CarTypeEnum.MOTORCYCLE, 5),
                        HouseCard(CardTypeEnum.HOUSE, HouseTypeEnum.VILLA),
                        JobCard(CardTypeEnum.JOB, JobTypeEnum.MANAGER)
                    ),
                    6
                ),

                // Test Case 6: Only car cards
                arrayOf(
                    listOf(
                        CarCard(CardTypeEnum.CAR, CarTypeEnum.MOTORCYCLE, 5),
                        CarCard(CardTypeEnum.CAR, CarTypeEnum.CONVERTIBLE, 5),
                        CarCard(CardTypeEnum.CAR, CarTypeEnum.VINTAGECAR, 5),
                        CarCard(CardTypeEnum.CAR, CarTypeEnum.SUV, 5),
                        CarCard(CardTypeEnum.CAR, CarTypeEnum.SPORTSCAR, 5)
                    ),
                    25
                ),

                // Test Case 7: Only house cards
                arrayOf(
                    listOf(
                        HouseCard(CardTypeEnum.HOUSE, HouseTypeEnum.TREEHOUSE),
                        HouseCard(CardTypeEnum.HOUSE, HouseTypeEnum.VILLA),
                        HouseCard(CardTypeEnum.HOUSE, HouseTypeEnum.BEACHHOUSE),
                        HouseCard(CardTypeEnum.HOUSE, HouseTypeEnum.RESIDENTALTOWER),
                        HouseCard(CardTypeEnum.HOUSE, HouseTypeEnum.COUNTRYHOUSE),
                        HouseCard(CardTypeEnum.HOUSE, HouseTypeEnum.MANORHOUSE),
                        HouseCard(CardTypeEnum.HOUSE, HouseTypeEnum.HOUSEBOAT),
                        HouseCard(CardTypeEnum.HOUSE, HouseTypeEnum.CONSTRUCTIONTRAILER)
                    ),
                    64
                ),

                // Test Case 8: Only job cards
                arrayOf(
                    listOf(
                        JobCard(CardTypeEnum.JOB, JobTypeEnum.MANAGER),
                        JobCard(CardTypeEnum.JOB, JobTypeEnum.MANAGERIN),
                        JobCard(CardTypeEnum.JOB, JobTypeEnum.PERSONALTRAINER),
                        JobCard(CardTypeEnum.JOB, JobTypeEnum.CARSALESMAN),
                        JobCard(CardTypeEnum.JOB, JobTypeEnum.VET),
                        JobCard(CardTypeEnum.JOB, JobTypeEnum.REALESTATEAGENT)
                    ),
                    0
                ),

                // Test Case 9: Only love cards
                arrayOf(
                    listOf(
                        LoveCard(CardTypeEnum.LOVE, LoveTypeEnum.ANIMAL),
                        LoveCard(CardTypeEnum.LOVE, LoveTypeEnum.HOUSE),
                        LoveCard(CardTypeEnum.LOVE, LoveTypeEnum.JOB),
                        LoveCard(CardTypeEnum.LOVE, LoveTypeEnum.CAR)
                    ),
                    0
                )
            )
        }
    }

    private lateinit var pointsCalculationService: IPlayerPointsCalculationService

    @Before
    fun setUp() {
        pointsCalculationService = PlayerPointsCalculationService()
    }

    @Test
    fun testCalculatePlayerPoints() {
        // Act
        val calculatedPoints = pointsCalculationService.calculatePlayerPoints(cards)

        // Assert
        assertEquals(expectedPoints, calculatedPoints)
    }
}
