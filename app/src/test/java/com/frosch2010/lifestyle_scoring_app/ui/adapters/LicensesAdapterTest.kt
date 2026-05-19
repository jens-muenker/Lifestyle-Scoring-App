package com.frosch2010.lifestyle_scoring_app.ui.adapters

import android.view.ContextThemeWrapper
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.domain.model.LicenseEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper

/**
 * Unit tests for [LicensesAdapter].
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class LicensesAdapterTest {

    /**
     * Verifies `bind_withProjectAndLicenseUrls_showsClickableUrlRows()` behavior.
     */
    @Test
    fun bind_withProjectAndLicenseUrls_showsClickableUrlRows() {
        // Arrange
        val adapter = LicensesAdapter()
        adapter.submitList(
            listOf(
                LicenseEntry(
                    projectName = "Retrofit",
                    version = "2.11.0",
                    projectUrl = "https://square.github.io/retrofit/",
                    licenseName = "Apache-2.0",
                    licenseUrl = "https://www.apache.org/licenses/LICENSE-2.0"
                )
            )
        )
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()

        val context = ContextThemeWrapper(
            ApplicationProvider.getApplicationContext(),
            R.style.Theme_LifestyleScoringApp
        )
        val parent = FrameLayout(context)

        // Act
        val holder = adapter.onCreateViewHolder(parent, adapter.getItemViewType(0))
        adapter.onBindViewHolder(holder, 0)

        // Assert
        val projectUrl = holder.itemView.findViewById<TextView>(R.id.txt_license_url)
        val licenseUrl = holder.itemView.findViewById<TextView>(R.id.txt_license_license_url)
        assertEquals(View.VISIBLE, projectUrl.visibility)
        assertEquals(View.VISIBLE, licenseUrl.visibility)
        assertNotNull(projectUrl.movementMethod)
        assertNotNull(licenseUrl.movementMethod)
    }

    /**
     * Verifies `bind_withoutUrls_hidesUrlRows()` behavior.
     */
    @Test
    fun bind_withoutUrls_hidesUrlRows() {
        // Arrange
        val adapter = LicensesAdapter()
        adapter.submitList(
            listOf(
                LicenseEntry(
                    projectName = "NoUrlLib",
                    version = "",
                    projectUrl = "",
                    licenseName = "MIT",
                    licenseUrl = ""
                )
            )
        )
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()

        val context = ContextThemeWrapper(
            ApplicationProvider.getApplicationContext(),
            R.style.Theme_LifestyleScoringApp
        )
        val parent = FrameLayout(context)

        // Act
        val holder = adapter.onCreateViewHolder(parent, adapter.getItemViewType(0))
        adapter.onBindViewHolder(holder, 0)

        // Assert
        val projectUrl = holder.itemView.findViewById<TextView>(R.id.txt_license_url)
        val licenseUrl = holder.itemView.findViewById<TextView>(R.id.txt_license_license_url)
        assertEquals(View.GONE, projectUrl.visibility)
        assertEquals(View.GONE, licenseUrl.visibility)
    }

    /**
     * Segment view types are defined in [LicensesAdapter] companion (single / first / middle / last).
     */
    @Test
    fun getItemViewType_singleEntry_returnsSingleSegmentType() {
        val adapter = LicensesAdapter()
        adapter.submitList(
            listOf(
                LicenseEntry(
                    projectName = "SoloLib",
                    version = "1",
                    projectUrl = "",
                    licenseName = "MIT",
                    licenseUrl = ""
                )
            )
        )
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()

        assertEquals(0, adapter.getItemViewType(0))
    }

    /**
     * Verifies `getItemViewType_threeEntries_returnsFirstMiddleLastSegmentTypes` behavior.
     */
    @Test
    fun getItemViewType_threeEntries_returnsFirstMiddleLastSegmentTypes() {
        val template = LicenseEntry(
            projectName = "Lib",
            version = "1",
            projectUrl = "",
            licenseName = "MIT",
            licenseUrl = ""
        )
        val adapter = LicensesAdapter()
        adapter.submitList(
            listOf(
                template.copy(projectName = "A"),
                template.copy(projectName = "B"),
                template.copy(projectName = "C")
            )
        )
        ShadowLooper.runUiThreadTasksIncludingDelayedTasks()

        assertEquals(1, adapter.getItemViewType(0))
        assertEquals(2, adapter.getItemViewType(1))
        assertEquals(3, adapter.getItemViewType(2))
    }
}
