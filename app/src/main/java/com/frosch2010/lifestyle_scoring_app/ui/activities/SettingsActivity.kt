package com.frosch2010.lifestyle_scoring_app.ui.activities

import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.databinding.ActivitySettingsBinding
import com.frosch2010.lifestyle_scoring_app.ui.util.EdgeToEdgeTopBarController
import com.frosch2010.lifestyle_scoring_app.ui.viewmodels.SettingsViewModel
import com.frosch2010.lifestyle_scoring_app.utils.LanguageTags
import com.google.android.material.color.MaterialColors
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

/**
 * App settings hub: language selection with delayed navigation rows, build info, and legal deep links.
 */
@AndroidEntryPoint
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var scrollContent: NestedScrollView
    private lateinit var languageRow: View
    private lateinit var languageAutoComplete: MaterialAutoCompleteTextView
    private lateinit var viewModel: SettingsViewModel
    private lateinit var topBarController: EdgeToEdgeTopBarController

    /** Posted runnable so privacy navigation fires after ripple animation completes. */
    private var pendingPrivacyNavigation: Runnable? = null

    /** Posted runnable so licenses navigation fires after ripple animation completes. */
    private var pendingLicensesNavigation: Runnable? = null

    /**
     * Inflates UI, wires edge-to-edge controller, debounced legal navigation, and language dropdown state.
     *
     * @param savedInstanceState Unused; ViewModel reloads persisted selections on [viewModel.load].
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[SettingsViewModel::class.java]

        scrollContent = binding.scrollContent
        languageRow = binding.rowLanguageSelect
        languageAutoComplete = binding.autoCompleteLanguage

        topBarController = EdgeToEdgeTopBarController(
            activity = this,
            rootForInsets = binding.root,
            toolbarForInsets = binding.toolbar,
            topBarBackgroundView = binding.toolbar,
            topBarDivider = binding.topBarDivider,
            scrollPaddingView = scrollContent,
            isDarkMode = ::isDarkMode,
            isScrolled = { scrollContent.scrollY > 0 }
        )
        topBarController.setup()
        setupObservers()

        binding.btnBack.setOnClickListener { finish() }

        binding.rowPrivacyPolicy.setOnClickListener { clickedView ->
            // Let the ripple/press state play out before navigating.
            // Also guard against accidental double taps.
            if (!clickedView.isEnabled) return@setOnClickListener
            clickedView.isEnabled = false
            pendingPrivacyNavigation?.let { clickedView.removeCallbacks(it) }
            val task = Runnable { startActivity(Intent(this, PrivacyPolicyActivity::class.java)) }
            pendingPrivacyNavigation = task
            clickedView.postDelayed(task, NAVIGATION_DELAY_MS)
        }

        binding.rowOpenSourceLicenses.setOnClickListener { clickedView ->
            // Let the ripple/press state play out before navigating.
            // Also guard against accidental double taps.
            if (!clickedView.isEnabled) return@setOnClickListener
            clickedView.isEnabled = false
            pendingLicensesNavigation?.let { clickedView.removeCallbacks(it) }
            val task = Runnable { startActivity(Intent(this, LicensesActivity::class.java)) }
            pendingLicensesNavigation = task
            clickedView.postDelayed(task, NAVIGATION_DELAY_MS)
        }

        setupLanguageDropdown()
        viewModel.load()

        topBarController.bindScroll()
    }

    /**
     * Re-enables legal rows after returning from child screens so delayed navigation can run again.
     */
    override fun onResume() {
        super.onResume()
        binding.rowPrivacyPolicy.isEnabled = true
        binding.rowOpenSourceLicenses.isEnabled = true
    }

    /**
     * Cancels pending navigation callbacks to avoid leaking posted work across activity transitions.
     */
    override fun onPause() {
        super.onPause()
        pendingPrivacyNavigation?.let { binding.rowPrivacyPolicy.removeCallbacks(it) }
        pendingLicensesNavigation?.let { binding.rowOpenSourceLicenses.removeCallbacks(it) }
        pendingPrivacyNavigation = null
        pendingLicensesNavigation = null
    }

    /**
     * Observes build metadata, persisted language tag, and one-shot restart events after locale changes.
     */
    private fun setupObservers() {
        viewModel.buildInfo.observe(this) { buildInfo ->
            val versionName = buildInfo?.versionName ?: getString(R.string.settings_build_unknown)
            val versionCode = buildInfo?.versionCode ?: 0L
            binding.txtBuildInfo.text = getString(
                R.string.settings_build_info,
                versionName,
                versionCode
            )
        }

        viewModel.persistedLanguageTag.observe(this) { tag ->
            val index = when (tag) {
                LanguageTags.DE -> 1
                LanguageTags.EN -> 2
                else -> 0
            }
            // Keep label in sync even after external locale changes.
            val labels = arrayOf(
                getString(R.string.settings_language_system),
                getString(R.string.settings_language_de),
                getString(R.string.settings_language_en)
            )
            languageAutoComplete.setText(labels[index], false)
        }

        viewModel.events.observe(this) { event ->
            when (event.consume()) {
                SettingsViewModel.UiEvent.RestartActivity -> {
                    val fresh = Intent(this, SettingsActivity::class.java)
                    finish()
                    startActivity(fresh)
                    overridePendingTransition(0, 0)
                }
                null -> Unit
            }
        }
    }

    /**
     * Binds BCP-47 tags to localized labels, applies rounded popup chrome, and forwards selections to the ViewModel.
     */
    private fun setupLanguageDropdown() {
        val tags = arrayOf(LanguageTags.SYSTEM, LanguageTags.DE, LanguageTags.EN)
        val labels = arrayOf(
            getString(R.string.settings_language_system),
            getString(R.string.settings_language_de),
            getString(R.string.settings_language_en)
        )
        val adapter = ArrayAdapter(
            this,
            R.layout.lst_item_language_dropdown,
            android.R.id.text1,
            labels
        )
        languageAutoComplete.setAdapter(adapter)
        applyRoundedLanguageDropdownBackground(languageAutoComplete)

        val textInputLayout: TextInputLayout = binding.textInputLanguage
        textInputLayout.isHintAnimationEnabled = false
        languageRow.visibility = View.INVISIBLE
        languageRow.post {
            languageRow.visibility = View.VISIBLE
            textInputLayout.isHintAnimationEnabled = true
        }

        languageRow.setOnClickListener {
            languageAutoComplete.requestFocus()
            languageAutoComplete.showDropDown()
        }

        languageAutoComplete.setOnItemClickListener { _, _, position, _ ->
            val tag = tags[position]
            viewModel.onLanguageSelected(tag)
        }
    }

    private companion object {
        /** Delay between row tap and `startActivity` so Material ripple finishes visibly. */
        private const val NAVIGATION_DELAY_MS = 160L
    }

    /**
     * Replaces the default rectangular popup drawable with a rounded surface/outline pairing aligned to M3.
     *
     * @param autoComplete Field whose drop-down background should be themed.
     */
    private fun applyRoundedLanguageDropdownBackground(autoComplete: MaterialAutoCompleteTextView) {
        val radius = resources.getDimension(R.dimen.dropdown_popup_corner_radius)
        val fill = MaterialColors.getColor(
            autoComplete,
            com.google.android.material.R.attr.colorSurfaceContainerHigh
        )
        val outline = MaterialColors.getColor(
            autoComplete,
            com.google.android.material.R.attr.colorOutlineVariant
        )
        val strokePx = (resources.displayMetrics.density * 1f).toInt().coerceAtLeast(1)
        val bg = GradientDrawable().apply {
            cornerRadius = radius
            setColor(fill)
            setStroke(strokePx, outline)
        }
        autoComplete.setDropDownBackgroundDrawable(bg)
    }

    /**
     * @return `true` when night mode is active for edge-to-edge status bar icon contrast.
     */
    private fun isDarkMode(): Boolean {
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES
    }
}
