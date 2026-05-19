package com.frosch2010.lifestyle_scoring_app.ui.activities

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.databinding.ActivityLicensesBinding
import com.frosch2010.lifestyle_scoring_app.ui.adapters.LicensesAdapter
import com.frosch2010.lifestyle_scoring_app.ui.decorations.AdaptiveSpacingItemDecoration
import com.frosch2010.lifestyle_scoring_app.ui.util.EdgeToEdgeTopBarController
import com.frosch2010.lifestyle_scoring_app.ui.viewmodels.LicensesViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Legal attribution screen listing bundled OSS dependencies with loading, empty, and error affordances.
 */
@AndroidEntryPoint
class LicensesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLicensesBinding
    private lateinit var viewModel: LicensesViewModel
    private lateinit var toolbar: View
    private lateinit var topBarDivider: View
    private lateinit var emptyState: View
    private lateinit var licensesRecycler: RecyclerView
    private lateinit var topBarController: EdgeToEdgeTopBarController
    private lateinit var adapter: LicensesAdapter

    /** Mirrors whether the recycler can scroll up so the top bar elevation stays in sync. */
    private var isTopBarScrolledState = false

    /**
     * Inflates the layout, wires [LicensesAdapter], edge-to-edge insets, and [LicensesViewModel] observers.
     *
     * @param savedInstanceState Restored bundle; unused because state lives in the ViewModel.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLicensesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[LicensesViewModel::class.java]

        toolbar = binding.toolbar
        topBarDivider = binding.topBarDivider
        emptyState = binding.emptyLicensesState
        licensesRecycler = binding.recLicenses
        adapter = LicensesAdapter()

        licensesRecycler.layoutManager = LinearLayoutManager(this)
        licensesRecycler.adapter = adapter
        licensesRecycler.addItemDecoration(
            AdaptiveSpacingItemDecoration(resources.getDimensionPixelSize(R.dimen.spacing_small), true)
        )
        licensesRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                updateTopBarScrollState()
            }
        })

        topBarController = EdgeToEdgeTopBarController(
            activity = this,
            rootForInsets = binding.root,
            toolbarForInsets = toolbar,
            topBarBackgroundView = toolbar,
            topBarDivider = topBarDivider,
            scrollPaddingView = licensesRecycler,
            isDarkMode = ::isDarkMode,
            isScrolled = { licensesRecycler.canScrollVertically(-1) }
        )
        topBarController.setup()

        binding.btnBack.setOnClickListener { finish() }

        viewModel.uiState.observe(this) { state ->
            when (state) {
                LicensesViewModel.UiState.Loading -> Unit
                LicensesViewModel.UiState.Error -> {
                    licensesRecycler.visibility = View.GONE
                    emptyState.visibility = View.VISIBLE
                    binding.txtLicensesEmptyMessage.text = getString(R.string.failed_to_load_licenses)
                }
                LicensesViewModel.UiState.Empty -> {
                    licensesRecycler.visibility = View.GONE
                    emptyState.visibility = View.VISIBLE
                    binding.txtLicensesEmptyMessage.text = getString(R.string.licenses_none_listed)
                }
                is LicensesViewModel.UiState.Content -> {
                    adapter.submitList(state.entries)
                    licensesRecycler.visibility = View.VISIBLE
                    emptyState.visibility = View.GONE
                    licensesRecycler.post { updateTopBarScrollState() }
                }
            }
        }
        viewModel.load()

        topBarController.bindScroll()
    }

    /**
     * Propagates scroll-derived elevation changes into [topBarController] without redundant updates.
     */
    private fun updateTopBarScrollState() {
        val shouldUseScrolledStyle = licensesRecycler.canScrollVertically(-1)
        if (isTopBarScrolledState != shouldUseScrolledStyle) {
            isTopBarScrolledState = shouldUseScrolledStyle
            topBarController.updateTopBarScrollState(isTopBarScrolledState)
        }
    }

    /**
     * @return `true` when night mode is active for status bar icon tinting inside [topBarController].
     */
    private fun isDarkMode(): Boolean {
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES
    }
}
