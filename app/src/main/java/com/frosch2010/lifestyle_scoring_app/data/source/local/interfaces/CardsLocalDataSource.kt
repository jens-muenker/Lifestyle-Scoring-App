package com.frosch2010.lifestyle_scoring_app.data.source.local.interfaces

import com.frosch2010.lifestyle_scoring_app.models.entities.LoveCard
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard

/**
 * Port describing every read-only card catalog operation backed by local resources or static tables.
 */
interface CardsLocalDataSource {
    /**
     * @return Map of localized display labels to canonical [ICard] instances used when persisting picks.
     */
    fun getCardsMap(): Map<String, ICard>

    /**
     * @param card Card whose title should be resolved against current resources.
     * @return Localized label suitable for list rows and dialogs.
     */
    fun getCardName(card: ICard): String

    /**
     * @param card Car card whose subtype determines the scoring ladder.
     * @return Distinct ascending manual point options for that subtype.
     */
    fun getCarCardPoints(card: ICard): List<Int>

    /**
     * @return Localized labels for each supported love specialization in stable UI order.
     */
    fun getLoveCardTypes(): List<String>

    /**
     * @param loveCard Love card variant used to select the preview drawable.
     * @return Drawable resource id for previews; may be a generic placeholder for unknown pairings.
     */
    fun getLoveIcon(loveCard: LoveCard): Int

    /**
     * @return Pairs aligning localized labels with companion drawable resources for rich pickers.
     */
    fun getLoveCardsWithIcon(): List<Pair<String, Int>>
}
