package com.frosch2010.lifestyle_scoring_app.services.interfaces

import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage

/**
 * This interface is used to access the card recognizer service.
 * @author Jens Münker
 */
interface ICardRecognizerService {

    /**
     * This method is used to recognize a card from an image.
     * @param inputImage The image to recognize the card from.
     * @return The recognized card.
     */
    fun process(inputImage: InputImage): Task<ICard?>
}