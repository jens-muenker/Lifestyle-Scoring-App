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
            context.getString(R.string.kakadu) to AnimalCard(CardTypeEnum.ANIMAL, AnimalTypeEnum.COCKATOO),
            context.getString(R.string.pony) to AnimalCard(CardTypeEnum.ANIMAL, AnimalTypeEnum.PONY),
            context.getString(R.string.hund) to AnimalCard(CardTypeEnum.ANIMAL, AnimalTypeEnum.DOG),

            context.getString(R.string.roller) to CarCard(CardTypeEnum.CAR, CarTypeEnum.MOTORCYCLE, 1),
            context.getString(R.string.roller) to CarCard(CardTypeEnum.CAR, CarTypeEnum.MOTORCYCLE, 2),
            context.getString(R.string.roller) to CarCard(CardTypeEnum.CAR, CarTypeEnum.MOTORCYCLE, 3),

            context.getString(R.string.cabrio) to CarCard(CardTypeEnum.CAR, CarTypeEnum.CONVERTIBLE, 4),
            context.getString(R.string.cabrio) to CarCard(CardTypeEnum.CAR, CarTypeEnum.CONVERTIBLE, 5),
            context.getString(R.string.cabrio) to CarCard(CardTypeEnum.CAR, CarTypeEnum.CONVERTIBLE, 6),

            context.getString(R.string.oldtimer) to CarCard(CardTypeEnum.CAR, CarTypeEnum.VINTAGECAR, 7),
            context.getString(R.string.oldtimer) to CarCard(CardTypeEnum.CAR, CarTypeEnum.VINTAGECAR, 8),
            context.getString(R.string.oldtimer) to CarCard(CardTypeEnum.CAR, CarTypeEnum.VINTAGECAR, 9),

            context.getString(R.string.suv) to CarCard(CardTypeEnum.CAR, CarTypeEnum.SUV, 10),
            context.getString(R.string.suv) to CarCard(CardTypeEnum.CAR, CarTypeEnum.SUV, 11),
            context.getString(R.string.suv) to CarCard(CardTypeEnum.CAR, CarTypeEnum.SUV, 12),

            context.getString(R.string.sportwagen) to CarCard(CardTypeEnum.CAR, CarTypeEnum.SPORTSCAR, 13),
            context.getString(R.string.sportwagen) to CarCard(CardTypeEnum.CAR, CarTypeEnum.SPORTSCAR, 14),

            context.getString(R.string.schwimmen) to SportCard(CardTypeEnum.SPORT, SportTypeEnum.SWIMMING),
            context.getString(R.string.laufen) to SportCard(CardTypeEnum.SPORT, SportTypeEnum.RUNNING),
            context.getString(R.string.radfahren) to SportCard(CardTypeEnum.SPORT, SportTypeEnum.CYCLING),
            context.getString(R.string.rudern) to SportCard(CardTypeEnum.SPORT, SportTypeEnum.ROWING),

            context.getString(R.string.baumhaus) to HouseCard(CardTypeEnum.HOUSE, HouseTypeEnum.TREEHOUSE),
            context.getString(R.string.stadtvilla) to HouseCard(CardTypeEnum.HOUSE, HouseTypeEnum.VILLA),
            context.getString(R.string.strandhaus) to HouseCard(CardTypeEnum.HOUSE, HouseTypeEnum.BEACHHOUSE),
            context.getString(R.string.wohnturm) to HouseCard(CardTypeEnum.HOUSE, HouseTypeEnum.RESIDENTALTOWER),
            context.getString(R.string.landhaus) to HouseCard(CardTypeEnum.HOUSE, HouseTypeEnum.COUNTRYHOUSE),
            context.getString(R.string.herrenhaus) to HouseCard(CardTypeEnum.HOUSE, HouseTypeEnum.MANORHOUSE),
            context.getString(R.string.hausboot) to HouseCard(CardTypeEnum.HOUSE, HouseTypeEnum.HOUSEBOAT),
            context.getString(R.string.bauwagen) to HouseCard(CardTypeEnum.HOUSE, HouseTypeEnum.CONSTRUCTIONTRAILER),

            context.getString(R.string.manager) to JobCard(CardTypeEnum.JOB, JobTypeEnum.MANAGER),
            context.getString(R.string.managerin) to JobCard(CardTypeEnum.JOB, JobTypeEnum.MANAGER),
            context.getString(R.string.trainerin) to JobCard(CardTypeEnum.JOB, JobTypeEnum.PERSONALTRAINER),
            context.getString(R.string.autoh_ndler) to JobCard(CardTypeEnum.JOB, JobTypeEnum.CARSALESMAN),
            context.getString(R.string.tier_rztin) to JobCard(CardTypeEnum.JOB, JobTypeEnum.VET),
            context.getString(R.string.makler) to JobCard(CardTypeEnum.JOB, JobTypeEnum.REALESTATEAGENT),

            context.getString(R.string.liebe) to LoveCard(CardTypeEnum.LOVE, LoveTypeEnum.UNKNOWN)
        )
    }

    override fun getCardName(card: ICard): String {
        TODO("Not yet implemented")
    }
}