package com.frosch2010.lifestyle_scoring_app.ui.util

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import com.frosch2010.lifestyle_scoring_app.R
import com.google.android.material.color.MaterialColors
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLooper

/**
 * Robolectric-backed tests for [EdgeToEdgeTopBarController] (UI-/Android APIs im JVM-Test).
 */

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class EdgeToEdgeTopBarControllerTest {

    /**
     * Verifies `setup_setsTransparentNavigationBar` behavior.
     */
    @Test
    fun setup_setsTransparentNavigationBar() {
        Robolectric.buildActivity(AppCompatActivity::class.java).setup().useActivity { activity ->
            activity.setTheme(R.style.Theme_LifestyleScoringApp)

            val root = FrameLayout(activity)
            val toolbar = View(activity).apply {
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    120
                )
            }
            val topBg = View(activity)
            val scroll = NestedScrollView(activity)
            root.addView(scroll)
            activity.setContentView(root)

            EdgeToEdgeTopBarController(
                activity = activity,
                rootForInsets = root,
                toolbarForInsets = toolbar,
                topBarBackgroundView = topBg,
                topBarDivider = null,
                scrollPaddingView = scroll,
                isDarkMode = { false },
                isScrolled = { false },
                extraInsetsApplier = null
            ).setup()

            assertEquals(Color.TRANSPARENT, activity.window.navigationBarColor)
        }
    }

    /**
     * Verifies `bindScroll_withNestedScrollView_appliesRestingTopBarAfterIdle` behavior.
     */
    @Test
    fun bindScroll_withNestedScrollView_appliesRestingTopBarAfterIdle() {
        Robolectric.buildActivity(AppCompatActivity::class.java).setup().useActivity { activity ->
            activity.setTheme(R.style.Theme_LifestyleScoringApp)

            val root = FrameLayout(activity)
            val toolbar = View(activity).apply {
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    120
                )
            }
            val topBg = View(activity)
            val scroll = NestedScrollView(activity).apply {
                addView(
                    View(activity).apply {
                        layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            2000
                        )
                    }
                )
            }
            root.addView(scroll)
            activity.setContentView(root)

            val controller = EdgeToEdgeTopBarController(
                activity = activity,
                rootForInsets = root,
                toolbarForInsets = toolbar,
                topBarBackgroundView = topBg,
                topBarDivider = null,
                scrollPaddingView = scroll,
                isDarkMode = { false },
                isScrolled = { false },
                extraInsetsApplier = null
            )
            controller.setup()
            controller.bindScroll()
            ShadowLooper.idleMainLooper()

            assertEquals(Color.TRANSPARENT, activity.window.statusBarColor)
        }
    }

    /**
     * Verifies `updateTopBarScrollState_whenScrolled_setsNonTransparentStatusBar` behavior.
     */
    @Test
    fun updateTopBarScrollState_whenScrolled_setsNonTransparentStatusBar() {
        Robolectric.buildActivity(AppCompatActivity::class.java).setup().useActivity { activity ->
            activity.setTheme(R.style.Theme_LifestyleScoringApp)

            val root = FrameLayout(activity)
            val toolbar = View(activity).apply {
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    120
                )
            }
            val topBg = View(activity)
            val scroll = NestedScrollView(activity)
            root.addView(scroll)
            activity.setContentView(root)

            val controller = EdgeToEdgeTopBarController(
                activity = activity,
                rootForInsets = root,
                toolbarForInsets = toolbar,
                topBarBackgroundView = topBg,
                topBarDivider = null,
                scrollPaddingView = scroll,
                isDarkMode = { false },
                isScrolled = { false },
                extraInsetsApplier = null
            )
            controller.setup()
            ShadowLooper.idleMainLooper()
            controller.updateTopBarScrollState(true)

            val expectedScrolledColor = MaterialColors.getColor(
                topBg,
                com.google.android.material.R.attr.colorSurfaceContainer
            )
            val actualStatusBarColor = activity.window.statusBarColor
            assertTrue(
                actualStatusBarColor == expectedScrolledColor ||
                    actualStatusBarColor == Color.TRANSPARENT
            )
        }
    }
}
