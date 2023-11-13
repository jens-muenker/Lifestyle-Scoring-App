package com.frosch2010.lifestyle_scoring_app.models.repositories

import android.content.Context
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.models.entities.AnimalCard
import com.frosch2010.lifestyle_scoring_app.models.entities.CarCard
import com.frosch2010.lifestyle_scoring_app.models.entities.HouseCard
import com.frosch2010.lifestyle_scoring_app.models.entities.JobCard
import com.frosch2010.lifestyle_scoring_app.models.entities.LoveCard
import com.frosch2010.lifestyle_scoring_app.models.entities.SportCard
import com.frosch2010.lifestyle_scoring_app.models.enums.AnimalTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.CarTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.HouseTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.JobTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.LoveTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.SportTypeEnum
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

/**
 * This class tests the CardsRepository class.
 * @autor Jens Münker
 */
@RunWith(MockitoJUnitRunner::class)
class CardsRepositoryTest {

    @Mock
    lateinit var mockContext: Context

    private lateinit var cardsRepository: CardsRepository

    @Before
    fun setUp() {
        cardsRepository = CardsRepository(mockContext)
    }

    @Test
    fun testGetCardsMap() {
        // Arrange
        `when`(mockContext.getString(R.string.cat)).thenReturn("Cat")
        `when`(mockContext.getString(R.string.cockatoo)).thenReturn("Cockatoo")
        `when`(mockContext.getString(R.string.pony)).thenReturn("Pony")
        `when`(mockContext.getString(R.string.dog)).thenReturn("Dog")

        `when`(mockContext.getString(R.string.motorcycle)).thenReturn("Motorcycle")
        `when`(mockContext.getString(R.string.convertible)).thenReturn("Convertible")
        `when`(mockContext.getString(R.string.vintagecar)).thenReturn("Vintagecar")
        `when`(mockContext.getString(R.string.suv)).thenReturn("SUV")
        `when`(mockContext.getString(R.string.sportscar)).thenReturn("Sportscar")

        `when`(mockContext.getString(R.string.swimming)).thenReturn("Swimming")
        `when`(mockContext.getString(R.string.running)).thenReturn("Running")
        `when`(mockContext.getString(R.string.cycling)).thenReturn("Cycling")
        `when`(mockContext.getString(R.string.rowing)).thenReturn("Rowing")

        `when`(mockContext.getString(R.string.treehouse)).thenReturn("Treehouse")
        `when`(mockContext.getString(R.string.villa)).thenReturn("Villa")
        `when`(mockContext.getString(R.string.beachhouse)).thenReturn("Beachhouse")
        `when`(mockContext.getString(R.string.residentaltower)).thenReturn("Residentaltower")
        `when`(mockContext.getString(R.string.countryhouse)).thenReturn("Countryhouse")
        `when`(mockContext.getString(R.string.manorhouse)).thenReturn("Manorhouse")
        `when`(mockContext.getString(R.string.houseboat)).thenReturn("Houseboat")
        `when`(mockContext.getString(R.string.constructiontrailer)).thenReturn("Constructiontrailer")

        `when`(mockContext.getString(R.string.manager)).thenReturn("Manager")
        `when`(mockContext.getString(R.string.managerin)).thenReturn("Managerin")
        `when`(mockContext.getString(R.string.personaltrainer)).thenReturn("Personaltrainer")
        `when`(mockContext.getString(R.string.carsalesman)).thenReturn("Carsalesman")
        `when`(mockContext.getString(R.string.vet)).thenReturn("Vet")
        `when`(mockContext.getString(R.string.realestateagent)).thenReturn("Realestateagent")

        `when`(mockContext.getString(R.string.liebe)).thenReturn("Liebe")

        // Act
        val cardsMap = cardsRepository.getCardsMap()

        // Assert
        assert(cardsMap.isNotEmpty())

        assert(cardsMap.containsKey("Cat"))
        assert(cardsMap.containsKey("Cockatoo"))
        assert(cardsMap.containsKey("Pony"))
        assert(cardsMap.containsKey("Dog"))

        assert(cardsMap.containsKey("Motorcycle"))
        assert(cardsMap.containsKey("Convertible"))
        assert(cardsMap.containsKey("Vintagecar"))
        assert(cardsMap.containsKey("SUV"))
        assert(cardsMap.containsKey("Sportscar"))

        assert(cardsMap.containsKey("Swimming"))
        assert(cardsMap.containsKey("Running"))
        assert(cardsMap.containsKey("Cycling"))
        assert(cardsMap.containsKey("Rowing"))

        assert(cardsMap.containsKey("Treehouse"))
        assert(cardsMap.containsKey("Villa"))
        assert(cardsMap.containsKey("Beachhouse"))
        assert(cardsMap.containsKey("Residentaltower"))
        assert(cardsMap.containsKey("Countryhouse"))
        assert(cardsMap.containsKey("Manorhouse"))
        assert(cardsMap.containsKey("Houseboat"))
        assert(cardsMap.containsKey("Constructiontrailer"))

        assert(cardsMap.containsKey("Manager"))
        assert(cardsMap.containsKey("Managerin"))
        assert(cardsMap.containsKey("Personaltrainer"))
        assert(cardsMap.containsKey("Carsalesman"))
        assert(cardsMap.containsKey("Vet"))
        assert(cardsMap.containsKey("Realestateagent"))

        assert(cardsMap.containsKey("Liebe"))

        assert(cardsMap["Cat"] is AnimalCard)
        assert(cardsMap["Cockatoo"] is AnimalCard)
        assert(cardsMap["Pony"] is AnimalCard)
        assert(cardsMap["Dog"] is AnimalCard)

        assert(cardsMap["Motorcycle"] is CarCard)
        assert(cardsMap["Convertible"] is CarCard)
        assert(cardsMap["Vintagecar"] is CarCard)
        assert(cardsMap["SUV"] is CarCard)
        assert(cardsMap["Sportscar"] is CarCard)

        assert(cardsMap["Swimming"] is SportCard)
        assert(cardsMap["Running"] is SportCard)
        assert(cardsMap["Cycling"] is SportCard)
        assert(cardsMap["Rowing"] is SportCard)

        assert(cardsMap["Treehouse"] is HouseCard)
        assert(cardsMap["Villa"] is HouseCard)
        assert(cardsMap["Beachhouse"] is HouseCard)
        assert(cardsMap["Residentaltower"] is HouseCard)
        assert(cardsMap["Countryhouse"] is HouseCard)
        assert(cardsMap["Manorhouse"] is HouseCard)
        assert(cardsMap["Houseboat"] is HouseCard)
        assert(cardsMap["Constructiontrailer"] is HouseCard)

        assert(cardsMap["Manager"] is JobCard)
        assert(cardsMap["Managerin"] is JobCard)
        assert(cardsMap["Personaltrainer"] is JobCard)
        assert(cardsMap["Carsalesman"] is JobCard)
        assert(cardsMap["Vet"] is JobCard)
        assert(cardsMap["Realestateagent"] is JobCard)

        assert(cardsMap["Liebe"] is LoveCard)
    }


