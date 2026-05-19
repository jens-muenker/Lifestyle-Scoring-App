package com.frosch2010.lifestyle_scoring_app.services.impl

import com.frosch2010.lifestyle_scoring_app.models.entities.AnimalCard
import com.frosch2010.lifestyle_scoring_app.models.entities.CarCard
import com.frosch2010.lifestyle_scoring_app.models.entities.HouseCard
import com.frosch2010.lifestyle_scoring_app.models.entities.JobCard
import com.frosch2010.lifestyle_scoring_app.models.entities.LoveCard
import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.JobTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.LoveTypeEnum
import com.frosch2010.lifestyle_scoring_app.domain.model.CategoryPointsBreakdown
import com.frosch2010.lifestyle_scoring_app.domain.model.CategoryPointsEntry
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import com.frosch2010.lifestyle_scoring_app.services.interfaces.IPlayerPointsCalculationService
import javax.inject.Inject

/**
 * Pure Kotlin implementation of the Lifestyle Scoring rule book used for totals and UI breakdowns.
 *
 * Keeps scoring side effects free of Android types so the same engine runs in JVM unit tests and on device.
 */
class PlayerPointsCalculationService @Inject constructor() : IPlayerPointsCalculationService {

    /**
     * Intermediate aggregation of direct category points before love multipliers mutate headline totals.
     *
     * @property animalPoints Points accumulated from animal sets and related synergies.
     * @property carPoints Sum of explicit car card point chips plus applicable bonuses.
     * @property housePoints Iterative house-set scoring outcome.
     * @property jobPoints Stacked profession bonuses including manager interactions.
     */
    private data class ScoredCategories(
        val animalPoints: Int,
        val carPoints: Int,
        val housePoints: Int,
        val jobPoints: Int
    ) {
        /**
         * Sums the four headline categories that feed both total score and breakdown rows.
         *
         * @return Combined integer used as `CategoryPointsBreakdown.totalPoints` after love adjustments.
         */
        fun total(): Int = animalPoints + carPoints + housePoints + jobPoints
    }

    /**
     * Collapses [cards] into a single competition score after love multipliers are applied per category.
     *
     * @param cards Unordered player hand; duplicate physical cards remain distinct entries.
     * @return Final non-negative score mirroring tournament rules encoded in private helpers.
     */
    override fun calculatePlayerPoints(cards: List<ICard>): Int {
        return computeScoredCategories(cards).total()
    }

    /**
     * Surfaces both the post-multiplier total and enum-ordered rows for analytics-style UI lists.
     *
     * Sport and love cards influence scoring indirectly and therefore expose `0` as direct category points
     * in the breakdown list even when they change multiplier outcomes in [calculatePlayerPoints].
     *
     * @param cards Same hand input as the total score API for consistency guarantees.
     * @return Immutable breakdown whose `entries` align with [CardTypeEnum] declaration order.
     */
    override fun calculateCategoryPointsBreakdown(cards: List<ICard>): CategoryPointsBreakdown {
        val scored = computeScoredCategories(cards)
        val byType = mapOf(
            CardTypeEnum.ANIMAL to scored.animalPoints,
            CardTypeEnum.CAR to scored.carPoints,
            CardTypeEnum.HOUSE to scored.housePoints,
            CardTypeEnum.JOB to scored.jobPoints,
            CardTypeEnum.SPORT to 0,
            CardTypeEnum.LOVE to 0
        )
        val entries = CardTypeEnum.entries.map { type ->
            CategoryPointsEntry(type, byType.getValue(type))
        }
        return CategoryPointsBreakdown(scored.total(), entries)
    }

    /**
     * Partitions the mixed hand, scores each pillar, then applies love-type doubles where applicable.
     *
     * @param cards Full card list including indirect types such as sport or love cards.
     * @return Immutable aggregate of per-category totals after love multipliers are applied independently.
     */
    private fun computeScoredCategories(cards: List<ICard>): ScoredCategories {
        val animalCards = getAnimalCards(cards)
        val carCards = getCarCards(cards)
        val houseCards = getHouseCards(cards)
        val jobCards = getJobCards(cards)
        val sportCards = getSportCards(cards)
        val loveCards = getLoveCards(cards)

        var animalPoints = calculateAnimalPoints(animalCards)
        var carPoints = calculateCarPoints(carCards)
        var housePoints = calculateHousePoints(houseCards)
        var jobPoints = calculateJobPoints(jobCards, animalCards.size, carCards.size, houseCards.size, sportCards.size)

        animalPoints = calculateLovePointsForAnimalCards(loveCards, animalPoints)
        carPoints = calculateLovePointsForCarCards(loveCards, carPoints)
        housePoints = calculateLovePointsForHouseCards(loveCards, housePoints)
        jobPoints = calculateLovePointsForJobCards(loveCards, jobPoints)

        return ScoredCategories(animalPoints, carPoints, housePoints, jobPoints)
    }

