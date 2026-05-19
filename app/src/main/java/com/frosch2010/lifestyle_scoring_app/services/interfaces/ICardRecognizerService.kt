package com.frosch2010.lifestyle_scoring_app.services.interfaces

import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage

/**
 * Vision-sidecar port translating camera frames into domain [ICard] selections via OCR pipelines.
 */
interface ICardRecognizerService {

    /**
     * Executes ML Kit text recognition followed by fuzzy dictionary matching against localized labels.
     *
     * @param inputImage Bitmap-backed ML Kit image wrapper produced by the camera pipeline.
     * @return Completed task with a confident card match, or `null` when OCR fails or confidence is low.
     */
    fun process(inputImage: InputImage): Task<ICard?>
}
