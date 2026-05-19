package com.frosch2010.lifestyle_scoring_app.data.repository.impl

import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.CardsRepository
import com.frosch2010.lifestyle_scoring_app.data.source.local.interfaces.CardsLocalDataSource
import com.frosch2010.lifestyle_scoring_app.models.entities.LoveCard
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import javax.inject.Inject

/**
 * Pass-through repository keeping card catalog concerns inside resource-backed local sources.
 *
 * Marked `open` so instrumentation tests can substitute lightweight fakes without Hilt graph changes.
 *
 * @param cardsLocalDataSource Provider of maps, labels, ladders, and drawable bindings.
 */
open class CardsRepositoryImpl @Inject constructor(
    private val cardsLocalDataSource: CardsLocalDataSource
) : CardsRepository {
    /**
     * @return Delegated card catalog from the local implementation.
     */
    override fun getCardsMap(): Map<String, ICard> {
        return cardsLocalDataSource.getCardsMap()
    }

    /**
     * @param card Concrete card instance whose localized title is requested.
     * @return Delegated localized display string.
     */
    override fun getCardName(card: ICard): String {
        return cardsLocalDataSource.getCardName(card)
    }

    /**
     * @param card Car card whose subtype determines the returned ladder.
     * @return Delegated list of valid manual point assignments.
     */
    override fun getCarCardPoints(card: ICard): List<Int> {
        return cardsLocalDataSource.getCarCardPoints(card)
    }

    /**
     * @return Delegated ordered love subtype labels.
     */
    override fun getLoveCardTypes(): List<String> {
        return cardsLocalDataSource.getLoveCardTypes()
    }

    /**
     * @param loveCard Love card carrying subtype discriminator used for drawable lookup.
     * @return Delegated drawable resource identifier.
     */
    override fun getLoveIcon(loveCard: LoveCard): Int {
        return cardsLocalDataSource.getLoveIcon(loveCard)
    }

    /**
     * @return Delegated `(label, icon)` tuples for rich picker rows.
     */
    override fun getLoveCardsWithIcon(): List<Pair<String, Int>> {
        return cardsLocalDataSource.getLoveCardsWithIcon()
    }
}
