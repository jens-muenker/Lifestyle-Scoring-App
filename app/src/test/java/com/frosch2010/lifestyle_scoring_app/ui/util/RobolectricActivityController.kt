package com.frosch2010.lifestyle_scoring_app.ui.util

import android.app.Activity
import org.robolectric.android.controller.ActivityController

/**
 * Runs [block] with the activity from this controller, then [ActivityController.destroy]s the
 * controller so window/surface resources are torn down (reduces Robolectric CloseGuard noise).
 */
fun <T : Activity> ActivityController<T>.useActivity(block: (T) -> Unit) {
    try {
        block(get())
    } finally {
        destroy()
    }
}
