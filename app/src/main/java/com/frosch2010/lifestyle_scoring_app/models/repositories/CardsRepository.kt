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
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICardsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CardsRepository @Inject constructor(@ApplicationContext val context: Context): ICardsRepository {
    override fun getCardsMap(): Map<String, ICard> {
        return mapOf(
            context.getString(R.string.cat) to AnimalCard(CardTypeEnum.ANIMAL, AnimalTypeEnum.CAT),
            context.getString(R.string.cockatoo) to AnimalCard(CardTypeEnum.ANIMAL, AnimalTypeEnum.COCKATOO),
            context.getString(R.string.pony) to AnimalCard(CardTypeEnum.ANIMAL, AnimalTypeEnum.PONY),
            context.getString(R.string.dog) to AnimalCard(CardTypeEnum.ANIMAL, AnimalTypeEnum.DOG),

            context.getString(R.string.motorcycle) to CarCard(CardTypeEnum.CAR, CarTypeEnum.MOTORCYCLE, 1),
            context.getString(R.string.motorcycle) to CarCard(CardTypeEnum.CAR, CarTypeEnum.MOTORCYCLE, 2),
            context.getString(R.string.motorcycle) to CarCard(CardTypeEnum.CAR, CarTypeEnum.MOTORCYCLE, 3),

            context.getString(R.string.convertible) to CarCard(CardTypeEnum.CAR, CarTypeEnum.CONVERTIBLE, 4),
            context.getString(R.string.convertible) to CarCard(CardTypeEnum.CAR, CarTypeEnum.CONVERTIBLE, 5),
            context.getString(R.string.convertible) to CarCard(CardTypeEnum.CAR, CarTypeEnum.CONVERTIBLE, 6),

            context.getString(R.string.vintagecar) to CarCard(CardTypeEnum.CAR, CarTypeEnum.VINTAGECAR, 7),
            context.getString(R.string.vintagecar) to CarCard(CardTypeEnum.CAR, CarTypeEnum.VINTAGECAR, 8),
            context.getString(R.string.vintagecar) to CarCard(CardTypeEnum.CAR, CarTypeEnum.VINTAGECAR, 9),

            context.getString(R.string.suv) to CarCard(CardTypeEnum.CAR, CarTypeEnum.SUV, 10),
            context.getString(R.string.suv) to CarCard(CardTypeEnum.CAR, CarTypeEnum.SUV, 11),
            context.getString(R.string.suv) to CarCard(CardTypeEnum.CAR, CarTypeEnum.SUV, 12),

            context.getString(R.string.sportscar) to CarCard(CardTypeEnum.CAR, CarTypeEnum.SPORTSCAR, 13),
            context.getString(R.string.sportscar) to CarCard(CardTypeEnum.CAR, CarTypeEnum.SPORTSCAR, 14),

            context.getString(R.string.swimming) to SportCard(CardTypeEnum.SPORT, SportTypeEnum.SWIMMING),
            context.getString(R.string.running) to SportCard(CardTypeEnum.SPORT, SportTypeEnum.RUNNING),
            context.getString(R.string.cycling) to SportCard(CardTypeEnum.SPORT, SportTypeEnum.CYCLING),
            context.getString(R.string.rowing) to SportCard(CardTypeEnum.SPORT, SportTypeEnum.ROWING),

            context.getString(R.string.treehouse) to HouseCard(CardTypeEnum.HOUSE, HouseTypeEnum.TREEHOUSE),
            context.getString(R.string.villa) to HouseCard(CardTypeEnum.HOUSE, HouseTypeEnum.VILLA),
            context.getString(R.string.beachhouse) to HouseCard(CardTypeEnum.HOUSE, HouseTypeEnum.BEACHHOUSE),
            context.getString(R.string.residentaltower) to HouseCard(CardTypeEnum.HOUSE, HouseTypeEnum.RESIDENTALTOWER),
            context.getString(R.string.countryhouse) to HouseCard(CardTypeEnum.HOUSE, HouseTypeEnum.COUNTRYHOUSE),
            context.getString(R.string.manorhouse) to HouseCard(CardTypeEnum.HOUSE, HouseTypeEnum.MANORHOUSE),
            context.getString(R.string.houseboat) to HouseCard(CardTypeEnum.HOUSE, HouseTypeEnum.HOUSEBOAT),
            context.getString(R.string.constructiontrailer) to HouseCard(CardTypeEnum.HOUSE, HouseTypeEnum.CONSTRUCTIONTRAILER),

            context.getString(R.string.manager) to JobCard(CardTypeEnum.JOB, JobTypeEnum.MANAGER),
            context.getString(R.string.managerin) to JobCard(CardTypeEnum.JOB, JobTypeEnum.MANAGERIN),
            context.getString(R.string.personaltrainer) to JobCard(CardTypeEnum.JOB, JobTypeEnum.PERSONALTRAINER),
            context.getString(R.string.carsalesman) to JobCard(CardTypeEnum.JOB, JobTypeEnum.CARSALESMAN),
            context.getString(R.string.vet) to JobCard(CardTypeEnum.JOB, JobTypeEnum.VET),
            context.getString(R.string.realestateagent) to JobCard(CardTypeEnum.JOB, JobTypeEnum.REALESTATEAGENT),

            context.getString(R.string.liebe) to LoveCard(CardTypeEnum.LOVE, LoveTypeEnum.UNKNOWN)
        )
    }

    override fun getCardName(card: ICard): String {
        when(card.cardType){

            CardTypeEnum.LOVE -> return when((card as LoveCard).loveType){
                LoveTypeEnum.ANIMAL -> context.getString(R.string.animal_love)
                LoveTypeEnum.HOUSE -> context.getString(R.string.realestate_love)
                LoveTypeEnum.JOB -> context.getString(R.string.job_love)
                LoveTypeEnum.CAR -> context.getString(R.string.car_love)
                LoveTypeEnum.UNKNOWN -> context.getString(R.string.liebe)
            }

            CardTypeEnum.JOB -> return when((card as JobCard).jobType){
                JobTypeEnum.MANAGER -> context.getString(R.string.manager)
                JobTypeEnum.MANAGERIN -> context.getString(R.string.managerin)
                JobTypeEnum.PERSONALTRAINER -> context.getString(R.string.personaltrainer)
                JobTypeEnum.CARSALESMAN -> context.getString(R.string.carsalesman)
                JobTypeEnum.VET -> context.getString(R.string.vet)
                JobTypeEnum.REALESTATEAGENT -> context.getString(R.string.realestateagent)
            }

            CardTypeEnum.CAR -> return when((card as CarCard).carType) {
                CarTypeEnum.MOTORCYCLE -> context.getString(R.string.motorcycle)
                CarTypeEnum.CONVERTIBLE -> context.getString(R.string.convertible)
                CarTypeEnum.VINTAGECAR -> context.getString(R.string.vintagecar)
                CarTypeEnum.SUV -> context.getString(R.string.suv)
                CarTypeEnum.SPORTSCAR -> context.getString(R.string.sportscar)
            }

            CardTypeEnum.HOUSE -> return when((card as HouseCard).houseType) {
                HouseTypeEnum.TREEHOUSE -> context.getString(R.string.treehouse)
                HouseTypeEnum.VILLA -> context.getString(R.string.villa)
                HouseTypeEnum.BEACHHOUSE -> context.getString(R.string.beachhouse)
                HouseTypeEnum.RESIDENTALTOWER -> context.getString(R.string.residentaltower)
                HouseTypeEnum.COUNTRYHOUSE -> context.getString(R.string.countryhouse)
                HouseTypeEnum.MANORHOUSE -> context.getString(R.string.manorhouse)
                HouseTypeEnum.HOUSEBOAT -> context.getString(R.string.houseboat)
                HouseTypeEnum.CONSTRUCTIONTRAILER -> context.getString(R.string.constructiontrailer)
            }

            CardTypeEnum.ANIMAL -> return when((card as AnimalCard).animalType) {
                AnimalTypeEnum.CAT -> context.getString(R.string.cat)
                AnimalTypeEnum.COCKATOO -> context.getString(R.string.cockatoo)
                AnimalTypeEnum.PONY -> context.getString(R.string.pony)
                AnimalTypeEnum.DOG -> context.getString(R.string.dog)
            }

            CardTypeEnum.SPORT -> return when((card as SportCard).sportType) {
                SportTypeEnum.SWIMMING -> context.getString(R.string.swimming)
                SportTypeEnum.RUNNING -> context.getString(R.string.running)
                SportTypeEnum.CYCLING -> context.getString(R.string.cycling)
                SportTypeEnum.ROWING -> context.getString(R.string.rowing)
            }
        }
    }

    override fun getCarCardPoints(card: ICard): List<Int> {
        return when((card as CarCard).carType){
            CarTypeEnum.MOTORCYCLE -> listOf(1, 2, 3)
            CarTypeEnum.CONVERTIBLE -> listOf(4, 5, 6)
            CarTypeEnum.VINTAGECAR -> listOf(7, 8, 9)
            CarTypeEnum.SUV -> listOf(10, 11, 12)
            CarTypeEnum.SPORTSCAR -> listOf(13, 14)
        }
    }

    override fun getLoveCardTypes(): List<String> {
        return listOf(
            context.getString(R.string.animal_love),
            context.getString(R.string.realestate_love),
            context.getString(R.string.job_love),
            context.getString(R.string.car_love)
        )
    }
}