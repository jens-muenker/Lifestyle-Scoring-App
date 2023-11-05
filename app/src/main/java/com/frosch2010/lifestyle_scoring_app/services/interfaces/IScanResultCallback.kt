package com.frosch2010.lifestyle_scoring_app.services.interfaces

import com.frosch2010.lifestyle_scoring_app.utils.ScanResult

/**
 * This interface is used to return the result of a card scan to the calling activity.
 * @author Jens Münker
 */
interface IScanResultCallback {

    /**
     * This method is called when a card is scanned
     * and the result is ready to be returned to the calling activity.
     * @param result The result of the card scan.
     */
    fun onScanResult(result: ScanResult)
}