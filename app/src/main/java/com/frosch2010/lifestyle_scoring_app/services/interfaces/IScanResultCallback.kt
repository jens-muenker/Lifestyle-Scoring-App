package com.frosch2010.lifestyle_scoring_app.services.interfaces

import com.frosch2010.lifestyle_scoring_app.utils.ScanResult

interface IScanResultCallback {
    fun onScanResult(result: ScanResult)
}