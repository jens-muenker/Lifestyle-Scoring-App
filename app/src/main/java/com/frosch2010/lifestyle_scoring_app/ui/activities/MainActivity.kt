package com.frosch2010.lifestyle_scoring_app.ui.activities

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.core.graphics.withClip
import com.frosch2010.lifestyle_scoring_app.R
import com.frosch2010.lifestyle_scoring_app.databinding.ActivityMainBinding
import com.frosch2010.lifestyle_scoring_app.ui.adapters.PlayerAdapter
import com.frosch2010.lifestyle_scoring_app.ui.decorations.AdaptiveSpacingItemDecoration
import com.frosch2010.lifestyle_scoring_app.ui.dialogs.DeletePlayerDialog
import com.frosch2010.lifestyle_scoring_app.ui.dialogs.PlayerNameDialog
import com.frosch2010.lifestyle_scoring_app.ui.util.EdgeToEdgeTopBarController
import com.frosch2010.lifestyle_scoring_app.ui.util.IntentExtras
import com.frosch2010.lifestyle_scoring_app.ui.viewmodels.MainViewModel
import com.frosch2010.lifestyle_scoring_app.ui.viewmodels.dto.PlayerDTO
import com.google.android.material.color.MaterialColors
import dagger.hilt.android.AndroidEntryPoint

