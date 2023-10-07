package com.frosch2010.lifestyle_scoring_app.services.impl

import android.os.Handler
import android.os.Looper
import com.frosch2010.fuzzywuzzy_kotlin.FuzzySearch
import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import com.frosch2010.lifestyle_scoring_app.services.interfaces.ICardRecognizerService
import com.frosch2010.lifestyle_scoring_app.utils.normalize
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.Locale
import kotlin.math.abs

/**
 * Recognize title card from images and provide list of card (ids) identified
 */
class CardRecognizerService (private val cards: Map<String, ICard>): ICardRecognizerService {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    /**
     * Put to lower case, remove everything which is not a letter and replace all accented characters per their base letter version<br>
     * (é -> e, à -> a, ...)
     *
     * @param input     text to clean
     * @return "Cleaned" text
     */
    private fun cleanText(input: String): String = input.lowercase(Locale.getDefault()).normalize().filter { it.isLetter() }

    private fun getMatchingResult(input: String): MatchingResult {
        val extractedResult = FuzzySearch.extractOne(input, cards.keys)
        return MatchingResult(input, cards[extractedResult.string], extractedResult.score, extractedResult.string)
    }

    /**
     * Matching result rule
     *
     * @property input              entry text
     * @property bestCardResult     best card based on score
     * @property score              score obtained
     * @property matchingKey        best matching key with input
     */
    class MatchingResult(private val input: String, val bestCardResult: ICard?, private val score: Int, private val matchingKey: String) {

        /**
         * A match is considered valid when :
         * <ul>
         *     <li> matching text length difference is inferior to 4 </li>
         *     <li> score is greater than 75 </li>
         *     <li> best card result is not null </li>
         * <ul>
         *
         */
        fun isAcceptable() = abs(input.length - matchingKey.length) < 4 && score > 75 && bestCardResult != null
    }

    /**
     * Analyze input image and provide result as task
     *
     * @param inputImage        image containing card titles
     * @return                  task of card list identified (as ids)
     */
    override fun process(inputImage: InputImage): Task<List<ICard?>> =
        recognizer.process(inputImage).continueWithTask { recognizeTextTask ->
            val taskCompletionSource = TaskCompletionSource<List<ICard?>>()
            Handler(Looper.getMainLooper()).post {
                val text = recognizeTextTask.result
                taskCompletionSource.setResult(
                    text.textBlocks.asSequence()
                        .map { textBlock -> cleanText(textBlock.text) }
                        .filter { it.length > 2 }
                        .map { getMatchingResult(it) }
                        .filter { matching -> matching.isAcceptable() }
                        .map { matching -> matching.bestCardResult }
                        .toSet()
                        .map { it })
            }
            taskCompletionSource.task
        }
}