    /**
     * Doubles job pillar points when a job-specialized love card is present in the same hand.
     *
     * @param loveCards Filtered love cards (maybe empty).
     * @param jobPoints Pre-love job subtotal.
     * @return Either multiplied or unchanged job points.
     */
    private fun calculateLovePointsForJobCards(loveCards: List<ICard>, jobPoints: Int): Int {
        if(loveCards.isEmpty()) return jobPoints

        for (card in loveCards) {
            if (card.cardType == CardTypeEnum.LOVE) {
                val loveCard = card as LoveCard
                if (loveCard.loveType == LoveTypeEnum.JOB) {
                    return jobPoints * LOVE_MULTIPLIER
                }
            }
        }

        return jobPoints
    }

    /**
     * Doubles house pillar points when a real-estate-oriented love card exists.
     *
     * @param loveCards Filtered love cards (maybe empty).
     * @param housePoints Pre-love house subtotal.
     * @return Multiplied house points when the love synergy fires; otherwise [housePoints].
     */
    private fun calculateLovePointsForHouseCards(loveCards: List<ICard>, housePoints: Int): Int {
        if(loveCards.isEmpty()) return housePoints

        for (card in loveCards) {
            if (card.cardType == CardTypeEnum.LOVE) {
                val loveCard = card as LoveCard
                if (loveCard.loveType == LoveTypeEnum.HOUSE) {
                    return housePoints * LOVE_MULTIPLIER
                }
            }
        }

        return housePoints
    }

    /**
     * Doubles car pillar points when a car-oriented love card exists.
     *
     * @param loveCards Filtered love cards (maybe empty).
     * @param carPoints Pre-love car subtotal.
     * @return Multiplied car points when the synergy applies.
     */
    private fun calculateLovePointsForCarCards(loveCards: List<ICard>, carPoints: Int): Int {
        if(loveCards.isEmpty()) return carPoints

        for (card in loveCards) {
            if (card.cardType == CardTypeEnum.LOVE) {
                val loveCard = card as LoveCard
                if (loveCard.loveType == LoveTypeEnum.CAR) {
                    return carPoints * LOVE_MULTIPLIER
                }
            }
        }

        return carPoints
    }

    /**
     * Doubles animal pillar points when an animal-oriented love card exists.
     *
     * @param loveCards Filtered love cards (maybe empty).
     * @param animalPoints Pre-love animal subtotal.
     * @return Multiplied animal points when the synergy applies.
     */
    private fun calculateLovePointsForAnimalCards(loveCards: List<ICard>, animalPoints: Int): Int {
        if(loveCards.isEmpty()) return animalPoints

        for (card in loveCards) {
            if (card.cardType == CardTypeEnum.LOVE) {
                val loveCard = card as LoveCard
                if (loveCard.loveType == LoveTypeEnum.ANIMAL) {
                    return animalPoints * LOVE_MULTIPLIER
                }
            }
        }

        return animalPoints
    }

    /**
     * Sums every profession-specific bonus that depends on other category counts.
     *
     * @param jobCards Concrete job cards owned by the player.
     * @param animalCount Number of animal cards for vet synergy.
     * @param carCount Number of car cards for salesman synergy.
     * @param houseCount Number of house cards for agent synergy.
     * @param sportCount Number of sport cards for trainer synergy.
     * @return Aggregate job contribution before love doubling.
     */
    private fun calculateJobPoints(jobCards: List<JobCard>, animalCount: Int, carCount: Int, houseCount: Int, sportCount: Int): Int {
        val vetPoints = calculateVetPoints(jobCards, animalCount)
        val carSalesmanPoints = calculateCarSalesmanPoints(jobCards, carCount)
        val realEstateAgentPoints = calculateRealEstateAgentPoints(jobCards, houseCount)
        val personalTrainerPoints = calculatePersonalTrainerPoints(jobCards, sportCount)
        val managerPoints = calculateManagerPoints(jobCards, animalCount, carCount, houseCount, sportCount)

        return vetPoints + carSalesmanPoints + realEstateAgentPoints + personalTrainerPoints + managerPoints
    }