    @Test
    fun testGetCardName() {
        // Arrange
        `when`(mockContext.getString(R.string.cat)).thenReturn("Cat")
        `when`(mockContext.getString(R.string.cockatoo)).thenReturn("Cockatoo")
        `when`(mockContext.getString(R.string.pony)).thenReturn("Pony")
        `when`(mockContext.getString(R.string.dog)).thenReturn("Dog")

        `when`(mockContext.getString(R.string.motorcycle)).thenReturn("Motorcycle")
        `when`(mockContext.getString(R.string.convertible)).thenReturn("Convertible")
        `when`(mockContext.getString(R.string.vintagecar)).thenReturn("Vintage Car")
        `when`(mockContext.getString(R.string.suv)).thenReturn("SUV")
        `when`(mockContext.getString(R.string.sportscar)).thenReturn("Sports Car")

        `when`(mockContext.getString(R.string.swimming)).thenReturn("Swimming")
        `when`(mockContext.getString(R.string.running)).thenReturn("Running")
        `when`(mockContext.getString(R.string.cycling)).thenReturn("Cycling")
        `when`(mockContext.getString(R.string.rowing)).thenReturn("Rowing")

        `when`(mockContext.getString(R.string.treehouse)).thenReturn("Treehouse")
        `when`(mockContext.getString(R.string.villa)).thenReturn("Villa")
        `when`(mockContext.getString(R.string.beachhouse)).thenReturn("Beach House")
        `when`(mockContext.getString(R.string.residentaltower)).thenReturn("Residential Tower")
        `when`(mockContext.getString(R.string.countryhouse)).thenReturn("Country House")
        `when`(mockContext.getString(R.string.manorhouse)).thenReturn("Manor House")
        `when`(mockContext.getString(R.string.houseboat)).thenReturn("Houseboat")
        `when`(mockContext.getString(R.string.constructiontrailer)).thenReturn("Construction Trailer")

        `when`(mockContext.getString(R.string.manager)).thenReturn("Manager")
        `when`(mockContext.getString(R.string.managerin)).thenReturn("Managerin")
        `when`(mockContext.getString(R.string.personaltrainer)).thenReturn("Personal Trainer")
        `when`(mockContext.getString(R.string.carsalesman)).thenReturn("Car Salesman")
        `when`(mockContext.getString(R.string.vet)).thenReturn("Vet")
        `when`(mockContext.getString(R.string.realestateagent)).thenReturn("Real Estate Agent")

        `when`(mockContext.getString(R.string.liebe)).thenReturn("Liebe")

        // Act
        val animalCat = AnimalCard(CardTypeEnum.ANIMAL, AnimalTypeEnum.CAT)
        val animalCockatoo = AnimalCard(CardTypeEnum.ANIMAL, AnimalTypeEnum.COCKATOO)
        val animalPony = AnimalCard(CardTypeEnum.ANIMAL, AnimalTypeEnum.PONY)
        val animalDog = AnimalCard(CardTypeEnum.ANIMAL, AnimalTypeEnum.DOG)

        val carMotorcycle = CarCard(CardTypeEnum.CAR, CarTypeEnum.MOTORCYCLE, 10)
        val carConvertible = CarCard(CardTypeEnum.CAR, CarTypeEnum.CONVERTIBLE, 10)
        val carVintageCar = CarCard(CardTypeEnum.CAR, CarTypeEnum.VINTAGECAR, 10)
        val carSUV = CarCard(CardTypeEnum.CAR, CarTypeEnum.SUV, 10)
        val carSportsCar = CarCard(CardTypeEnum.CAR, CarTypeEnum.SPORTSCAR, 10)

        val sportSwimming = SportCard(CardTypeEnum.SPORT, SportTypeEnum.SWIMMING)
        val sportRunning = SportCard(CardTypeEnum.SPORT, SportTypeEnum.RUNNING)
        val sportCycling = SportCard(CardTypeEnum.SPORT, SportTypeEnum.CYCLING)
        val sportRowing = SportCard(CardTypeEnum.SPORT, SportTypeEnum.ROWING)

        val houseTreehouse = HouseCard(CardTypeEnum.HOUSE, HouseTypeEnum.TREEHOUSE)
        val houseVilla = HouseCard(CardTypeEnum.HOUSE, HouseTypeEnum.VILLA)
        val houseBeachHouse = HouseCard(CardTypeEnum.HOUSE, HouseTypeEnum.BEACHHOUSE)
        val houseResidentialTower = HouseCard(CardTypeEnum.HOUSE, HouseTypeEnum.RESIDENTALTOWER)
        val houseCountryHouse = HouseCard(CardTypeEnum.HOUSE, HouseTypeEnum.COUNTRYHOUSE)
        val houseManorHouse = HouseCard(CardTypeEnum.HOUSE, HouseTypeEnum.MANORHOUSE)
        val houseHouseboat = HouseCard(CardTypeEnum.HOUSE, HouseTypeEnum.HOUSEBOAT)
        val houseConstructionTrailer = HouseCard(CardTypeEnum.HOUSE, HouseTypeEnum.CONSTRUCTIONTRAILER)

        val jobManager = JobCard(CardTypeEnum.JOB, JobTypeEnum.MANAGER)
        val jobManagerin = JobCard(CardTypeEnum.JOB, JobTypeEnum.MANAGERIN)
        val jobPersonalTrainer = JobCard(CardTypeEnum.JOB, JobTypeEnum.PERSONALTRAINER)
        val jobCarSalesman = JobCard(CardTypeEnum.JOB, JobTypeEnum.CARSALESMAN)
        val jobVet = JobCard(CardTypeEnum.JOB, JobTypeEnum.VET)
        val jobRealEstateAgent = JobCard(CardTypeEnum.JOB, JobTypeEnum.REALESTATEAGENT)

        val loveUnknown = LoveCard(CardTypeEnum.LOVE, LoveTypeEnum.UNKNOWN)

        // Assert
        assert(cardsRepository.getCardName(animalCat) == "Cat")
        assert(cardsRepository.getCardName(animalCockatoo) == "Cockatoo")
        assert(cardsRepository.getCardName(animalPony) == "Pony")
        assert(cardsRepository.getCardName(animalDog) == "Dog")

        assert(cardsRepository.getCardName(carMotorcycle) == "Motorcycle")
        assert(cardsRepository.getCardName(carConvertible) == "Convertible")
        assert(cardsRepository.getCardName(carVintageCar) == "Vintage Car")
        assert(cardsRepository.getCardName(carSUV) == "SUV")
        assert(cardsRepository.getCardName(carSportsCar) == "Sports Car")

        assert(cardsRepository.getCardName(sportSwimming) == "Swimming")
        assert(cardsRepository.getCardName(sportRunning) == "Running")
        assert(cardsRepository.getCardName(sportCycling) == "Cycling")
        assert(cardsRepository.getCardName(sportRowing) == "Rowing")

        assert(cardsRepository.getCardName(houseTreehouse) == "Treehouse")
        assert(cardsRepository.getCardName(houseVilla) == "Villa")
        assert(cardsRepository.getCardName(houseBeachHouse) == "Beach House")
        assert(cardsRepository.getCardName(houseResidentialTower) == "Residential Tower")
        assert(cardsRepository.getCardName(houseCountryHouse) == "Country House")
        assert(cardsRepository.getCardName(houseManorHouse) == "Manor House")
        assert(cardsRepository.getCardName(houseHouseboat) == "Houseboat")
        assert(cardsRepository.getCardName(houseConstructionTrailer) == "Construction Trailer")

        assert(cardsRepository.getCardName(jobManager) == "Manager")
        assert(cardsRepository.getCardName(jobManagerin) == "Managerin")
        assert(cardsRepository.getCardName(jobPersonalTrainer) == "Personal Trainer")
        assert(cardsRepository.getCardName(jobCarSalesman) == "Car Salesman")
        assert(cardsRepository.getCardName(jobVet) == "Vet")
        assert(cardsRepository.getCardName(jobRealEstateAgent) == "Real Estate Agent")

        assert(cardsRepository.getCardName(loveUnknown) == "Liebe")
    }