/**
 * Primary roster screen: lists players with scores, supports swipe-to-delete, FAB add, and navigation into hands.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity(), PlayerAdapter.OnPlayerClickedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    /** Tracks recycler scroll position for coordinated top bar elevation. */
    private var isTopBarScrolledState = false

    private lateinit var topBarController: EdgeToEdgeTopBarController

    /** Bottom inset applied to empty state and FAB margins after window insets dispatch. */
    private var lastSystemBottomInset: Int = 0

    /**
     * Binds ViewModel, edge-to-edge chrome, RecyclerView decorations, and observers for roster events.
     *
     * @param savedInstanceState Restored bundle; roster state is reloaded from the repository via ViewModel.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        topBarController = EdgeToEdgeTopBarController(
            activity = this,
            rootForInsets = binding.root,
            toolbarForInsets = binding.toolbar,
            topBarBackgroundView = binding.toolbar,
            topBarDivider = binding.topBarDivider,
            scrollPaddingView = binding.recView,
            isDarkMode = ::isDarkMode,
            isScrolled = { binding.recView.canScrollVertically(-1) },
            extraInsetsApplier = { insets ->
                val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                lastSystemBottomInset = bars.bottom

                val initialEmptyStatePaddingStart = binding.emptyPlayersState.paddingStart
                val initialEmptyStatePaddingTop = binding.emptyPlayersState.paddingTop
                val initialEmptyStatePaddingEnd = binding.emptyPlayersState.paddingEnd
                val initialEmptyStatePaddingBottom = binding.emptyPlayersState.paddingBottom

                binding.emptyPlayersState.setPadding(
                    initialEmptyStatePaddingStart + bars.left,
                    initialEmptyStatePaddingTop,
                    initialEmptyStatePaddingEnd + bars.right,
                    initialEmptyStatePaddingBottom + bars.bottom
                )

                val initialFabMargin = resources.getDimensionPixelSize(R.dimen.fab_margin)
                val initialFabEndMargin =
                    (binding.fabAddPlayer.layoutParams as ViewGroup.MarginLayoutParams).marginEnd

                val layoutParams = binding.fabAddPlayer.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.bottomMargin = initialFabMargin + bars.bottom
                layoutParams.marginEnd = initialFabEndMargin + bars.right
                binding.fabAddPlayer.layoutParams = layoutParams
                updateRecyclerBottomPadding()
            }
        )
        topBarController.setup()

        setSupportActionBar(binding.toolbar)

        binding.btnBack.setOnClickListener {
            finish()
        }

        /**
          * RecyclerView adapter that renders and updates the corresponding list.
         */
        val adapter = PlayerAdapter(this, this, viewModel)
        binding.recView.layoutManager = LinearLayoutManager(this)
        binding.recView.adapter = adapter
        binding.recView.addItemDecoration(AdaptiveSpacingItemDecoration(resources.getDimensionPixelSize(R.dimen.spacing_small), true))
        attachSwipeGestures()
        binding.recView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                updateTopBarScrollState()
            }
        })
        binding.recView.post { updateTopBarScrollState() }
        binding.fabAddPlayer.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            updateRecyclerBottomPadding()
        }

        viewModel.uiState.observe(this) { uiState ->
            adapter.updateData(uiState.players)
            if (uiState.showEmptyState) {
                binding.emptyPlayersState.visibility = android.view.View.VISIBLE
                binding.fabAddPlayer.visibility = android.view.View.GONE
            } else {
                binding.emptyPlayersState.visibility = android.view.View.GONE
                binding.fabAddPlayer.visibility = android.view.View.VISIBLE
            }
        }
        viewModel.events.observe(this) { event ->
            when (event.consume()) {
                MainViewModel.UiEvent.ShowInitialAddPlayerDialog -> {
                    binding.root.post { showAddPlayerDialog() }
                }
                null -> Unit
            }
        }

        binding.fabAddPlayer.setOnClickListener {
            showAddPlayerDialog()
        }

        binding.btnAddPlayerInline.setOnClickListener {
            showAddPlayerDialog()
        }
    }

    /**
     * Refreshes derived scores when returning from downstream screens that mutate hands.
     */
    override fun onResume() {
        super.onResume()
        viewModel.recalculatePlayerPoints()
    }

    /**
     * @param index Adapter position / roster index forwarded as [IntentExtras.EXTRA_PLAYER_NUMBER].
     */
    override fun onPlayerClicked(index: Int) {
        val intent = Intent(this, PlayerCardsActivity::class.java)
        intent.putExtra(IntentExtras.EXTRA_PLAYER_NUMBER, index)
        startActivity(intent)
    }

    /**
     * Presents the add-player dialog and persists the entered name through [MainViewModel.addPlayer].
     */
    private fun showAddPlayerDialog() {
        val playerNameDialog = PlayerNameDialog(
            context = this,
            listener = object : PlayerNameDialog.OnNameEnteredListener {
                override fun onNameEntered(playerName: String) {
                    viewModel.addPlayer(PlayerDTO(playerName, 0))
                }
            },
            onDismiss = { viewModel.recalculatePlayerPoints() }
        )
        playerNameDialog.show()
    }

    /**
     * Opens rename dialog seeded with the current roster name at [playerIndex].
     *
     * @param playerIndex Roster slot to update after confirmation.
     */
    private fun showEditPlayerDialog(playerIndex: Int) {
        val currentName = viewModel.uiState.value?.players?.getOrNull(playerIndex)?.name ?: return
        val playerNameDialog = PlayerNameDialog(
            context = this,
            listener = object : PlayerNameDialog.OnNameEnteredListener {
                override fun onNameEntered(playerName: String) {
                    viewModel.editPlayerName(playerIndex, playerName)
                }
            },
            dialogTitleRes = R.string.edit_player_name,
            initialName = currentName,
            onDismiss = { viewModel.recalculatePlayerPoints() }
        )
        playerNameDialog.show()
    }

    /**
     * Confirms destructive removal for the player at [playerIndex] before mutating repository state.
     *
     * @param playerIndex Roster index passed through to [DeletePlayerDialog].
     */
    private fun showDeletePlayerDialog(playerIndex: Int) {
        DeletePlayerDialog(this,
            object : DeletePlayerDialog.IDeletePlayerDialogCallback {
                override fun onDeleteConfirmed(playerIndex: Int) {
                    viewModel.deletePlayer(playerIndex)
                }
            }
        ).show(
            getString(R.string.delete_player),
            getString(R.string.message_delete_player),
            playerIndex
        )
    }

    /**
     * Installs horizontal swipe actions: left delete confirmation, right rename dialog, with custom child draw artwork.
     */
    private fun attachSwipeGestures() {
        val swipeLabelTextSize = resources.getDimension(R.dimen.swipe_action_text_size)
        val swipeCornerRadius = resources.getDimension(R.dimen.swipe_action_corner_radius)
        val swipeVerticalInset = resources.getDimension(R.dimen.swipe_action_vertical_inset)

        val swipeLabelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = MaterialColors.getColor(binding.root, com.google.android.material.R.attr.colorOnTertiaryContainer)
            textSize = swipeLabelTextSize
            textAlign = Paint.Align.LEFT
        }
        val swipeBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)

        val swipeCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                if (position == RecyclerView.NO_POSITION) {
                    return
                }

                when (direction) {
                    ItemTouchHelper.LEFT -> showDeletePlayerDialog(position)
                    ItemTouchHelper.RIGHT -> showEditPlayerDialog(position)
                }

                binding.recView.adapter?.notifyItemChanged(position)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView

                if (dX > 0f) {
                    swipeBackgroundPaint.color = MaterialColors.getColor(binding.root, com.google.android.material.R.attr.colorTertiaryContainer)
                    swipeLabelPaint.color = MaterialColors.getColor(binding.root, com.google.android.material.R.attr.colorOnTertiaryContainer)
                    val background = RectF(
                        itemView.left.toFloat(),
                        itemView.top + swipeVerticalInset,
                        itemView.left + dX,
                        itemView.bottom - swipeVerticalInset
                    )
                    c.drawRoundRect(background, swipeCornerRadius, swipeCornerRadius, swipeBackgroundPaint)
                    drawSwipeIconAndLabel(
                        c = c,
                        background = background,
                        text = getString(R.string.edit_player_name),
                        iconRes = R.drawable.ic_edit,
                        textColor = swipeLabelPaint.color,
                        labelPaint = swipeLabelPaint,
                        alignToEnd = false
                    )
                } else if (dX < 0f) {
                    swipeBackgroundPaint.color = MaterialColors.getColor(binding.root, com.google.android.material.R.attr.colorErrorContainer)
                    swipeLabelPaint.color = MaterialColors.getColor(binding.root, com.google.android.material.R.attr.colorOnErrorContainer)
                    val background = RectF(
                        itemView.right + dX,
                        itemView.top + swipeVerticalInset,
                        itemView.right.toFloat(),
                        itemView.bottom - swipeVerticalInset
                    )
                    c.drawRoundRect(background, swipeCornerRadius, swipeCornerRadius, swipeBackgroundPaint)
                    drawSwipeIconAndLabel(
                        c = c,
                        background = background,
                        text = getString(R.string.delete),
                        iconRes = R.drawable.ic_google_delete_outline,
                        textColor = swipeLabelPaint.color,
                        labelPaint = swipeLabelPaint,
                        alignToEnd = true
                    )
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

        ItemTouchHelper(swipeCallback).attachToRecyclerView(binding.recView)
    }

    /**
     * Paints tinted icon + label pair centered vertically inside the swipe reveal [background] rect.
     *
     * @param c Canvas provided by [ItemTouchHelper] during swipe displacement.
     * @param background Rounded rectangle already filled with semantic swipe color.
     * @param text Localized swipe affordance label.
     * @param iconRes Vector drawable resource id tinted to [textColor].
     * @param textColor Resolved on-container color for both glyph and label.
     * @param labelPaint Paint configured with type size; color is overwritten per call.
     * @param alignToEnd When `true`, packs content toward the right edge (delete direction).
     */
    private fun drawSwipeIconAndLabel(
        c: Canvas,
        background: RectF,
        text: String,
        iconRes: Int,
        textColor: Int,
        labelPaint: Paint,
        alignToEnd: Boolean
    ) {
        val iconSize = resources.getDimensionPixelSize(R.dimen.swipe_action_icon_size)
        val horizontalPadding = resources.getDimension(R.dimen.spacing_small)
        val gapBetweenIconAndText = resources.getDimension(R.dimen.swipe_action_label_gap)

        labelPaint.color = textColor

        val textWidth = labelPaint.measureText(text)
        val totalWidth = iconSize + gapBetweenIconAndText + textWidth
        val maxLeft = (background.right - horizontalPadding - totalWidth).coerceAtLeast(background.left + horizontalPadding)
        val minLeft = background.left + horizontalPadding

        val iconLeft = if (alignToEnd) {
            maxLeft
        } else {
            minLeft
        }

        val centerY = (background.top + background.bottom) / 2f
        val iconTop = centerY - iconSize / 2f

        c.withClip(background) {
            val drawable = ContextCompat.getDrawable(this@MainActivity, iconRes)?.mutate()
            drawable?.setTint(textColor)

            drawable?.setBounds(
                iconLeft.toInt(),
                iconTop.toInt(),
                (iconLeft + iconSize).toInt(),
                (iconTop + iconSize).toInt()
            )
            drawable?.draw(this)

            val textStartX = iconLeft + iconSize + gapBetweenIconAndText
            val textY = centerY - ((labelPaint.descent() + labelPaint.ascent()) / 2f)
            drawText(text, textStartX, textY, labelPaint)
        }
    }

    /**
     * Forwards recycler scroll capability into [topBarController] to toggle elevated toolbar styling.
     */
    private fun updateTopBarScrollState() {
        val shouldUseScrolledStyle = binding.recView.canScrollVertically(-1)
        if (isTopBarScrolledState != shouldUseScrolledStyle) {
            isTopBarScrolledState = shouldUseScrolledStyle
            topBarController.updateTopBarScrollState(isTopBarScrolledState)
        }
    }

    /**
     * @return `true` when night mode is active for system bar icon contrast inside [topBarController].
     */
    private fun isDarkMode(): Boolean {
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES
    }

    /**
     * Keeps list content clear of the FAB and system navigation bar using the last known bottom inset.
     */
    private fun updateRecyclerBottomPadding() {
        val baseBottomPadding = resources.getDimensionPixelSize(R.dimen.main_list_bottom_padding)
        val fabHeight = binding.fabAddPlayer.height
        val targetBottom = maxOf(baseBottomPadding, fabHeight + lastSystemBottomInset + resources.getDimensionPixelSize(R.dimen.spacing_medium))
        binding.recView.setPadding(
            binding.recView.paddingLeft,
            binding.recView.paddingTop,
            binding.recView.paddingRight,
            targetBottom
        )
    }
}
