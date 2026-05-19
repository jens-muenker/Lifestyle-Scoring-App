package com.frosch2010.lifestyle_scoring_app.ui.activities

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.pm.PackageManager
import androidx.test.core.app.ApplicationProvider
import com.frosch2010.lifestyle_scoring_app.di.DataSourceModule
import com.frosch2010.lifestyle_scoring_app.di.RepositoryModule
import com.frosch2010.lifestyle_scoring_app.di.ServiceModule
import com.frosch2010.lifestyle_scoring_app.models.entities.AnimalCard
import com.frosch2010.lifestyle_scoring_app.models.enums.AnimalTypeEnum
import com.frosch2010.lifestyle_scoring_app.models.enums.CardTypeEnum
import com.frosch2010.lifestyle_scoring_app.ui.util.IntentExtras
import com.frosch2010.lifestyle_scoring_app.ui.util.getSerializableExtraCompat
import com.frosch2010.lifestyle_scoring_app.ui.util.useActivity
import com.frosch2010.lifestyle_scoring_app.utils.ScanResult
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import dagger.hilt.android.testing.UninstallModules
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper

/**
 * Robolectric-backed tests for [ScanCardActivity] (JVM, Android APIs via Robolectric).
 */
@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class, sdk = [36])
@UninstallModules(
    RepositoryModule::class,
    ServiceModule::class,
    DataSourceModule::class
)
class ScanCardActivityRobolectricTest {

    /**
     * Hilt Android rule that installs the test dependency graph for this class.
     */
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    /**
     * Verifies `onScanResult_withFailure_deliversFailureResultAndFinishes()` behavior.
     */
    @Test
    fun onScanResult_withFailure_deliversFailureResultAndFinishes() {
        hiltRule.inject()
        Robolectric.buildActivity(ScanCardActivity::class.java).setup().useActivity { activity ->
            activity.onScanResult(ScanResult(success = false, cards = null))

            val shadowActivity = shadowOf(activity)
            assertEquals(Activity.RESULT_OK, shadowActivity.resultCode)
            val delivered = requireNotNull(
                shadowActivity.resultIntent.getSerializableExtraCompat<ScanResult>(
                    IntentExtras.EXTRA_SCAN_RESULT
                )
            )
            assertEquals(false, delivered.success)
            assertEquals(null, delivered.cards)
            assertTrue(activity.isFinishing || activity.isDestroyed)
        }
    }

    /**
     * Verifies `onScanResult_withSimpleRecognizedCard_deliversCardAndFinishes()` behavior.
     */
    @Test
    fun onScanResult_withSimpleRecognizedCard_deliversCardAndFinishes() {
        hiltRule.inject()
        Robolectric.buildActivity(ScanCardActivity::class.java).setup().useActivity { activity ->
            val card = AnimalCard(CardTypeEnum.ANIMAL, AnimalTypeEnum.DOG)

            activity.onScanResult(ScanResult(success = true, cards = card))

            val shadowActivity = shadowOf(activity)
            assertEquals(Activity.RESULT_OK, shadowActivity.resultCode)
            val delivered = requireNotNull(
                shadowActivity.resultIntent.getSerializableExtraCompat<ScanResult>(
                    IntentExtras.EXTRA_SCAN_RESULT
                )
            )
            assertNotNull(delivered.cards)
            assertEquals(true, delivered.success)
            assertEquals(card, delivered.cards)
            assertTrue(activity.isFinishing || activity.isDestroyed)
        }
    }

    /**
     * Verifies `cameraPermissionDenied_finishesActivity()` behavior.
     */
    @Test
    fun cameraPermissionDenied_finishesActivity() {
        hiltRule.inject()
        shadowApplication().denyPermissions(Manifest.permission.CAMERA)
        Robolectric.buildActivity(ScanCardActivity::class.java).setup().useActivity { activity ->
            activity.onRequestPermissionsResult(
                ScanCardActivity.REQUEST_CODE_PERMISSIONS,
                arrayOf(Manifest.permission.CAMERA),
                intArrayOf(PackageManager.PERMISSION_DENIED)
            )
            ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
            assertTrue(activity.isFinishing || activity.isDestroyed)
        }
    }

    /**
     * Verifies `cameraPermissionGranted_activityRemainsAlive()` behavior.
     */
    @Test
    fun cameraPermissionGranted_activityRemainsAlive() {
        hiltRule.inject()
        shadowApplication().grantPermissions(Manifest.permission.CAMERA)
        Robolectric.buildActivity(ScanCardActivity::class.java).setup().useActivity { activity ->
            ShadowLooper.runUiThreadTasksIncludingDelayedTasks()
            assertFalse(activity.isFinishing)
        }
    }

    private fun shadowApplication() = shadowOf(
        ApplicationProvider.getApplicationContext<Application>()
    )
}

