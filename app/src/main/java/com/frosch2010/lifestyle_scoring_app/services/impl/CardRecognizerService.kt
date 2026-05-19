package com.frosch2010.lifestyle_scoring_app.services.impl

import com.frosch2010.lifestyle_scoring_app.data.repository.interfaces.CardsRepository
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import com.frosch2010.lifestyle_scoring_app.services.interfaces.ICardRecognizerService
import com.frosch2010.lifestyle_scoring_app.utils.CardNameMatcher
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ML Kit-backed recognizer that normalizes OCR fragments and fuzzy-matches them against the card catalog.
 *
 * @param cardsRepository Lazy source of localized dictionary keys used during scoring block iteration.
 */
@Singleton
class CardRecognizerService @Inject constructor(
    private val cardsRepository: CardsRepository
) : ICardRecognizerService {

    /**
     * Latin script recognizer configured with default options suitable for on-device card title OCR.
     */
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    /**
     * Memoized map from localized labels to immutable card instances reused across scan invocations.
     */
    private val cards: Map<String, ICard> by lazy { cardsRepository.getCardsMap() }

    /**
     * Chains ML Kit recognition with fuzzy scoring, returning the first acceptable unique match.
     *
     * @param inputImage Frame captured from the camera pipeline in ML Kit's input format.
     * @return Task completing with a matched [ICard], or `null` when no text block clears heuristics.
     */
    override fun process(inputImage: InputImage): Task<ICard?> =
        recognizer.process(inputImage).continueWithTask { recognizeTextTask ->
            if (!recognizeTextTask.isSuccessful) {
                return@continueWithTask Tasks.forException(
                    recognizeTextTask.exception ?: IllegalStateException("Text recognition failed")
                )
            }
            val text = recognizeTextTask.result ?: return@continueWithTask Tasks.forResult(null)
            val matchedCard = matchCardFromOcrBlockLines(
                text.textBlocks.asSequence().map { it.text },
                cards
            )
            Tasks.forResult(matchedCard)
        }
}

/**
 * Maps OCR line fragments to a single catalog card using the same heuristics as [CardRecognizerService.process].
 *
 * @param textBlockLines Raw text per ML Kit block (whitespace/newlines preserved until [CardNameMatcher.cleanText]).
 * @param cards Localized dictionary from label to immutable card instance.
 * @return First unique acceptable match in block order, or `null` when nothing clears fuzzy thresholds.
 */
internal fun matchCardFromOcrBlockLines(
    textBlockLines: Sequence<String>,
    cards: Map<String, ICard>
): ICard? {
    return textBlockLines
        .map { textBlock -> CardNameMatcher.cleanText(textBlock) }
        .filter { it.length > 2 }
        .map { CardNameMatcher.matchCleanedInput(it, cards) }
        .filter { it.isAcceptable() }
        .map { matching -> matching.card!! }
        .toSet()
        .firstOrNull()
}