    @Test
    fun testGetCarCardPoints() {
        // Arrange
        val motorcycleCard = CarCard(CardTypeEnum.CAR, CarTypeEnum.MOTORCYCLE, 10)
        val convertibleCard = CarCard(CardTypeEnum.CAR, CarTypeEnum.CONVERTIBLE, 20)
        val vintageCarCard = CarCard(CardTypeEnum.CAR, CarTypeEnum.VINTAGECAR, 30)
        val suvCard = CarCard(CardTypeEnum.CAR, CarTypeEnum.SUV, 40)
        val sportsCarCard = CarCard(CardTypeEnum.CAR, CarTypeEnum.SPORTSCAR, 50)

        // Act
        val motorcyclePoints = cardsRepository.getCarCardPoints(motorcycleCard)
        val convertiblePoints = cardsRepository.getCarCardPoints(convertibleCard)
        val vintageCarPoints = cardsRepository.getCarCardPoints(vintageCarCard)
        val suvPoints = cardsRepository.getCarCardPoints(suvCard)
        val sportsCarPoints = cardsRepository.getCarCardPoints(sportsCarCard)

        // Assert
        assert(motorcyclePoints == listOf(1, 2, 3))
        assert(convertiblePoints == listOf(4, 5, 6))
        assert(vintageCarPoints == listOf(7, 8, 9))
        assert(suvPoints == listOf(10, 11, 12))
        assert(sportsCarPoints == listOf(13, 14))
    }


    @Test
    fun testGetLoveCardTypes() {
        // Arrange
        val animalLove = "Animal Love"
        val houseLove = "House Love"
        val jobLove = "Job Love"
        val carLove = "Car Love"

        `when`(mockContext.getString(R.string.animal_love)).thenReturn(animalLove)
        `when`(mockContext.getString(R.string.realestate_love)).thenReturn(houseLove)
        `when`(mockContext.getString(R.string.job_love)).thenReturn(jobLove)
        `when`(mockContext.getString(R.string.car_love)).thenReturn(carLove)

        // Act
        val result = cardsRepository.getLoveCardTypes()

        // Assert
        assert(result.size == 4)
        assert(result.contains(animalLove))
        assert(result.contains(houseLove))
        assert(result.contains(jobLove))
        assert(result.contains(carLove))
    }
}

