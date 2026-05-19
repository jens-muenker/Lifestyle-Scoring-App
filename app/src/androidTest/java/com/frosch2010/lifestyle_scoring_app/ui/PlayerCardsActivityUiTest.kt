package com.frosch2010.lifestyle_scoring_app.ui

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.lifecycle.Lifecycle
import com.frosch2010.lifestyle_scoring_app.ui.activities.PlayerCardsActivity
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI test suite for `PlayerCardsActivity` precondition handling.
 */
@RunWith(AndroidJUnit4::class)
class PlayerCardsActivityUiTest {

    /**
     * Verifies that the activity closes when the required player extra is missing.
     */
    @Test
    fun playerCards_withoutPlayerExtra_finishesImmediately() {
        val intent = Intent(
            androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext,
            PlayerCardsActivity::class.java
        )
        ActivityScenario.launch<PlayerCardsActivity>(intent).use { scenario ->
            assertTrue(scenario.state == Lifecycle.State.DESTROYED)
        }
    }
}

