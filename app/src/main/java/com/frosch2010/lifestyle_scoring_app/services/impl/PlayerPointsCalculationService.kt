package com.frosch2010.lifestyle_scoring_app.services.impl

import com.frosch2010.lifestyle_scoring_app.models.entities.AnimalCard
import com.frosch2010.lifestyle_scoring_app.models.entities.CarCard
import com.frosch2010.lifestyle_scoring_app.models.entities.HouseCard
import com.frosch2010.lifestyle_scoring_app.models.entities.JobCard
import com.frosch2010.lifestyle_scoring_app.models.entities.LoveCard
import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.JobTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.LoveTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import com.frosch2010.lifestyle_scoring_app.services.interfaces.IPlayerPointsCalculationService
import javax.inject.Inject

class PlayerPointsCalculationService @Inject constructor(): IPlayerPointsCalculationService {
    override fun calculatePlayerPoints(cards: List<ICard>): Int {
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

        return animalPoints + carPoints + housePoints + jobPoints
    }

    private fun calculateLovePointsForJobCards(loveCards: List<ICard>, jobPoints: Int): Int {
        if(loveCards.isEmpty()) return jobPoints

        for (card in loveCards) {
            if (card.cardType == CardTypeEnum.LOVE) {
                val loveCard = card as LoveCard
                if (loveCard.loveType == LoveTypeEnum.JOB) {
                    return jobPoints * 2
                }
            }
        }

        return jobPoints
    }

    private fun calculateLovePointsForHouseCards(loveCards: List<ICard>, housePoints: Int): Int {
        if(loveCards.isEmpty()) return housePoints

        for (card in loveCards) {
            if (card.cardType == CardTypeEnum.LOVE) {
                val loveCard = card as LoveCard
                if (loveCard.loveType == LoveTypeEnum.HOUSE) {
                    return housePoints * 2
                }
            }
        }

        return housePoints
    }

    private fun calculateLovePointsForCarCards(loveCards: List<ICard>, carPoints: Int): Int {
        if(loveCards.isEmpty()) return carPoints

        for (card in loveCards) {
            if (card.cardType == CardTypeEnum.LOVE) {
                val loveCard = card as LoveCard
                if (loveCard.loveType == LoveTypeEnum.CAR) {
                    return carPoints * 2
                }
            }
        }

        return carPoints
    }

    private fun calculateLovePointsForAnimalCards(loveCards: List<ICard>, animalPoints: Int): Int {
        if(loveCards.isEmpty()) return animalPoints

        for (card in loveCards) {
            if (card.cardType == CardTypeEnum.LOVE) {
                val loveCard = card as LoveCard
                if (loveCard.loveType == LoveTypeEnum.ANIMAL) {
                    return animalPoints * 2
                }
            }
        }

        return animalPoints
    }

    private fun calculateJobPoints(jobCards: List<JobCard>, animalCount: Int, carCount: Int, houseCount: Int, sportCount: Int): Int {
        val vetPoints = calculateVetPoints(jobCards, animalCount)
        val carSalesmanPoints = calculateCarSalesmanPoints(jobCards, carCount)
        val realEstateAgentPoints = calculateRealEstateAgentPoints(jobCards, houseCount)
        val personalTrainerPoints = calculatePersonalTrainerPoints(jobCards, sportCount)
        val managerPoints = calculateManagerPoints(jobCards, animalCount, carCount, houseCount, sportCount)

        return vetPoints + carSalesmanPoints + realEstateAgentPoints + personalTrainerPoints + managerPoints
    }

    private fun calculateManagerPoints(jobCards: List<JobCard>, animalCount: Int, carCount: Int, houseCount: Int, sportCount: Int): Int {
        if(!jobCards.contains(JobCard(CardTypeEnum.JOB, JobTypeEnum.MANAGER)) && !jobCards.contains(JobCard(CardTypeEnum.JOB, JobTypeEnum.MANAGERIN))) {
            return 0
        }

        val counts = arrayOf(animalCount, carCount, houseCount, sportCount)
        counts.sortDescending()
        return counts.last() * 7
    }

    private fun calculatePersonalTrainerPoints(jobCards: List<JobCard>, sportCount: Int): Int {
        if(!jobCards.contains(JobCard(CardTypeEnum.JOB, JobTypeEnum.PERSONALTRAINER))) {
            return 0
        }

        return sportCount * 4
    }

