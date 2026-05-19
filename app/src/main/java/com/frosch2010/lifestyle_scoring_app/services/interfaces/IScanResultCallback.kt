package com.frosch2010.lifestyle_scoring_app.services.interfaces

import com.frosch2010.lifestyle_scoring_app.utils.ScanResult

/**
 * Callback surface for asynchronous scan pipelines to hand results back to hosting activities.
 */
interface IScanResultCallback {

    /**
     * Invoked on the main thread once OCR, fuzzy matching, or user cancellation has settled.
     *
     * @param result Serializable payload describing success, failure, or cancellation semantics.
     */
    fun onScanResult(result: ScanResult)
}
