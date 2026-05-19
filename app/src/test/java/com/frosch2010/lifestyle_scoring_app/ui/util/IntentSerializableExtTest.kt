package com.frosch2010.lifestyle_scoring_app.ui.util

import android.content.Intent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.Serializable

/**
 * Robolectric-backed tests for [IntentSerializableExt]: [getSerializableExtraCompat] on SDK 29 vs API 33+ (`@Config` per test).
 */

@RunWith(RobolectricTestRunner::class)
class IntentSerializableExtTest {

    private data class Payload(val n: Int) : Serializable

    @Test
    @Config(sdk = [36])
    fun getSerializableExtraCompat_belowTiramisu_returnsCastValue() {
        val intent = Intent().putExtra("k", Payload(5))

        val read: Payload? = intent.getSerializableExtraCompat("k")

        assertEquals(5, read?.n)
    }

    @Test
    @Config(sdk = [36])
    fun getSerializableExtraCompat_belowTiramisu_returnsNullWhenMissing() {
        val intent = Intent()

        val read: Payload? = intent.getSerializableExtraCompat("missing")

        assertNull(read)
    }

    @Test
    @Config(sdk = [36])
    fun getSerializableExtraCompat_api33Plus_returnsTypedExtra() {
        val intent = Intent().putExtra("k", Payload(7))

        val read: Payload? = intent.getSerializableExtraCompat("k")

        assertEquals(7, read?.n)
    }
}