    /**
     * Manager/Managerin bonus multiplying the smallest owned lifestyle pillar count to reward balance.
     *
     * @param jobCards Job cards present in the hand.
     * @param animalCount Animal stack size for comparison.
     * @param carCount Car stack size for comparison.
     * @param houseCount House stack size for comparison.
     * @param sportCount Sport stack size for comparison.
     * @return Bonus points, or `0` when neither manager variant is owned.
     */
    private fun calculateManagerPoints(jobCards: List<JobCard>, animalCount: Int, carCount: Int, houseCount: Int, sportCount: Int): Int {
        if(!jobCards.contains(JobCard(CardTypeEnum.JOB, JobTypeEnum.MANAGER)) && !jobCards.contains(JobCard(CardTypeEnum.JOB, JobTypeEnum.MANAGERIN))) {
            return 0
        }

        val counts = arrayOf(animalCount, carCount, houseCount, sportCount)
        counts.sortDescending()
        return counts.last() * MANAGER_MULTIPLIER
    }

    /**
     * Awards linear points for each sport card when the personal trainer job is present.
     *
     * @param jobCards Job cards owned by the player.
     * @param sportCount Number of sport cards fueling the bonus.
     * @return Trainer contribution or `0` when the job is absent.
     */
    private fun calculatePersonalTrainerPoints(jobCards: List<JobCard>, sportCount: Int): Int {
        if(!jobCards.contains(JobCard(CardTypeEnum.JOB, JobTypeEnum.PERSONALTRAINER))) {
            return 0
        }

        return sportCount * PERSONAL_TRAINER_MULTIPLIER
    }

    /**
     * Awards points per house card when the real estate agent job is owned.
     *
     * @param jobCards Job cards owned by the player.
     * @param houseCount Number of house cards fueling the bonus.
     * @return Agent contribution or `0` when the job is absent.
     */
    private fun calculateRealEstateAgentPoints(jobCards: List<JobCard>, houseCount: Int): Int {
        if(!jobCards.contains(JobCard(CardTypeEnum.JOB, JobTypeEnum.REALESTATEAGENT))) {
            return 0
        }

        return houseCount * REAL_ESTATE_AGENT_MULTIPLIER
    }

    /**
     * Awards points per car card when the car salesman job is owned.
     *
     * @param jobCards Job cards owned by the player.
     * @param carCount Number of car cards fueling the bonus.
     * @return Salesman contribution or `0` when the job is absent.
     */
    private fun calculateCarSalesmanPoints(jobCards: List<JobCard>, carCount: Int): Int {
        if(!jobCards.contains(JobCard(CardTypeEnum.JOB, JobTypeEnum.CARSALESMAN))) {
            return 0
        }

        return carCount * CAR_SALESMAN_MULTIPLIER
    }

    /**
     * Vet bonus based on number of animal cards.
     *
     * @param jobCards Job cards owned by the player.
     * @param animalCount Number of animal cards fueling the bonus.
     * @return Vet contribution or `0` when the job is absent.
     */
    private fun calculateVetPoints(jobCards: List<JobCard>, animalCount: Int): Int {
        if(!jobCards.contains(JobCard(CardTypeEnum.JOB, JobTypeEnum.VET))) {
            return 0
        }

        return animalCount * VET_MULTIPLIER
    }

    /**
     * Iteratively scores house sets where each round grants `activeSetSize^2` points until stacks deplete.
     *
     * @param houseCards Collection of house cards possibly containing duplicate types.
     * @return Accumulated quadratic scoring across all depletion rounds.
     */
    private fun calculateHousePoints(houseCards: List<HouseCard>): Int {
        if (houseCards.isEmpty()) {
            return 0
        }

        val cardTypeCounts = houseCards.groupingBy { it.houseType }.eachCount()

        val pointsList = cardTypeCounts.values.sorted().toMutableList()
        var points = 0

        while (pointsList.count { it > 0 } > 0) {
            val activeSetSize = pointsList.count { it > 0 }
            points += activeSetSize * activeSetSize

            for (i in 0 until pointsList.size) {
                if (pointsList[i] > 0) {
                    pointsList[i] -= 1
                }
            }
        }

        return points
    }

    /**
     * Sums the printed point values carried on each car card instance.
     *
     * @param carCards Non-null list of car cards (possibly empty).
     * @return Aggregate car pillar points before love doubling.
     */
    private fun calculateCarPoints(carCards: List<CarCard>): Int {
        return carCards.sumOf { it.points }
    }

