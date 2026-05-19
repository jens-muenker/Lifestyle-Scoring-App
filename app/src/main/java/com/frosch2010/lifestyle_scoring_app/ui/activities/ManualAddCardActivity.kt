package com.frosch2010.lifestyle_scoring_app.ui.activities

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.frosch2010.lifestyle_scoring_app.databinding.ActivityManualAddCardBinding
import com.frosch2010.lifestyle_scoring_app.ui.adapters.ManualCardSelectionAdapter
import com.frosch2010.lifestyle_scoring_app.ui.decorations.AdaptiveSpacingItemDecoration
import com.frosch2010.lifestyle_scoring_app.ui.util.EdgeToEdgeTopBarController
import com.frosch2010.lifestyle_scoring_app.ui.viewmodels.ManualAddCardViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Full-screen manual picker with live search, excluding cards already owned by the requesting player.
 */
@AndroidEntryPoint
class ManualAddCardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManualAddCardBinding
    private lateinit var viewModel: ManualAddCardViewModel
    private lateinit var adapter: ManualCardSelectionAdapter

    /** Cached scroll state forwarded to [topBarController] after insets settle. */
    private var isTopBarScrolledState = false

    private lateinit var topBarController: EdgeToEdgeTopBarController

    /**
     * Inflates search UI, applies header/empty padding insets, and wires result propagation on selection.
     *
     * @param savedInstanceState Unused; selection state is ephemeral.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManualAddCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val initialHeaderStart = binding.headerContainer.paddingStart
        val initialHeaderTop = binding.headerContainer.paddingTop
        val initialHeaderEnd = binding.headerContainer.paddingEnd
        val initialHeaderBottom = binding.headerContainer.paddingBottom

        val initialEmptyPaddingStart = binding.layoutEmptyState.paddingStart
        val initialEmptyPaddingTop = binding.layoutEmptyState.paddingTop
        val initialEmptyPaddingEnd = binding.layoutEmptyState.paddingEnd
        val initialEmptyPaddingBottom = binding.layoutEmptyState.paddingBottom

        topBarController = EdgeToEdgeTopBarController(
            activity = this,
            rootForInsets = binding.root,
            toolbarForInsets = binding.toolbar,
            topBarBackgroundView = binding.headerContainer,
            topBarDivider = binding.headerDivider,
            scrollPaddingView = binding.recManualCards,
            isDarkMode = ::isDarkMode,
            isScrolled = { binding.recManualCards.canScrollVertically(-1) },
            extraInsetsApplier = { insets ->
                val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

                binding.headerContainer.setPadding(
                    initialHeaderStart + bars.left,
                    initialHeaderTop,
                    initialHeaderEnd + bars.right,
                    initialHeaderBottom
                )

                binding.layoutEmptyState.setPadding(
                    initialEmptyPaddingStart + bars.left,
                    initialEmptyPaddingTop,
                    initialEmptyPaddingEnd + bars.right,
                    initialEmptyPaddingBottom + bars.bottom
                )
            }
        )
        topBarController.setup()
        topBarController.updateTopBarScrollState(isTopBarScrolledState)

        viewModel = ViewModelProvider(this)[ManualAddCardViewModel::class.java]
        val excludedCardNames = intent
            .getStringArrayListExtra(EXTRA_EXISTING_CARD_NAMES)
            .orEmpty()
            .map { com.frosch2010.lifestyle_scoring_app.utils.CardNameMatcher.cleanText(it) }
            .toSet()
        viewModel.load(excludedCardNames)

        setSupportActionBar(binding.toolbar)
        binding.btnBack.setOnClickListener { finish() }

        adapter = ManualCardSelectionAdapter { selected ->
            val intent = Intent().apply {
                putExtra(EXTRA_SELECTED_CARD, selected.card)
            }
            setResult(RESULT_OK, intent)
            finish()
        }

        binding.recManualCards.layoutManager = LinearLayoutManager(this)
        binding.recManualCards.adapter = adapter
        binding.recManualCards.addItemDecoration(
            AdaptiveSpacingItemDecoration(resources.getDimensionPixelSize(com.frosch2010.lifestyle_scoring_app.R.dimen.spacing_small), true)
        )
        binding.recManualCards.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                updateTopBarScrollState()
            }
        })

        viewModel.uiState.observe(this) { uiState ->
            adapter.submitList(uiState.visibleCards)
            binding.layoutEmptyState.visibility = if (uiState.isEmptyStateVisible) View.VISIBLE else View.GONE
            binding.txtNoResults.visibility = View.GONE
            binding.recManualCards.visibility = if (uiState.isEmptyStateVisible) View.GONE else View.VISIBLE
        }
        binding.recManualCards.post { updateTopBarScrollState() }
        binding.editSearchCard.doOnTextChanged { text, _, _, _ ->
            viewModel.onSearchQueryChanged(text?.toString().orEmpty())
        }
        binding.txtClearSearch.setOnClickListener {
            binding.editSearchCard.setText("")
            viewModel.onSearchQueryChanged("")
        }
    }

    companion object {
        /** Intent extra carrying the serialized [com.frosch2010.lifestyle_scoring_app.models.interfaces.ICard] chosen in this screen. */
        const val EXTRA_SELECTED_CARD = "manual_selected_card"

        /** `StringArrayList` extra of normalized names already present in the requesting player's hand. */
        const val EXTRA_EXISTING_CARD_NAMES = "manual_existing_card_names"
    }

    /**
     * Syncs header elevation with vertical scroll capability of the manual card list.
     */
    private fun updateTopBarScrollState() {
        val shouldUseScrolledStyle = binding.recManualCards.canScrollVertically(-1)
        if (isTopBarScrolledState != shouldUseScrolledStyle) {
            isTopBarScrolledState = shouldUseScrolledStyle
            topBarController.updateTopBarScrollState(isTopBarScrolledState)
        }
    }

    /**
     * @return `true` when night mode is active for status bar contrast inside [topBarController].
     */
    private fun isDarkMode(): Boolean {
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES
    }
}
