package com.frosch2010.lifestyle_scoring_app.ui.activities

import com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard
import com.frosch2010.lifestyle_scoring_app.services.interfaces.ICardRecognizerService
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import javax.inject.Inject

/**
 * Test double for [ICardRecognizerService] used in Hilt [TestBaseBindingsModule]; always completes with `null` (no OCR match).
 */
class FakeCardRecognizerService @Inject constructor() : ICardRecognizerService {
    override fun process(inputImage: InputImage): Task<ICard?> = Tasks.forResult(null)
}
