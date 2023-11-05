package com.frosch2010.lifestyle_scoring_app.models.interfaces

/**
 * This interface is used for the cards repository to manage the card data.
 * @author Jens Münker
 */
interface ICardsRepository {

    /**
     * This method is used to get all cards from the repository.
     * @return The cards.
     */
    fun getCardsMap(): Map<String, ICard>

    /**
     * This method is used to get the card name from a card.
     * @param card The card to get.
     * @return The card name.
     */
    fun getCardName(card: ICard): String

    /**
     * This method is used to get the points for a car card.
     * @param card Car card to get the points for.
     * @return The possible card points.
     */
    fun getCarCardPoints(card: ICard): List<Int>

    /**
     * This method is used to get the types of the love cards.
     * @return The love card types.
     */
    fun getLoveCardTypes(): List<String>
}