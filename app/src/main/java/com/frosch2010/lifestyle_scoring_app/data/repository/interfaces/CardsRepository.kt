package com.frosch2010.lifestyle_scoring_app.data.repository.interfaces

import com.frosch2010.lifestyle_scoring_app.models.entities.LoveCard
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard

/**
 * Domain port for static card catalogs, localized labels, and subtype-specific scoring metadata.
 *
 * Centralizes everything the UI needs to offer consistent manual selection and iconography without
 * duplicating resource lookups across activities.
 */
interface CardsRepository {
    /**
     * Returns the full selectable card catalog keyed by stable localized display labels.
     *
     * @return Map from human-readable label to concrete [ICard] instance used when persisting picks.
     */
    fun getCardsMap(): Map<String, ICard>

    /**
     * Resolves the user-visible title for a concrete card instance.
     *
     * @param card Card whose localized display name should be resolved.
     * @return Non-empty localized string suitable for list rows and dialogs.
     */
    fun getCardName(card: ICard): String

    /**
     * Enumerates legal manual point assignments for a car card, respecting its subtype rules.
     *
     * @param card Car card instance whose allowed point steps are requested.
     * @return Distinct ascending point options the manual add flow may offer.
     */
    fun getCarCardPoints(card: ICard): List<Int>

    /**
     * Lists localized labels for every selectable love-card specialization.
     *
     * @return Subtype names in presentation order for pickers and dialogs.
     */
    fun getLoveCardTypes(): List<String>

    /**
     * Maps a typed love card to its drawable resource identifier.
     *
     * @param loveCard Concrete love card carrying subtype state.
     * @return Android `R.drawable` identifier for previews and list adornments.
     */
    fun getLoveIcon(loveCard: LoveCard): Int

    /**
     * Returns love-card subtype labels paired with icon resource identifiers for rich list rows.
     *
     * @return Ordered pairs of `(label, drawableResId)` suitable for adapter binding.
     */
    fun getLoveCardsWithIcon(): List<Pair<String, Int>>
}
