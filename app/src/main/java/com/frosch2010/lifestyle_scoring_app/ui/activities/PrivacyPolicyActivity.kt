package com.frosch2010.lifestyle_scoring_app.ui.activities

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.databinding.ActivityPrivacyPolicyBinding
import com.frosch2010.lifestyle_scoring_app.ui.util.EdgeToEdgeTopBarController
import com.frosch2010.lifestyle_scoring_app.ui.viewmodels.PrivacyPolicyViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.noties.markwon.Markwon

/**
 * Renders localized privacy markdown via Markwon with edge-to-edge scrolling and top bar elevation.
 */
@AndroidEntryPoint
class PrivacyPolicyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrivacyPolicyBinding
    private lateinit var scrollContent: NestedScrollView
    private lateinit var viewModel: PrivacyPolicyViewModel
    private lateinit var markwon: Markwon
    private lateinit var topBarController: EdgeToEdgeTopBarController

    /**
     * @param savedInstanceState Unused; policy text is reloaded through the ViewModel on each entry.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacyPolicyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[PrivacyPolicyViewModel::class.java]

        scrollContent = binding.scrollContent

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

        markwon = Markwon.create(this)
        setupObservers()
        viewModel.load()

        binding.btnBack.setOnClickListener { finish() }

        topBarController.bindScroll()
    }

    /**
     * @return `true` when the device is in night mode for light/dark system bar icon treatment.
     */
    private fun isDarkMode(): Boolean {
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES
    }

    /**
     * Subscribes to [PrivacyPolicyViewModel.uiState] and maps each phase to Markdown or fallback copy.
     */
    private fun setupObservers() {
        viewModel.uiState.observe(this) { state ->
            when (state) {
                PrivacyPolicyViewModel.UiState.Loading -> Unit
                PrivacyPolicyViewModel.UiState.Error -> {
                    binding.txtPrivacyPolicy.text = getString(R.string.privacy_policy_fallback_text)
                }
                is PrivacyPolicyViewModel.UiState.Content -> {
                    markwon.setMarkdown(binding.txtPrivacyPolicy, state.markdown)
                }
            }
        }
    }
}
