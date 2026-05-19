package com.frosch2010.lifestyle_scoring_app.ui.util

import android.app.Activity
import android.graphics.Color
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.widget.NestedScrollView
import com.google.android.material.color.MaterialColors

/**
 * Encapsulates edge-to-edge window setup, toolbar inset expansion, and scroll-driven top bar elevation.
 *
 * Activities share this helper to avoid duplicating `WindowCompat`, padding math, and Material surface colors
 * while keeping scroll detection pluggable via lambdas.
 *
 * @param activity Hosting window whose system bars and colors are mutated.
 * @param rootForInsets View receiving `OnApplyWindowInsetsListener` (typically the content root).
 * @param toolbarForInsets `MaterialToolbar` (or container) whose height/padding absorb the status bar inset.
 * @param topBarBackgroundView Solid surface behind the toolbar whose color toggles with scroll state.
 * @param topBarDivider Optional hairline below the top bar shown only when elevated.
 * @param scrollPaddingView Scrollable region receiving horizontal and bottom system bar padding.
 * @param isDarkMode Supplier used to pick light vs dark system bar icon appearance.
 * @param isScrolled Supplier returning whether content is scrolled far enough to elevate the top bar.
 * @param extraInsetsApplier Optional hook for screens needing additional inset consumers (fab padding, etc.).
 */
class EdgeToEdgeTopBarController(
    private val activity: Activity,
    private val rootForInsets: View,
    private val toolbarForInsets: View,
    private val topBarBackgroundView: View,
    private val topBarDivider: View?,
    private val scrollPaddingView: View,
    private val isDarkMode: () -> Boolean,
    private val isScrolled: () -> Boolean,
    private val extraInsetsApplier: ((WindowInsetsCompat) -> Unit)? = null
) {
    /**
     * Last scroll-derived elevation state applied to avoid redundant theme churn.
     */
    private var isTopBarScrolledState = false

    /**
     * Whether the initial resting top bar style has been applied at least once after inset dispatch.
     */
    private var hasAppliedTopBarStyle = false

    /**
     * Enables drawing behind system bars, transparent navigation, and applies the initial resting top bar look.
     */
    fun setup() {
        WindowCompat.setDecorFitsSystemWindows(activity.window, false)
        activity.window.navigationBarColor = Color.TRANSPARENT
        WindowInsetsControllerCompat(activity.window, activity.window.decorView).apply {
            isAppearanceLightNavigationBars = !isDarkMode()
            isAppearanceLightStatusBars = !isDarkMode()
        }

        isTopBarScrolledState = false
        hasAppliedTopBarStyle = false
        updateTopBarScrollState(scrolled = false)

        val initialToolbarPaddingStart = toolbarForInsets.paddingStart
        val initialToolbarPaddingTop = toolbarForInsets.paddingTop
        val initialToolbarPaddingEnd = toolbarForInsets.paddingEnd
        val initialToolbarPaddingBottom = toolbarForInsets.paddingBottom
        val initialToolbarHeight = toolbarForInsets.layoutParams.height

        val initialScrollPaddingStart = scrollPaddingView.paddingStart
        val initialScrollPaddingTop = scrollPaddingView.paddingTop
        val initialScrollPaddingEnd = scrollPaddingView.paddingEnd
        val initialScrollPaddingBottom = scrollPaddingView.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(rootForInsets) { _, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            toolbarForInsets.setPadding(
                initialToolbarPaddingStart + bars.left,
                initialToolbarPaddingTop + bars.top,
                initialToolbarPaddingEnd + bars.right,
                initialToolbarPaddingBottom
            )
            toolbarForInsets.layoutParams = toolbarForInsets.layoutParams.apply {
                height = initialToolbarHeight + bars.top
            }

            scrollPaddingView.setPadding(
                initialScrollPaddingStart + bars.left,
                initialScrollPaddingTop,
                initialScrollPaddingEnd + bars.right,
                initialScrollPaddingBottom + bars.bottom
            )

            extraInsetsApplier?.invoke(insets)

            insets
        }
    }

    /**
     * Subscribes to scroll changes on [scrollPaddingView] so the top bar elevation tracks user interaction.
     */
    fun bindScroll() {
        when (scrollPaddingView) {
            is NestedScrollView -> {
                scrollPaddingView.setOnScrollChangeListener(
                    NestedScrollView.OnScrollChangeListener { _, _, _, _, _ ->
                        updateTopBarScrollState(isScrolled())
                    }
                )
                scrollPaddingView.post { updateTopBarScrollState(isScrolled()) }
            }
            else -> {
                scrollPaddingView.post { updateTopBarScrollState(isScrolled()) }
            }
        }
    }

    /**
     * Forces the top bar visual state to match [scrolled], no-oping when already synchronized after first paint.
     *
     * @param scrolled `true` when content has left the resting position and the surface should elevate.
     */
    fun updateTopBarScrollState(scrolled: Boolean) {
        if (hasAppliedTopBarStyle && isTopBarScrolledState == scrolled) return
        isTopBarScrolledState = scrolled
        hasAppliedTopBarStyle = true
        applyTopBarStyle(scrolled)
    }

    /**
     * Applies Material surface colors and divider visibility for the requested scroll elevation.
     *
     * @param scrolled Whether the top region should appear as an elevated surface vs transparent overlay.
     */
    private fun applyTopBarStyle(scrolled: Boolean) {
        val surfaceContainer =
            MaterialColors.getColor(topBarBackgroundView, com.google.android.material.R.attr.colorSurfaceContainer)
        val topBarColor = if (scrolled) surfaceContainer else Color.TRANSPARENT
        topBarBackgroundView.setBackgroundColor(topBarColor)
        topBarDivider?.visibility = if (scrolled) View.VISIBLE else View.GONE
        activity.window.statusBarColor = topBarColor
    }
}
