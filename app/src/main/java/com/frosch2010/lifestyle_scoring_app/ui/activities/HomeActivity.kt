package com.frosch2010.lifestyle_scoring_app.ui.activities

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.databinding.ActivityHomeBinding
import com.google.android.material.color.MaterialColors

/**
 * Marketing-style landing screen with hero animation, deep link to official rules, and entry into scoring flow.
 */
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    /** Tracks whether the collapsing top bar should use an opaque surface color vs transparent overlay. */
    private var isTopBarElevatedState = false

    /**
     * Inflates content, applies edge-to-edge insets, wires navigation to settings and main scoring activities.
     *
     * @param savedInstanceState Unused; home is stateless between launches.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupEdgeToEdge()
        applyStatusBarColor()

        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
        styleToolbarOverflowIcon()

        binding.btnCalculatePoints.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.btnGameRules.setOnClickListener {
            openWebsiteInBrowser()
        }

        binding.scrollContent.setOnScrollChangeListener { _, _, _, _, _ ->
            updateTopBarStyle()
        }
        binding.scrollContent.post { updateTopBarStyle() }

        playEnterAnimations()

        // Tint the settings (gear) icon for light/dark themes.
        // The menu icon vector has no theme tint anymore, so we set it explicitly.
        binding.toolbar.post { styleSettingsMenuIcon() }
    }

    /**
     * Opens the configured game rules URL in the user's default browser when a handler exists.
     */
    private fun openWebsiteInBrowser() {
        val url = getString(R.string.home_game_rules_url)
        val webpage = url.toUri()
        val intent = Intent(Intent.ACTION_VIEW, webpage)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    /**
     * Enables drawing behind system bars, expands the toolbar into the status bar inset, and pads scroll content.
     */
    private fun setupEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.navigationBarColor = Color.TRANSPARENT
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightNavigationBars = !isDarkMode()
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = !isDarkMode()

        val initialToolbarPaddingStart = binding.toolbar.paddingStart
        val initialToolbarPaddingTop = binding.toolbar.paddingTop
        val initialToolbarPaddingEnd = binding.toolbar.paddingEnd
        val initialToolbarPaddingBottom = binding.toolbar.paddingBottom
        val initialToolbarHeight = binding.toolbar.layoutParams.height

        val initialScrollPaddingStart = binding.scrollContent.paddingStart
        val initialScrollPaddingTop = binding.scrollContent.paddingTop
        val initialScrollPaddingEnd = binding.scrollContent.paddingEnd
        val initialScrollPaddingBottom = binding.scrollContent.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            binding.toolbar.setPadding(
                initialToolbarPaddingStart + bars.left,
                initialToolbarPaddingTop + bars.top,
                initialToolbarPaddingEnd + bars.right,
                initialToolbarPaddingBottom
            )
            binding.toolbar.layoutParams = binding.toolbar.layoutParams.apply {
                height = initialToolbarHeight + bars.top
            }

            binding.scrollContent.setPadding(
                initialScrollPaddingStart + bars.left,
                initialScrollPaddingTop,
                initialScrollPaddingEnd + bars.right,
                initialScrollPaddingBottom + bars.bottom
            )

            applyStatusBarColor()
            insets
        }
    }

    /**
     * Sets [android.view.Window.statusBarColor] to transparent or surface color matching toolbar elevation.
     */
    private fun applyStatusBarColor() {
        val topBarColor = if (isTopBarElevatedState) {
            MaterialColors.getColor(binding.toolbar, com.google.android.material.R.attr.colorSurfaceContainer)
        } else {
            Color.TRANSPARENT
        }
        window.statusBarColor = topBarColor
    }

    /**
     * Toggles toolbar background and status bar color when the scroll view leaves or returns to the origin offset.
     */
    private fun updateTopBarStyle() {
        val shouldBeElevated = binding.scrollContent.canScrollVertically(1) || binding.scrollContent.canScrollVertically(-1)
        if (isTopBarElevatedState == shouldBeElevated) {
            return
        }
        isTopBarElevatedState = shouldBeElevated

        val topBarColor = if (shouldBeElevated) {
            MaterialColors.getColor(binding.toolbar, com.google.android.material.R.attr.colorSurfaceContainer)
        } else {
            Color.TRANSPARENT
        }
        binding.toolbar.setBackgroundColor(topBarColor)
        applyStatusBarColor()
        styleSettingsMenuIcon()
    }

    /**
     * Applies on-surface-variant tint and alpha to the three-dot overflow affordance for theme parity.
     */
    private fun styleToolbarOverflowIcon() {
        val overflow = binding.toolbar.overflowIcon ?: return
        val wrapped = DrawableCompat.wrap(overflow.mutate())
        DrawableCompat.setTint(
            wrapped,
            MaterialColors.getColor(
                binding.toolbar,
                com.google.android.material.R.attr.colorOnSurfaceVariant
            )
        )
        wrapped.alpha = TOOLBAR_OVERFLOW_ICON_ALPHA
        binding.toolbar.overflowIcon = wrapped
    }

    /**
     * Tints the toolbar settings action icon to `colorOnSurface` with tuned alpha for light/dark readability.
     */
    private fun styleSettingsMenuIcon() {
        val menuItem = binding.toolbar.menu.findItem(R.id.action_settings) ?: return
        val icon = menuItem.icon ?: return
        val wrapped = DrawableCompat.wrap(icon.mutate())
        DrawableCompat.setTint(
            wrapped,
            MaterialColors.getColor(
                binding.toolbar,
                com.google.android.material.R.attr.colorOnSurface
            )
        )
        wrapped.alpha = TOOLBAR_SETTINGS_ICON_ALPHA
        menuItem.icon = wrapped
    }

    /**
     * Staggered fade/slide entrance for hero icon, copy, and primary CTAs on first paint.
     */
    private fun playEnterAnimations() {
        val animatedViews = listOf(
            binding.heroIconContainer,
            binding.txtHeadline,
            binding.txtTagline,
            binding.btnCalculatePoints,
            binding.btnGameRules
        )

        animatedViews.forEachIndexed { index, view ->
            view.alpha = 0f
            view.translationY = ENTER_ANIM_TRANSLATION_Y_PX
            view.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay((index * ENTER_ANIM_STAGGER_DELAY_MS) + ENTER_ANIM_INITIAL_DELAY_MS)
                .setDuration(ENTER_ANIM_DURATION_MS)
                .start()
        }
    }

    private companion object {
        /** Base delay before the first hero element animates in. */
        private const val ENTER_ANIM_INITIAL_DELAY_MS = 40L

        /** Additional delay applied per element index for staggered motion. */
        private const val ENTER_ANIM_STAGGER_DELAY_MS = 70L

        /** Duration of each element's alpha/translation tween. */
        private const val ENTER_ANIM_DURATION_MS = 280L

        /** Starting vertical offset in pixels for upward slide entrance. */
        private const val ENTER_ANIM_TRANSLATION_Y_PX = 20f

        /** Alpha (0-255) applied to overflow menu drawable tint. */
        private const val TOOLBAR_OVERFLOW_ICON_ALPHA = 210

        /** Alpha (0-255) applied to settings action drawable tint. */
        private const val TOOLBAR_SETTINGS_ICON_ALPHA = 220
    }

    /**
     * @return `true` when the device is currently in night mode for icon and status bar treatment.
     */
    private fun isDarkMode(): Boolean {
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES
    }
}
