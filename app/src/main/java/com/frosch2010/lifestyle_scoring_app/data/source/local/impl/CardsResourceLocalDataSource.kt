package com.frosch2010.lifestyle_scoring_app.data.source.local.impl

import android.content.Context
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.data.source.local.interfaces.CardsLocalDataSource
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
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Materializes the static card catalog from string resources so manual flows stay locale-aware without remote config.
 *
 * Centralizes exhaustive `when` mapping from enums to `R.string`/`R.drawable` identifiers, keeping repositories thin.
 *
 * @param context Application [Context] for `getString` and drawable resolution.
 */
class CardsResourceLocalDataSource @Inject constructor(
    @param:ApplicationContext private val context: Context
) : CardsLocalDataSource {
    /**
     * Builds the master picker map tying each localized label to a concrete immutable [ICard] instance.
     *
     * @return Immutable map keyed by display strings; keys double as search tokens in manual add flows.
     */
    override fun getCardsMap(): Map<String, ICard> {
        return mapOf(
            context.getString(R.string.cat) to AnimalCard(CardTypeEnum.ANIMAL, AnimalTypeEnum.CAT),
            context.getString(R.string.cockatoo) to AnimalCard(CardTypeEnum.ANIMAL, AnimalTypeEnum.COCKATOO),
            context.getString(R.string.pony) to AnimalCard(CardTypeEnum.ANIMAL, AnimalTypeEnum.PONY),
            context.getString(R.string.dog) to AnimalCard(CardTypeEnum.ANIMAL, AnimalTypeEnum.DOG),
            context.getString(R.string.motorcycle) to CarCard(CardTypeEnum.CAR, CarTypeEnum.MOTORCYCLE, 1),
            context.getString(R.string.convertible) to CarCard(CardTypeEnum.CAR, CarTypeEnum.CONVERTIBLE, 4),
            context.getString(R.string.vintagecar) to CarCard(CardTypeEnum.CAR, CarTypeEnum.VINTAGECAR, 7),
            context.getString(R.string.suv) to CarCard(CardTypeEnum.CAR, CarTypeEnum.SUV, 10),
            context.getString(R.string.sportscar) to CarCard(CardTypeEnum.CAR, CarTypeEnum.SPORTSCAR, 13),
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

    /**
     * Performs exhaustive enum-to-string resolution for every supported [CardTypeEnum] branch.
     *
     * @param card Card whose `cardType` and subtype fields drive the `when` ladder.
     * @return Localized title matching list rows in the manual add experience.
     */
    override fun getCardName(card: ICard): String {
        return when (card.cardType) {
            CardTypeEnum.LOVE -> when ((card as LoveCard).loveType) {
                LoveTypeEnum.ANIMAL -> context.getString(R.string.animal_love)
                LoveTypeEnum.HOUSE -> context.getString(R.string.realestate_love)
                LoveTypeEnum.JOB -> context.getString(R.string.job_love)
                LoveTypeEnum.CAR -> context.getString(R.string.car_love)
                LoveTypeEnum.UNKNOWN -> context.getString(R.string.liebe)
            }

            CardTypeEnum.JOB -> when ((card as JobCard).jobType) {
                JobTypeEnum.MANAGER -> context.getString(R.string.manager)
                JobTypeEnum.MANAGERIN -> context.getString(R.string.managerin)
                JobTypeEnum.PERSONALTRAINER -> context.getString(R.string.personaltrainer)
                JobTypeEnum.CARSALESMAN -> context.getString(R.string.carsalesman)
                JobTypeEnum.VET -> context.getString(R.string.vet)
                JobTypeEnum.REALESTATEAGENT -> context.getString(R.string.realestateagent)
            }

            CardTypeEnum.CAR -> when ((card as CarCard).carType) {
                CarTypeEnum.MOTORCYCLE -> context.getString(R.string.motorcycle)
                CarTypeEnum.CONVERTIBLE -> context.getString(R.string.convertible)
                CarTypeEnum.VINTAGECAR -> context.getString(R.string.vintagecar)
                CarTypeEnum.SUV -> context.getString(R.string.suv)
                CarTypeEnum.SPORTSCAR -> context.getString(R.string.sportscar)
            }

            CardTypeEnum.HOUSE -> when ((card as HouseCard).houseType) {
                HouseTypeEnum.TREEHOUSE -> context.getString(R.string.treehouse)
                HouseTypeEnum.VILLA -> context.getString(R.string.villa)
                HouseTypeEnum.BEACHHOUSE -> context.getString(R.string.beachhouse)
                HouseTypeEnum.RESIDENTALTOWER -> context.getString(R.string.residentaltower)
                HouseTypeEnum.COUNTRYHOUSE -> context.getString(R.string.countryhouse)
                HouseTypeEnum.MANORHOUSE -> context.getString(R.string.manorhouse)
                HouseTypeEnum.HOUSEBOAT -> context.getString(R.string.houseboat)
                HouseTypeEnum.CONSTRUCTIONTRAILER -> context.getString(R.string.constructiontrailer)
            }

            CardTypeEnum.ANIMAL -> when ((card as AnimalCard).animalType) {
                AnimalTypeEnum.CAT -> context.getString(R.string.cat)
                AnimalTypeEnum.COCKATOO -> context.getString(R.string.cockatoo)
                AnimalTypeEnum.PONY -> context.getString(R.string.pony)
                AnimalTypeEnum.DOG -> context.getString(R.string.dog)
            }

            CardTypeEnum.SPORT -> when ((card as SportCard).sportType) {
                SportTypeEnum.SWIMMING -> context.getString(R.string.swimming)
                SportTypeEnum.RUNNING -> context.getString(R.string.running)
                SportTypeEnum.CYCLING -> context.getString(R.string.cycling)
                SportTypeEnum.ROWING -> context.getString(R.string.rowing)
            }
        }
    }

    /**
     * Supplies discrete manual score steps aligned with physical card tiers for each car subtype.
     *
     * @param card Car card whose `carType` selects the ladder; callers must ensure type safety before invoking.
     * @return Non-empty ascending options for UI numeric pickers.
     */
    override fun getCarCardPoints(card: ICard): List<Int> {
        return when ((card as CarCard).carType) {
            CarTypeEnum.MOTORCYCLE -> listOf(1, 2, 3)
            CarTypeEnum.CONVERTIBLE -> listOf(4, 5, 6)
            CarTypeEnum.VINTAGECAR -> listOf(7, 8, 9)
            CarTypeEnum.SUV -> listOf(10, 11, 12)
            CarTypeEnum.SPORTSCAR -> listOf(13, 14)
        }
    }

    /**
     * Lists every love specialization label shown when upgrading a generic love card.
     *
     * @return Fixed-size list ordered to match iconography in companion UI components.
     */
    override fun getLoveCardTypes(): List<String> {
        return listOf(
            context.getString(R.string.animal_love),
            context.getString(R.string.realestate_love),
            context.getString(R.string.job_love),
            context.getString(R.string.car_love)
        )
    }

    /**
     * Resolves preview artwork for known `(CardTypeEnum.LOVE, LoveTypeEnum)` combinations with a safe fallback.
     *
     * @param loveCard Love card value object compared structurally against canonical instances.
     * @return Drawable resource id suitable for `ImageView.setImageResource`.
     */
    override fun getLoveIcon(loveCard: LoveCard): Int {
        return when (loveCard) {
            LoveCard(CardTypeEnum.LOVE, LoveTypeEnum.ANIMAL) -> R.drawable.ic_animal
            LoveCard(CardTypeEnum.LOVE, LoveTypeEnum.HOUSE) -> R.drawable.ic_house
            LoveCard(CardTypeEnum.LOVE, LoveTypeEnum.JOB) -> R.drawable.ic_work
            LoveCard(CardTypeEnum.LOVE, LoveTypeEnum.CAR) -> R.drawable.ic_car
            else -> R.drawable.ic_question
        }
    }

    /**
     * Bundles localized love labels with companion drawable ids for two-line picker rows.
     *
     * @return Immutable pairs mirroring the order used by love-type selection dialogs.
     */
    override fun getLoveCardsWithIcon(): List<Pair<String, Int>> {
        return listOf(
            Pair(context.getString(R.string.animal_love), R.drawable.ic_animal),
            Pair(context.getString(R.string.realestate_love), R.drawable.ic_house),
            Pair(context.getString(R.string.job_love), R.drawable.ic_work),
            Pair(context.getString(R.string.car_love), R.drawable.ic_car)
        )
    }
}
