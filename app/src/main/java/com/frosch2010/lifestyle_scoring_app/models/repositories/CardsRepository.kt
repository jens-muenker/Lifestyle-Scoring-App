package com.frosch2010.lifestyle_scoring_app.models.repositories

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
import javax.inject.Inject

class CardsRepository @Inject constructor(): ICardsRepository {
    override fun getCardsMap(): Map<String, ICard> {
        return mapOf(
            "KATZE" to AnimalCard(CardTypeEnum.ANIMAL, AnimalTypeEnum.CAT),
            "KAKADU" to AnimalCard(CardTypeEnum.ANIMAL, AnimalTypeEnum.COCKATOO),
            "PONY" to AnimalCard(CardTypeEnum.ANIMAL, AnimalTypeEnum.PONY),
            "HUND" to AnimalCard(CardTypeEnum.ANIMAL, AnimalTypeEnum.DOG),

            "ROLLER" to CarCard(CardTypeEnum.CAR, CarTypeEnum.MOTORCYCLE, 1),
            "ROLLER" to CarCard(CardTypeEnum.CAR, CarTypeEnum.MOTORCYCLE, 2),
            "ROLLER" to CarCard(CardTypeEnum.CAR, CarTypeEnum.MOTORCYCLE, 3),

            "CABRIO" to CarCard(CardTypeEnum.CAR, CarTypeEnum.CONVERTIBLE, 4),
            "CABRIO" to CarCard(CardTypeEnum.CAR, CarTypeEnum.CONVERTIBLE, 5),
            "CABRIO" to CarCard(CardTypeEnum.CAR, CarTypeEnum.CONVERTIBLE, 6),

            "OLDTIMER" to CarCard(CardTypeEnum.CAR, CarTypeEnum.VINTAGECAR, 7),
            "OLDTIMER" to CarCard(CardTypeEnum.CAR, CarTypeEnum.VINTAGECAR, 8),
            "OLDTIMER" to CarCard(CardTypeEnum.CAR, CarTypeEnum.VINTAGECAR, 9),

            "SUV" to CarCard(CardTypeEnum.CAR, CarTypeEnum.SUV, 10),
            "SUV" to CarCard(CardTypeEnum.CAR, CarTypeEnum.SUV, 11),
            "SUV" to CarCard(CardTypeEnum.CAR, CarTypeEnum.SUV, 12),

            "SPORTWAGEN" to CarCard(CardTypeEnum.CAR, CarTypeEnum.SPORTSCAR, 13),
            "SPORTWAGEN" to CarCard(CardTypeEnum.CAR, CarTypeEnum.SPORTSCAR, 14),

            "SCHWIMMEN" to SportCard(CardTypeEnum.SPORT, SportTypeEnum.SWIMMING),
            "LAUFEN" to SportCard(CardTypeEnum.SPORT, SportTypeEnum.RUNNING),
            "RADFAHREN" to SportCard(CardTypeEnum.SPORT, SportTypeEnum.CYCLING),
            "RUDERN" to SportCard(CardTypeEnum.SPORT, SportTypeEnum.ROWING),

            "BAUMHAUS" to HouseCard(CardTypeEnum.HOUSE, HouseTypeEnum.TREEHOUSE),
            "STADTVILLA" to HouseCard(CardTypeEnum.HOUSE, HouseTypeEnum.VILLA),
            "STRANDHAUS" to HouseCard(CardTypeEnum.HOUSE, HouseTypeEnum.BEACHHOUSE),
            "WOHNTURM" to HouseCard(CardTypeEnum.HOUSE, HouseTypeEnum.RESIDENTALTOWER),
            "LANDHAUS" to HouseCard(CardTypeEnum.HOUSE, HouseTypeEnum.COUNTRYHOUSE),
            "HERRENHAUS" to HouseCard(CardTypeEnum.HOUSE, HouseTypeEnum.MANORHOUSE),
            "HAUSBOOT" to HouseCard(CardTypeEnum.HOUSE, HouseTypeEnum.HOUSEBOAT),
            "BAUWAGEN" to HouseCard(CardTypeEnum.HOUSE, HouseTypeEnum.CONSTRUCTIONTRAILER),

            "MANAGER" to JobCard(CardTypeEnum.JOB, JobTypeEnum.MANAGER),
            "MANAGERIN" to JobCard(CardTypeEnum.JOB, JobTypeEnum.MANAGER),
            "TRAINERIN" to JobCard(CardTypeEnum.JOB, JobTypeEnum.PERSONALTRAINER),
            "AUTOHÄNDLER" to JobCard(CardTypeEnum.JOB, JobTypeEnum.CARSALESMAN),
            "TIERÄRZTIN" to JobCard(CardTypeEnum.JOB, JobTypeEnum.VET),
            "MAKLER" to JobCard(CardTypeEnum.JOB, JobTypeEnum.REALESTATEAGENT),

            "LIEBE" to LoveCard(CardTypeEnum.LOVE, LoveTypeEnum.UNKNOWN)
        )
    }
}