package com.frosch2010.lifestyle_scoring_app.utils

import com.frosch2010.fuzzywuzzy_kotlin.FuzzySearch
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import com.frosch2010.lifestyle_scoring_app.utils.CardNameMatcher.cleanText
import java.util.Locale
import kotlin.math.abs

/**
 * Shared normalization and fuzzy scoring pipeline reused by OCR services and manual search UIs.
 *
 * Mirrors the confidence heuristics in [com.frosch2010.lifestyle_scoring_app.services.impl.CardRecognizerService]
 * so user-typed queries and camera-derived tokens converge on identical acceptance thresholds.
 */
object CardNameMatcher {

    /**
     * Lowercases, strips non-letters, and removes accents to stabilize fuzzy dictionary keys.
     *
     * @param input Raw text fragment from OCR blocks or text fields.
     * @return Canonical comparison token fed into fuzzy extractors.
     */
    fun cleanText(input: String): String =
        input.lowercase(Locale.getDefault()).normalize().filter { it.isLetter() }

    /**
     * Tuple describing the best fuzzy candidate for a cleaned query against the card map keys.
     *
     * @property card Resolved [ICard] when the extractor returned a known key; otherwise `null`.
     * @property score Library-specific similarity score for the best match.
     * @property matchingKey Winning dictionary key associated with [card].
     * @property cleanedInput Original normalized query used for length heuristics.
     */
    data class Match(
        val card: ICard?,
        val score: Int,
        val matchingKey: String,
        val cleanedInput: String
    ) {
        /**
         * Applies length-delta and score thresholds so weak fuzzy hits do not auto-select cards.
         *
         * @return `true` when the tuple represents a confident user-visible match.
         */
        fun isAcceptable(): Boolean =
            card != null && abs(cleanedInput.length - matchingKey.length) < 4 && score > 75
    }

    /**
     * Runs fuzzy extraction for a pre-cleaned token against localized card labels.
     *
     * @param cleanedInput Output of [cleanText] for the user or OCR fragment being evaluated.
     * @param cards Map from localized display labels to concrete card instances.
     * @return [Match] bundle carrying the best candidate and diagnostic metadata for UI messaging.
     */
    fun matchCleanedInput(cleanedInput: String, cards: Map<String, ICard>): Match {
        val extracted = FuzzySearch.extractOne(cleanedInput, cards.keys)
        return Match(cards[extracted.string], extracted.score, extracted.string, cleanedInput)
    }
}