    private fun calculateRealEstateAgentPoints(jobCards: List<JobCard>, houseCount: Int): Int {
        if(!jobCards.contains(JobCard(CardTypeEnum.JOB, JobTypeEnum.REALESTATEAGENT))) {
            return 0
        }

        return houseCount * 3
    }

    private fun calculateCarSalesmanPoints(jobCards: List<JobCard>, carCount: Int): Int {
        if(!jobCards.contains(JobCard(CardTypeEnum.JOB, JobTypeEnum.CARSALESMAN))) {
            return 0
        }

        return carCount * 3
    }

    private fun calculateVetPoints(jobCards: List<JobCard>, animalCount: Int): Int {
        if(!jobCards.contains(JobCard(CardTypeEnum.JOB, JobTypeEnum.VET))) {
            return 0
        }

        return animalCount * 2
    }

    private fun calculateHousePoints(houseCards: List<HouseCard>): Int {
        if (houseCards.isEmpty()) {
            return 0
        }

        val cardTypeCounts = houseCards.groupingBy { it.houseType }.eachCount()

        val pointsList = cardTypeCounts.values.sorted().toMutableList()
        var points = 0

        while (pointsList.count { it > 0 } > 0) {
            points += when(pointsList.count { it > 0 }) {
                0 -> 0
                1 -> 1
                2 -> 4
                3 -> 9
                4 -> 16
                5 -> 25
                6 -> 36
                7 -> 49
                8 -> 64
                else -> throw Exception("Invalid house card type count")
            }

            for (i in 0 until pointsList.size) {
                if (pointsList[i] > 0) {
                    pointsList[i] -= 1
                }
            }
        }

        return points
    }

    private fun calculateCarPoints(carCards: List<CarCard>): Int {
        return carCards.sumOf { it.points }
    }

    private fun calculateAnimalPoints(cards: List<AnimalCard>): Int {
        val cardTypeCounts = cards.groupingBy { it.animalType }.eachCount()

        if(cardTypeCounts.size < 3) return 0

        if(cardTypeCounts.size == 3) {
            var low = 0

            cardTypeCounts.values.forEach {
                if(it < low || low == 0) {
                    low = it
                }
            }

            return low * 10
        }

        if (cardTypeCounts.size == 4) {
            val pointsList = cardTypeCounts.values.sorted().toMutableList()
            var points = 0
            
            while (pointsList.count { it > 0 } > 2) {
                for (i in 0 until 4) {
                    if (pointsList[i] > 0) {
                        pointsList[i] -= 1
                    }
                }
                points += 10
            }

            return points
        }

        throw Exception("Invalid animal card type count")
    }


    private fun getAnimalCards(cards: List<ICard>): List<AnimalCard> {
        val animalCards = mutableListOf<AnimalCard>()
        for (card in cards) {
            if (card.cardType == CardTypeEnum.ANIMAL) {
                animalCards.add(card as AnimalCard)
            }
        }
        return animalCards
    }

    private fun getCarCards(cards: List<ICard>): List<CarCard> {
        val carCards = mutableListOf<CarCard>()
        for (card in cards) {
            if (card.cardType == CardTypeEnum.CAR) {
                carCards.add(card as CarCard)
            }
        }
        return carCards
    }

    private fun getHouseCards(cards: List<ICard>): List<HouseCard> {
        val houseCards = mutableListOf<HouseCard>()
        for (card in cards) {
            if (card.cardType == CardTypeEnum.HOUSE) {
                houseCards.add(card as HouseCard)
            }
        }
        return houseCards
    }

    private fun getJobCards(cards: List<ICard>): List<JobCard> {
        val jobCards = mutableListOf<JobCard>()
        for (card in cards) {
            if (card.cardType == CardTypeEnum.JOB) {
                jobCards.add(card as JobCard)
            }
        }
        return jobCards
    }

    private fun getLoveCards(cards: List<ICard>): List<ICard> {
        val loveCards = mutableListOf<ICard>()
        for (card in cards) {
            if (card.cardType == CardTypeEnum.LOVE) {
                loveCards.add(card)
            }
        }
        return loveCards
    }

    private fun getSportCards(cards: List<ICard>): List<ICard> {
        val sportCards = mutableListOf<ICard>()
        for (card in cards) {
            if (card.cardType == CardTypeEnum.SPORT) {
                sportCards.add(card)
            }
        }
        return sportCards
    }
}