    /**
     * Scores animal sets: three distinct types score once; four distinct types allow repeated scoring passes.
     *
     * @param cards Animal cards only; callers must pre-filter.
     * @return Animal pillar points before love doubling.
     */
    private fun calculateAnimalPoints(cards: List<AnimalCard>): Int {
        val cardTypeCounts = cards.groupingBy { it.animalType }.eachCount()

        if (cardTypeCounts.size < ANIMAL_MIN_SCORING_TYPES) return 0

        if (cardTypeCounts.size == ANIMAL_MIN_SCORING_TYPES) {
            var minSetCount = 0

            cardTypeCounts.values.forEach {
                if (it < minSetCount || minSetCount == 0) {
                    minSetCount = it
                }
            }

            return minSetCount * ANIMAL_SET_POINTS
        }

        if (cardTypeCounts.size == ANIMAL_FULL_SET_TYPES) {
            val pointsList = cardTypeCounts.values.sorted().toMutableList()
            var points = 0
            
            while (pointsList.count { it > 0 } > ANIMAL_UNSCORABLE_REMAINDER_TYPES) {
                for (i in 0 until ANIMAL_FULL_SET_TYPES) {
                    if (pointsList[i] > 0) {
                        pointsList[i] -= 1
                    }
                }
                points += ANIMAL_SET_POINTS
            }

            return points
        }

        return 0
    }


    /**
     * @param cards Mixed hand including non-animal types.
     * @return Only [AnimalCard] instances preserving original encounter order.
     */
    private fun getAnimalCards(cards: List<ICard>): List<AnimalCard> {
        return cards.filterIsInstance<AnimalCard>()
    }

    /**
     * @param cards Mixed hand including non-car types.
     * @return Only [CarCard] instances preserving original encounter order.
     */
    private fun getCarCards(cards: List<ICard>): List<CarCard> {
        return cards.filterIsInstance<CarCard>()
    }

    /**
     * @param cards Mixed hand including non-house types.
     * @return Only [HouseCard] instances preserving original encounter order.
     */
    private fun getHouseCards(cards: List<ICard>): List<HouseCard> {
        return cards.filterIsInstance<HouseCard>()
    }

    /**
     * @param cards Mixed hand including non-job types.
     * @return Only [JobCard] instances preserving original encounter order.
     */
    private fun getJobCards(cards: List<ICard>): List<JobCard> {
        return cards.filterIsInstance<JobCard>()
    }

    /**
     * Filters love cards from a mixed card list.
     *
     * @param cards Mixed hand including non-love types.
     * @return Only cards whose [ICard.cardType] is [CardTypeEnum.LOVE].
     */
    private fun getLoveCards(cards: List<ICard>): List<ICard> {
        return cards.filter { it.cardType == CardTypeEnum.LOVE }
    }

    /**
     * @param cards Mixed hand possibly lacking sport cards.
     * @return Sport cards as [ICard] shells because sport scoring is count-based only.
     */
    private fun getSportCards(cards: List<ICard>): List<ICard> {
        return cards.filter { it.cardType == CardTypeEnum.SPORT }
    }

    /**
     * Tunable coefficients mirroring the physical rule book; kept private to avoid leaking balancing details.
     */
    private companion object {
        /** Multiplier applied when a matching love card exists for a category. */
        private const val LOVE_MULTIPLIER = 2
        /** Manager bonus multiplier for the weakest category count. */
        private const val MANAGER_MULTIPLIER = 7
        /** Personal trainer bonus per sport card. */
        private const val PERSONAL_TRAINER_MULTIPLIER = 4
        /** Real estate agent bonus per house card. */
        private const val REAL_ESTATE_AGENT_MULTIPLIER = 3
        /** Car salesman bonus per car card. */
        private const val CAR_SALESMAN_MULTIPLIER = 3
        /** Vet bonus per animal card. */
        private const val VET_MULTIPLIER = 2
        /** Minimum number of distinct animal types required to score. */
        private const val ANIMAL_MIN_SCORING_TYPES = 3
        /** Number of distinct animal types that forms a full set. */
        private const val ANIMAL_FULL_SET_TYPES = 4
        /** Remaining distinct types below this threshold do not score another set. */
        private const val ANIMAL_UNSCORABLE_REMAINDER_TYPES = 2
        /** Points granted per completed animal set. */
        private const val ANIMAL_SET_POINTS = 10
    }
}
