package com.frosch2010.lifestyle_scoring_app.services.interfaces

import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage

interface ICardRecognizerService {
    fun process(inputImage: InputImage): Task<ICard?>
}